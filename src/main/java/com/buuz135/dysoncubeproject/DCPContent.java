package com.buuz135.dysoncubeproject;

import com.buuz135.dysoncubeproject.block.EMRailEjectorControllerBlock;
import com.buuz135.dysoncubeproject.block.MultiblockStructureBlock;
import com.buuz135.dysoncubeproject.block.RayReceiverControllerBlock;
import com.buuz135.dysoncubeproject.item.DysonComponentItem;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DCPContent {

    public static DeferredRegistryHelper REGISTRY = new DeferredRegistryHelper(DysonCubeProject.MODID);
    public static TitaniumTab TAB = new TitaniumTab(ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "main"));
    public static int CYAN_COLOR = 0xFF80E6E6;

    public static class Blocks {

        public static BlockWithTile MULTIBLOCK_STRUCTURE = REGISTRY.registerBlockWithTile("multiblock_structure", MultiblockStructureBlock::new, null);
        public static BlockWithTile EM_RAILEJECTOR_CONTROLLER = REGISTRY.registerBlockWithTile("em_railejector_controller", EMRailEjectorControllerBlock::new, TAB);
        public static BlockWithTile RAY_RECEIVER_CONTROLLER = REGISTRY.registerBlockWithTile("ray_receiver_controller", RayReceiverControllerBlock::new, TAB);

        public static void init() {
        }

    }

    public static class Items {

        public static DeferredHolder<Item, Item> SOLAR_SAIL = REGISTRY.registerGeneric(Registries.ITEM, "solar_sail", () -> new DysonComponentItem(1, 0, TAB));
        public static DeferredHolder<Item, Item> SOLAR_SAIL_PACKAGE = REGISTRY.registerGeneric(Registries.ITEM, "solar_sail_package", () -> new DysonComponentItem(8, 0, TAB));

        public static DeferredHolder<Item, Item> BEAM = REGISTRY.registerGeneric(Registries.ITEM, "beam", () -> new DysonComponentItem(0, 1, TAB));
        public static DeferredHolder<Item, Item> BEAM_PACKAGE = REGISTRY.registerGeneric(Registries.ITEM, "beam_package", () -> new DysonComponentItem(0, 4, TAB));


        public static void init() {
        }
    }

    public static class Sounds {

        public static DeferredHolder<SoundEvent, SoundEvent> RAILGUN = REGISTRY.registerGeneric(Registries.SOUND_EVENT, "railgun", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "railgun")));
        public static DeferredHolder<SoundEvent, SoundEvent> RAY = REGISTRY.registerGeneric(Registries.SOUND_EVENT, "ray", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "ray")));



        public static void init() {
        }
    }


}
