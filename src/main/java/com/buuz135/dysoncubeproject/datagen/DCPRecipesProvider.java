package com.buuz135.dysoncubeproject.datagen;


import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;

import net.minecraft.world.level.block.Block;

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

    }
}
