package com.buuz135.dysoncubeproject.block.tile;

import com.buuz135.dysoncubeproject.Config;
import com.buuz135.dysoncubeproject.DCPAttachments;
import com.buuz135.dysoncubeproject.DCPContent;
import com.buuz135.dysoncubeproject.client.gui.DysonProgressGuiAddon;
import com.buuz135.dysoncubeproject.client.gui.SubscribeDysonGuiAddon;
import com.buuz135.dysoncubeproject.world.DysonSphereStructure;
import com.buuz135.dysoncubeproject.world.DysonSphereProgressSavedData;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.api.client.IScreenAddonProvider;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.BasicTile;
import com.hrznstudio.titanium.block.tile.ITickableBlockEntity;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.client.screen.asset.IHasAssetProvider;
import com.hrznstudio.titanium.component.IComponentHarness;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import com.hrznstudio.titanium.container.BasicAddonContainer;
import com.hrznstudio.titanium.container.addon.IContainerAddon;
import com.hrznstudio.titanium.container.addon.IContainerAddonProvider;
import com.hrznstudio.titanium.network.IButtonHandler;
import com.hrznstudio.titanium.network.locator.LocatorFactory;
import com.hrznstudio.titanium.network.locator.instance.TileEntityLocatorInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class EMRailEjectorBlockEntity extends BasicTile<EMRailEjectorBlockEntity> implements IScreenAddonProvider, ITickableBlockEntity<EMRailEjectorBlockEntity>, MenuProvider, IButtonHandler, IContainerAddonProvider, IHasAssetProvider, IComponentHarness {

    @Save
    private float currentYaw, currentPitch, targetYaw, targetPitch;
    @Save
    private long lastExecution;
    @Save
    private ProgressBarComponent<EMRailEjectorBlockEntity> progressBarComponent;
    @Save
    private InventoryComponent<EMRailEjectorBlockEntity> input;
    @Save
    private EnergyStorageComponent<EMRailEjectorBlockEntity> power;
    @Save
    private String dysonSphereId;
    @Save
    private int rampupAmount;

    private int cooldown;

    public EMRailEjectorBlockEntity(BasicTileBlock<EMRailEjectorBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.progressBarComponent = new ProgressBarComponent<EMRailEjectorBlockEntity>(45, 21, 120)
                .setCanIncrease(iComponentHarness -> this.canIncrease()).setOnTickWork(() -> {
            syncObject(this.progressBarComponent);
                })
                .setOnFinishWork(this::onFinishWork)
                .setIncreaseType(true)
                .setComponentHarness(this)
                .setBarDirection(ProgressBarComponent.BarDirection.VERTICAL_UP)
                .setColor(DyeColor.CYAN)
                .setOnTickWork(this::onTickWork);
        this.input = new InventoryComponent<EMRailEjectorBlockEntity>("input", 7, 42, 1)
                .setInputFilter((itemStack, integer) -> itemStack.getOrDefault(DCPAttachments.SOLAR_SAIL, 0) > 0 || itemStack.getOrDefault(DCPAttachments.BEAM, 0) > 0)
                .setSlotToColorRender(0, DyeColor.CYAN);
        this.power = new EnergyStorageComponent<>(Config.RAIL_EJECTOR_POWER_BUFFER, Config.RAIL_EJECTOR_POWER_BUFFER, 0, 26, 21);
        this.currentYaw = 180;
        this.currentPitch = 90;
        this.targetYaw = 180; //HORIZONTAL
        this.targetPitch = 90; //VERTICAL
        this.lastExecution = 0;
        this.dysonSphereId = "";
        this.cooldown = 0;
        this.rampupAmount = 0;
    }

    private boolean canIncrease() {
        if (this.cooldown > 0) return false;
        if (this.input.getStackInSlot(0).isEmpty()) return false;
        if (this.getLevel().isRaining() || this.getLevel().isNight() || !this.getLevel().canSeeSky(this.getBlockPos().above()))
            return false;
        var time = level.getTimeOfDay(1f) * 360f;
        if (time <= 10 || time >= 360 - 10) {
            return false;
        }
        var dyson = DysonSphereProgressSavedData.get(this.level).getSpheres().computeIfAbsent(this.dysonSphereId, s -> new DysonSphereStructure());
        if (dyson.getProgress() >= 1) return false;
        var solarPanels = this.input.getStackInSlot(0).getOrDefault(DCPAttachments.SOLAR_SAIL, 0);
        var beams = this.input.getStackInSlot(0).getOrDefault(DCPAttachments.BEAM, 0);
        if (solarPanels > 0 && (dyson.getSolarPanels() + solarPanels) >= dyson.getMaxSolarPanels()) return false;
        if (beams > 0 && dyson.getBeams() >= dyson.getMaxBeams()) return false;
        if (this.rampupAmount > 1 && this.getPower().getEnergyStored() < (Math.pow(this.rampupAmount, 2) * Config.RAIL_EJECTOR_CONSUME)) {
            this.rampupAmount = 1;
            return false;
        }

        return true;
    }

    private void onTickWork() {
        this.power.setEnergyStored((int) Math.max(0, this.power.getEnergyStored() - (Math.pow(this.rampupAmount, 2) * Config.RAIL_EJECTOR_CONSUME)));
    }

    private void onFinishWork() {
        var data = DysonSphereProgressSavedData.get(this.level);
        var dyson = data.getSpheres().computeIfAbsent(this.dysonSphereId, s -> new DysonSphereStructure());
        boolean reset = false;
        for (int i = 0; i < this.rampupAmount; i++) {
            if (!this.input.getStackInSlot(0).isEmpty()) {
                var solarPanels = this.input.getStackInSlot(0).getOrDefault(DCPAttachments.SOLAR_SAIL, 0);
                var beams = this.input.getStackInSlot(0).getOrDefault(DCPAttachments.BEAM, 0);
                this.input.getStackInSlot(0).shrink(1);
                dyson.increaseBeams(beams);
                dyson.increaseSolarPanels(solarPanels);
            } else {
                reset = true;
            }
        }
        this.lastExecution = this.getLevel().getGameTime();
        this.cooldown = 30;

        data.setDirty();
        syncObject(this.lastExecution);
        if (reset) {
            this.rampupAmount = 1;
        } else {
            this.rampupAmount = Math.min(this.rampupAmount + 1, 64);
        }
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, EMRailEjectorBlockEntity blockEntity) {
        if (progressBarComponent.getCanIncrease().test(progressBarComponent.getComponentHarness())) {
            if (progressBarComponent.getIncreaseType() && progressBarComponent.getProgress() == 0) {
                progressBarComponent.onStart();
            }

            if (!progressBarComponent.getIncreaseType() && progressBarComponent.getProgress() == progressBarComponent.getMaxProgress()) {
                progressBarComponent.onStart();
            }

            progressBarComponent.tickBar();


        } else if (progressBarComponent.getCanReset().test(progressBarComponent.getComponentHarness())) {
            progressBarComponent.setProgress(progressBarComponent.getIncreaseType() ? 0 : progressBarComponent.getMaxProgress());
        }

        if (this.cooldown > 0) this.cooldown--;

        this.targetPitch = level.getTimeOfDay(1f) * 360f;
        //this.targetPitch = 300;
        if (this.targetPitch <= 10) {
            this.targetPitch = 10;
        }

        if (this.targetPitch >= 360 - 10) {
            this.targetPitch = 10;
        }


        if (this.targetPitch <= 90) {
            this.targetYaw = 0;
        } else {
            this.targetYaw = 180;
        }

        if (this.targetPitch >= 90 && this.targetPitch <= 270) {
            this.targetPitch = 90;
        }

        if (this.targetPitch >= 360 - 90) {
            this.targetPitch = 360 - this.targetPitch;
        }

        if (level.isRaining()) {
            this.targetPitch = 90;
        }

        // Move currentPitch towards targetPitch by 1 each tick
        if (this.currentPitch <= this.targetPitch) {
            this.currentPitch = Math.min(this.currentPitch + 1, this.targetPitch);
        } else if (this.currentPitch > this.targetPitch) {
            this.currentPitch = Math.max(this.currentPitch - 1, this.targetPitch);
        }
        if (this.currentYaw <= this.targetYaw) {
            this.currentYaw = Math.min(this.currentYaw + 1, this.targetYaw);
        } else if (this.currentYaw > this.targetYaw) {
            this.currentYaw = Math.max(this.currentYaw - 1, this.targetYaw);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void clientTick(Level level, BlockPos pos, BlockState state, EMRailEjectorBlockEntity blockEntity) {
        if (level instanceof ClientLevel clientLevel && progressBarComponent.getProgress() == 7) {
            Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(DCPContent.Sounds.RAILGUN.get(), SoundSource.BLOCKS, 1, 1, level.getRandom(), pos.getX(), pos.getY(), pos.getZ()));
        }
    }

    @Override
    public ItemInteractionResult onActivated(Player player, InteractionHand hand, Direction facing, double hitX, double hitY, double hitZ) {
        openGui(player);
        return ItemInteractionResult.SUCCESS;
    }

    public void openGui(Player player) {
        if (player instanceof ServerPlayer sp) {
            sp.openMenu(this, (buffer) -> LocatorFactory.writePacketBuffer(buffer, new TileEntityLocatorInstance(this.worldPosition)));
        }
    }

    public float getCurrentPitch() {
        return currentPitch;
    }

    public float getCurrentYaw() {
        return currentYaw;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public @NotNull List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        List<IFactory<? extends IScreenAddon>> list = new ArrayList<>();
        list.addAll(this.input.getScreenAddons());
        list.add(() -> new DysonProgressGuiAddon(this.dysonSphereId, 62, 24));
        list.add(() -> new SubscribeDysonGuiAddon(this.dysonSphereId, 9, 24 + 60));
        list.addAll(this.power.getScreenAddons());
        list.addAll(this.progressBarComponent.getScreenAddons());

        return list;
    }

    @Override
    public IAssetProvider getAssetProvider() {
        return IAssetProvider.DEFAULT_PROVIDER;
    }

    @Override
    public @NotNull List<IFactory<? extends IContainerAddon>> getContainerAddons() {
        var list = new ArrayList<IFactory<? extends IContainerAddon>>();
        list.addAll(this.progressBarComponent.getContainerAddons());
        list.addAll(this.input.getContainerAddons());
        list.addAll(this.power.getContainerAddons());
        return list;
    }

    @Override
    public void handleButtonMessage(int i, Player player, CompoundTag compoundTag) {

    }

    @Nullable
    public AbstractContainerMenu createMenu(int menu, Inventory inventoryPlayer, Player entityPlayer) {
        return new BasicAddonContainer(this, new TileEntityLocatorInstance(this.worldPosition), this.getWorldPosCallable(), inventoryPlayer, menu);
    }

    @Nonnull
    public Component getDisplayName() {
        return Component.translatable(this.getBasicTileBlock().getDescriptionId()).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY));
    }

    public ContainerLevelAccess getWorldPosCallable() {
        return this.getLevel() != null ? ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()) : ContainerLevelAccess.NULL;
    }

    @Override
    public Level getComponentWorld() {
        return this.level;
    }

    @Override
    public void markComponentForUpdate(boolean b) {
        this.markForUpdate();
    }

    @Override
    public void markComponentDirty() {
        this.markForUpdate();
    }

    public ProgressBarComponent<EMRailEjectorBlockEntity> getProgressBarComponent() {
        return progressBarComponent;
    }

    public long getLastExecution() {
        return lastExecution;
    }

    public String getDysonSphereId() {
        return dysonSphereId;
    }

    public void setDysonSphereId(String dysonSphereId) {
        this.dysonSphereId = dysonSphereId;
    }

    public InventoryComponent<EMRailEjectorBlockEntity> getInput() {
        return input;
    }

    public EnergyStorageComponent<EMRailEjectorBlockEntity> getPower() {
        return power;
    }
}
