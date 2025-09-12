package com.buuz135.dysoncubeproject;

import com.buuz135.dysoncubeproject.client.ClientSetup;
import com.hrznstudio.titanium.module.ModuleController;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
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
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;


@Mod(DysonCubeProject.MODID)
public class DysonCubeProject extends ModuleController {

    public static final String MODID = "dysoncubeproject";
    private static final Logger LOGGER = LogUtils.getLogger();

    public DysonCubeProject(Dist dist, IEventBus modEventBus, ModContainer modContainer) {
        super(modContainer);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        if (dist == Dist.CLIENT) ClientSetup.init();
    }


    @Override
    protected void initModules() {
        addCreativeTab("main", () -> new ItemStack(Blocks.DIRT), "dyson_cube_project", DCPContent.TAB);
        DCPContent.Blocks.init();
    }
}
