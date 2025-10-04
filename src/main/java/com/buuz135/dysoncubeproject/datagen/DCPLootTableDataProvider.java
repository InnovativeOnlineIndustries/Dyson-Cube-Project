package com.buuz135.dysoncubeproject.datagen;


import com.buuz135.dysoncubeproject.DCPContent;
import com.hrznstudio.titanium.datagenerator.loot.TitaniumLootTableProvider;
import com.hrznstudio.titanium.datagenerator.loot.block.BasicBlockLootTables;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class DCPLootTableDataProvider extends TitaniumLootTableProvider {

    private final Supplier<List<Block>> blocksToProcess;

    public DCPLootTableDataProvider(DataGenerator dataGenerator, Supplier<List<Block>> blocks, CompletableFuture<HolderLookup.Provider> providerCompletableFuture) {
        super(dataGenerator, blocks, providerCompletableFuture);
        this.blocksToProcess = blocks;
    }

    @Override
    protected BasicBlockLootTables createBlockLootTables(HolderLookup.Provider prov) {
        return new BasicBlockLootTables(this.blocksToProcess, prov) {
            @Override
            protected void generate() {
                super.generate();
            }
        };
    }
}
