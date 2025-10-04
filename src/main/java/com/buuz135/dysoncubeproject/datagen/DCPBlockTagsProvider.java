package com.buuz135.dysoncubeproject.datagen;


import com.buuz135.dysoncubeproject.DCPContent;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
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
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(DCPContent.Blocks.EM_RAILEJECTOR_CONTROLLER.block().get(), DCPContent.Blocks.RAY_RECEIVER_CONTROLLER.block().get(), DCPContent.Blocks.MULTIBLOCK_STRUCTURE.block().get());
    }
}
