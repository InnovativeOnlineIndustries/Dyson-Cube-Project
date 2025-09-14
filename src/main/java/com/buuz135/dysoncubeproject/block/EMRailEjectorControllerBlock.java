package com.buuz135.dysoncubeproject.block;

import com.buuz135.dysoncubeproject.DCPContent;
import com.buuz135.dysoncubeproject.block.tile.EMRailEjectorBlockEntity;
import com.buuz135.dysoncubeproject.block.tile.MultiblockStructureBlockEntity;
import com.buuz135.dysoncubeproject.multiblock.MultiblockStructure;
import com.buuz135.dysoncubeproject.world.DysonSphereConfiguration;
import com.buuz135.dysoncubeproject.world.DysonSphereProgressSavedData;
import com.hrznstudio.titanium.block.BasicTileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

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

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level instanceof ServerLevel) {
            if (placer != null) {
                var dyson = DysonSphereProgressSavedData.get(level);
                dyson.getSpheres().computeIfAbsent(placer.getStringUUID(), s -> new DysonSphereConfiguration());
                dyson.setDirty();
            }
            var lowerCorner = pos.offset(-1, 0, -1);
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    var updatedAt = lowerCorner.offset(x, 0, z);
                    if (!pos.equals(updatedAt)) {
                        MultiblockStructureBlock.createStructure(level, pos, updatedAt);
                    }
                }
            }
            MultiblockStructureBlock.createStructure(level, pos, pos.above());
            MultiblockStructureBlock.createStructure(level, pos, pos.above(2));
        }
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
        if (level instanceof ServerLevel) {
            var lowerCorner = pos.offset(-1, 0, -1);
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    var updatedAt = lowerCorner.offset(x, 0, z);
                    if (!pos.equals(updatedAt)) {
                        level.setBlockAndUpdate(updatedAt, Blocks.AIR.defaultBlockState());
                    }
                }
            }
            level.setBlockAndUpdate(pos.above(), Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos.above(2), Blocks.AIR.defaultBlockState());
        }
    }
}
