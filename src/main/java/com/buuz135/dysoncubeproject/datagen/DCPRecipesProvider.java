package com.buuz135.dysoncubeproject.datagen;


import com.buuz135.dysoncubeproject.DCPContent;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import java.util.function.Supplier;

public class DCPRecipesProvider extends RecipeProvider {

    private final Supplier<List<Block>> blocksToProcess;
    private CompletableFuture<HolderLookup.Provider> registries;

    public DCPRecipesProvider(DataGenerator generatorIn, Supplier<List<Block>> blocksToProcess, CompletableFuture<HolderLookup.Provider> registries) {
        super(generatorIn.getPackOutput(), registries);
        this.blocksToProcess = blocksToProcess;
        this.registries = registries;
    }

    @Override
    public void buildRecipes(RecipeOutput consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(DCPContent.Blocks.EM_RAILEJECTOR_CONTROLLER.block().get())
                .pattern("DRB")
                .pattern("RCB")
                .pattern("SSS")
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('B', DCPContent.Items.BEAM.get())
                .define('C', Tags.Items.STORAGE_BLOCKS_COPPER)
                .define('S', Items.SMOOTH_STONE_SLAB)
                .save(consumer);
        TitaniumShapedRecipeBuilder.shapedRecipe(DCPContent.Items.SOLAR_SAIL.get())
                .pattern("GCG")
                .pattern("GCG")
                .pattern("LCL")
                .define('G', Tags.Items.GLASS_PANES_COLORLESS)
                .define('C', Tags.Items.INGOTS_COPPER)
                .define('L', Tags.Items.GEMS_LAPIS)
                .save(consumer);
        TitaniumShapedRecipeBuilder.shapedRecipe(DCPContent.Items.SOLAR_SAIL_PACKAGE.get())
                .pattern("GGG")
                .pattern("GIG")
                .pattern("GGG")
                .define('G', DCPContent.Items.SOLAR_SAIL.get())
                .define('I', Tags.Items.STORAGE_BLOCKS_IRON)
                .save(consumer);
        TitaniumShapedRecipeBuilder.shapedRecipe(DCPContent.Items.BEAM.get(), 2)
                .pattern("NIN")
                .pattern("BIB")
                .pattern("NIN")
                .define('N', Tags.Items.NUGGETS_IRON)
                .define('I', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('B', Items.IRON_BARS)
                .save(consumer);
        TitaniumShapedRecipeBuilder.shapedRecipe(DCPContent.Items.BEAM_PACKAGE.get())
                .pattern(" G ")
                .pattern("GIG")
                .pattern(" G ")
                .define('G', DCPContent.Items.BEAM.get())
                .define('I', Tags.Items.STORAGE_BLOCKS_COPPER)
                .save(consumer);
        TitaniumShapedRecipeBuilder.shapedRecipe(DCPContent.Blocks.RAY_RECEIVER_CONTROLLER.asItem())
                .pattern("SSS")
                .pattern("NBN")
                .pattern("III")
                .define('S', DCPContent.Items.SOLAR_SAIL.get())
                .define('N', Items.SMOOTH_STONE_SLAB)
                .define('I', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('B', DCPContent.Items.BEAM.get())
                .save(consumer);
    }
}
