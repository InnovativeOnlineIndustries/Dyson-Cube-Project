package com.buuz135.dysoncubeproject.block;

import com.buuz135.dysoncubeproject.DCPContent;
import com.buuz135.dysoncubeproject.block.tile.MultiblockStructureBlockEntity;
import com.hrznstudio.titanium.block.BasicTileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MultiblockStructureBlock extends BasicTileBlock<MultiblockStructureBlockEntity> {

    public MultiblockStructureBlock() {
        super(Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion().noLootTable(), MultiblockStructureBlockEntity.class);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (blockPos, blockState) ->
                new MultiblockStructureBlockEntity((BasicTileBlock<MultiblockStructureBlockEntity>) DCPContent.Blocks.MULTIBLOCK_STRUCTURE.block().get(), DCPContent.Blocks.MULTIBLOCK_STRUCTURE.type().get(), blockPos, blockState);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context);
    }

    public static void createStructure(Level level, BlockPos controllerPos, BlockPos at) {
        level.setBlockAndUpdate(at, DCPContent.Blocks.MULTIBLOCK_STRUCTURE.getBlock().defaultBlockState());
        if (level.getBlockEntity(at) instanceof MultiblockStructureBlockEntity blockEntity) {
            blockEntity.setControllerPos(controllerPos);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (worldIn.getBlockEntity(pos) instanceof MultiblockStructureBlockEntity blockEntity) {
            var controllerPos = blockEntity.getControllerPos();
            if (controllerPos != null && worldIn.getBlockState(controllerPos).getBlock() instanceof DefaultMultiblockControllerBlock<?>) {
                worldIn.setBlockAndUpdate(controllerPos, Blocks.AIR.defaultBlockState());
            }
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }
}
