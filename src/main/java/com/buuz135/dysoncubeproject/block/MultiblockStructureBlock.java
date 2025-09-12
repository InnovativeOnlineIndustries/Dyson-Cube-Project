package com.buuz135.dysoncubeproject.block;

import com.buuz135.dysoncubeproject.DCPContent;
import com.buuz135.dysoncubeproject.block.tile.MultiblockStructureBlockEntity;
import com.hrznstudio.titanium.block.BasicTileBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class MultiblockStructureBlock extends BasicTileBlock<MultiblockStructureBlockEntity> {

    public MultiblockStructureBlock() {
        super(Properties.ofFullCopy(Blocks.IRON_BLOCK), MultiblockStructureBlockEntity.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (blockPos, blockState) ->
                new MultiblockStructureBlockEntity((BasicTileBlock<MultiblockStructureBlockEntity>) DCPContent.Blocks.MULTIBLOCK_STRUCTURE.block().get(), DCPContent.Blocks.MULTIBLOCK_STRUCTURE.type().get(), blockPos, blockState);
    }
}
