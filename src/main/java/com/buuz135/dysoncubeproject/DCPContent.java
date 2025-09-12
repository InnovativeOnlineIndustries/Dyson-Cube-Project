package com.buuz135.dysoncubeproject;

import com.buuz135.dysoncubeproject.block.EMRailEjectorControllerBlock;
import com.buuz135.dysoncubeproject.block.MultiblockStructureBlock;
import com.hrznstudio.titanium.module.BlockWithTile;
import com.hrznstudio.titanium.module.DeferredRegistryHelper;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.resources.ResourceLocation;

public class DCPContent {

    public static DeferredRegistryHelper REGISTRY = new DeferredRegistryHelper(DysonCubeProject.MODID);
    public static TitaniumTab TAB = new TitaniumTab(ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "main"));

    public static class Blocks {

        public static BlockWithTile MULTIBLOCK_STRUCTURE = REGISTRY.registerBlockWithTile("multiblock_structure", MultiblockStructureBlock::new, TAB);
        public static BlockWithTile EM_RAILEJECTOR_CONTROLLER = REGISTRY.registerBlockWithTile("em_railejector_controller", EMRailEjectorControllerBlock::new, TAB);

        public static void init() {
        }

    }


}
