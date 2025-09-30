package com.buuz135.dysoncubeproject.datagen;


import com.buuz135.dysoncubeproject.DCPContent;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;

public class DCPBlockstateProvider extends BlockStateProvider {


    public DCPBlockstateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen.getPackOutput(), modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(DCPContent.Blocks.MULTIBLOCK_STRUCTURE.block().get(), getUncheckedModel(DCPContent.Blocks.MULTIBLOCK_STRUCTURE.block().get()));
        simpleBlock(DCPContent.Blocks.EM_RAILEJECTOR_CONTROLLER.block().get(), getUncheckedModel(DCPContent.Blocks.EM_RAILEJECTOR_CONTROLLER.block().get()));
        simpleBlock(DCPContent.Blocks.RAY_RECEIVER_CONTROLLER.block().get(), getUncheckedModel(DCPContent.Blocks.RAY_RECEIVER_CONTROLLER.block().get()));
    }

    public static ModelFile.UncheckedModelFile getUncheckedModel(Block block) {
        return new ModelFile.UncheckedModelFile(getModel(block));
    }

    public static ResourceLocation getModel(Block block) {
        return ResourceLocation.fromNamespaceAndPath(BuiltInRegistries.BLOCK.getKey(block).getNamespace(), "block/" + BuiltInRegistries.BLOCK.getKey(block).getPath());
    }
}
