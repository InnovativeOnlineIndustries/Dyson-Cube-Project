package com.buuz135.dysoncubeproject.block.tile;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.api.client.IScreenAddonProvider;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.BasicTile;
import com.hrznstudio.titanium.block.tile.ITickableBlockEntity;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.client.screen.asset.IHasAssetProvider;
import com.hrznstudio.titanium.component.sideness.IFacingComponentHarness;
import com.hrznstudio.titanium.container.BasicAddonContainer;
import com.hrznstudio.titanium.container.addon.IContainerAddon;
import com.hrznstudio.titanium.container.addon.IContainerAddonProvider;
import com.hrznstudio.titanium.network.IButtonHandler;
import com.hrznstudio.titanium.network.locator.LocatorFactory;
import com.hrznstudio.titanium.network.locator.instance.TileEntityLocatorInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class EMRailEjectorBlockEntity extends BasicTile<EMRailEjectorBlockEntity> implements IScreenAddonProvider, ITickableBlockEntity<EMRailEjectorBlockEntity>, MenuProvider, IButtonHandler, IContainerAddonProvider, IHasAssetProvider {

    @Save
    private float currentYaw, currentPitch, targetYaw, targetPitch;

    public EMRailEjectorBlockEntity(BasicTileBlock<EMRailEjectorBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.currentYaw = 0;
        this.currentPitch = 0;
        this.targetYaw = 0; //HORIZONTAL
        this.targetPitch = 0; //VERTICAL
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, EMRailEjectorBlockEntity blockEntity) {

    }

    @Override
    public void clientTick(Level level, BlockPos pos, BlockState state, EMRailEjectorBlockEntity blockEntity) {
        this.targetPitch = level.getTimeOfDay(1f) * 360f;
        //this.targetPitch = 300;
        if (this.targetPitch <= 20) {
            this.targetPitch = 20;
        }

        if (this.targetPitch >= 360 - 20) {
            this.targetPitch = 20;
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

    @Override
    public ItemInteractionResult onActivated(Player player, InteractionHand hand, Direction facing, double hitX, double hitY, double hitZ) {
        openGui(player);
        return super.onActivated(player, hand, facing, hitX, hitY, hitZ);
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

    @Override
    public @NotNull List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        return List.of();
    }

    @Override
    public IAssetProvider getAssetProvider() {
        return IAssetProvider.DEFAULT_PROVIDER;
    }

    @Override
    public @NotNull List<IFactory<? extends IContainerAddon>> getContainerAddons() {
        return List.of();
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
}
