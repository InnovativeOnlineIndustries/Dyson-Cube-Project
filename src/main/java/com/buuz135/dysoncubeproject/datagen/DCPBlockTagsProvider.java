package com.buuz135.dysoncubeproject.datagen;


import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DCPBlockTagsProvider extends BlockTagsProvider {


    public DCPBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);

    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        //this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add();
    }
}
