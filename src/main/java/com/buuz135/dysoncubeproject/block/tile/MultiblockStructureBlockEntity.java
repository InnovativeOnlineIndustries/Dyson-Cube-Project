package com.buuz135.dysoncubeproject.block.tile;

import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.BasicTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MultiblockStructureBlockEntity extends BasicTile<MultiblockStructureBlockEntity> {

    public MultiblockStructureBlockEntity(BasicTileBlock<MultiblockStructureBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
    }
}
