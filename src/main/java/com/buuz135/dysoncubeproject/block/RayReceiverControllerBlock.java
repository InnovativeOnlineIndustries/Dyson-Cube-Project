package com.buuz135.dysoncubeproject.block;

import com.buuz135.dysoncubeproject.DCPContent;
import com.buuz135.dysoncubeproject.block.tile.RayReceiverBlockEntity;
import com.buuz135.dysoncubeproject.multiblock.MultiblockStructure;
import com.buuz135.dysoncubeproject.world.DysonSphereConfiguration;
import com.buuz135.dysoncubeproject.world.DysonSphereProgressSavedData;
import com.hrznstudio.titanium.block.BasicTileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class RayReceiverControllerBlock extends DefaultMultiblockControllerBlock<RayReceiverBlockEntity> {

    public static VoxelShape SHAPE = Stream.of(
            Block.box(0, 0, 0, 16, 32, 16),
            Block.box(0, 0, -12, 16, 10, 0),
            Block.box(0, 0, 16, 16, 10, 28),
            Block.box(-16, 0, 0, 0, 4, 16),
            Block.box(16, 0, 0, 32, 4, 16),
            Block.box(-16, 4, 0, -8, 10, 16),
            Block.box(24, 4, 0, 32, 10, 16),

            //TOP PART
            Block.box(-16, 32 + 0, -16, 32, 32 + 4, 32),
            Block.box(-16, 32 + 4, -16, 32, 32 + 8, -13),
            Block.box(-16, 32 + 4, 29, 32, 32 + 8, 32),
            Block.box(29, 32 + 4, -13, 32, 32 + 8, 29),
            Block.box(-16, 32 + 4, -13, -13, 32 + 8, 29),
            Block.box(0, 32 + 4, 0, 16, 32 + 12, 16),
            Block.box(4, 32 + 12, 4, 12, 32 + 28, 12),
            Block.box(-2, 32 + 27, -2, 18, 32 + 32, 18)

    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape CONTROLLER_LOCAL_SHAPE = Shapes.join(SHAPE, Block.box(0, 1, 0, 16, 16, 16), BooleanOp.AND);
    public static MultiblockStructure MULTIBLOCK_STRUCTURE = new MultiblockStructure(3, 6, 3, SHAPE);

    public RayReceiverControllerBlock() {
        super("ray_receiver_controller", Properties.ofFullCopy(Blocks.IRON_BLOCK), RayReceiverBlockEntity.class);
    }

    @Override
    public MultiblockStructure getMultiblockStructure() {
        return MULTIBLOCK_STRUCTURE;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (blockPos, blockState) ->
                new RayReceiverBlockEntity((BasicTileBlock<RayReceiverBlockEntity>) DCPContent.Blocks.RAY_RECEIVER_CONTROLLER.getBlock(), DCPContent.Blocks.RAY_RECEIVER_CONTROLLER.type().get(), blockPos, blockState);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level instanceof ServerLevel serverLevel) {
            if (placer != null) {
                var dyson = DysonSphereProgressSavedData.get(level);
                var subscribedSphere = dyson.getSubscribedFor(placer.getStringUUID());
                dyson.getSpheres().computeIfAbsent(subscribedSphere, s -> new DysonSphereConfiguration());
                dyson.setDirty();
                if (serverLevel.getBlockEntity(pos) instanceof RayReceiverBlockEntity blockEntity) {
                    blockEntity.setDysonSphereId(subscribedSphere);
                }
                dyson.setDirty();
            }
            MultiblockStructureBlock.createStructure(level, pos, pos.offset(-1, 0, 0));
            MultiblockStructureBlock.createStructure(level, pos, pos.offset(1, 0, 0));
            MultiblockStructureBlock.createStructure(level, pos, pos.offset(0, 0, -1));
            MultiblockStructureBlock.createStructure(level, pos, pos.offset(0, 0, 1));
            MultiblockStructureBlock.createStructure(level, pos, pos.above());
            MultiblockStructureBlock.createStructure(level, pos, pos.above(2));
            MultiblockStructureBlock.createStructure(level, pos, pos.above(3));
            MultiblockStructureBlock.createStructure(level, pos, pos.above(4));

            var lowerCorner = pos.offset(-1, 2, -1);
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    var updatedAt = lowerCorner.offset(x, 0, z);
                    MultiblockStructureBlock.createStructure(level, pos, updatedAt);
                }
            }
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
            level.setBlockAndUpdate(pos.offset(-1, 0, 0), Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos.offset(1, 0, 0), Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos.offset(0, 0, -1), Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos.offset(0, 0, 1), Blocks.AIR.defaultBlockState());

            level.setBlockAndUpdate(pos.above(), Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos.above(2), Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos.above(3), Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos.above(4), Blocks.AIR.defaultBlockState());

            var lowerCorner = pos.offset(-1, 2, -1);
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    var updatedAt = lowerCorner.offset(x, 0, z);
                    level.setBlockAndUpdate(updatedAt, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return CONTROLLER_LOCAL_SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext selectionContext) {
        return CONTROLLER_LOCAL_SHAPE;
    }
}
