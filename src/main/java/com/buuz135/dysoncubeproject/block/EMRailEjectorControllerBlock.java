package com.buuz135.dysoncubeproject.block;

import com.buuz135.dysoncubeproject.DCPContent;
import com.buuz135.dysoncubeproject.block.tile.EMRailEjectorBlockEntity;
import com.buuz135.dysoncubeproject.block.tile.MultiblockStructureBlockEntity;
import com.buuz135.dysoncubeproject.multiblock.MultiblockStructure;
import com.hrznstudio.titanium.block.BasicTileBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class EMRailEjectorControllerBlock extends DefaultMultiblockControllerBlock<EMRailEjectorBlockEntity> {

    public static MultiblockStructure MULTIBLOCK_STRUCTURE = new MultiblockStructure(3, 3, 3);

    public EMRailEjectorControllerBlock() {
        super("em_railejector_controller", Properties.ofFullCopy(Blocks.IRON_BLOCK), EMRailEjectorBlockEntity.class);
    }

    @Override
    public MultiblockStructure getMultiblockStructure() {
        return MULTIBLOCK_STRUCTURE;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (blockPos, blockState) ->
                new EMRailEjectorBlockEntity((BasicTileBlock<EMRailEjectorBlockEntity>) DCPContent.Blocks.EM_RAILEJECTOR_CONTROLLER.getBlock(), DCPContent.Blocks.EM_RAILEJECTOR_CONTROLLER.type().get(), blockPos, blockState);
    }
}
