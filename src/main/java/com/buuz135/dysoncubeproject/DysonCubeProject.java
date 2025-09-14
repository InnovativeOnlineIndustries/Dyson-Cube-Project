package com.buuz135.dysoncubeproject;

import com.buuz135.dysoncubeproject.client.ClientSetup;
import com.buuz135.dysoncubeproject.datagen.DCPBlockstateProvider;
import com.buuz135.dysoncubeproject.network.DysonSphereSyncMessage;
import com.buuz135.dysoncubeproject.world.DysonSphereProgressSavedData;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.ModuleController;
import com.hrznstudio.titanium.network.NetworkHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;


@Mod(DysonCubeProject.MODID)
public class DysonCubeProject extends ModuleController {

    public static final String MODID = "dysoncubeproject";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static NetworkHandler NETWORK = new NetworkHandler(MODID);

    public DysonCubeProject(Dist dist, IEventBus modEventBus, ModContainer modContainer) {
        super(modContainer);
        NETWORK.registerMessage("dyson_sphere_sync", DysonSphereSyncMessage.class);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        if (dist == Dist.CLIENT) ClientSetup.init();

        EventManager.forge(LevelTickEvent.Post.class).process(post -> {
            if (post.getLevel() instanceof ServerLevel serverLevel && post.getLevel().getGameTime() % 100 == 0) {
                var packet = new DysonSphereSyncMessage(DysonSphereProgressSavedData.get(serverLevel).save(new CompoundTag(), serverLevel.getServer().registryAccess()));
                for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
                    NETWORK.sendTo(packet, player);
                }
            }
        }).subscribe();
    }


    @Override
    protected void initModules() {
        addCreativeTab("main", () -> new ItemStack(Blocks.DIRT), "dyson_cube_project", DCPContent.TAB);
        DCPContent.Blocks.init();
    }

    @Override
    public void addDataProvider(GatherDataEvent event) {
        super.addDataProvider(event);
        event.addProvider(new DCPBlockstateProvider(event.getGenerator(), MODID, event.getExistingFileHelper()));
    }
}
