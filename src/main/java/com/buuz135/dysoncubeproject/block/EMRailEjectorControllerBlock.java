package com.buuz135.dysoncubeproject.block;

import com.buuz135.dysoncubeproject.DCPContent;
import com.buuz135.dysoncubeproject.block.tile.EMRailEjectorBlockEntity;
import com.buuz135.dysoncubeproject.multiblock.MultiblockStructure;
import com.buuz135.dysoncubeproject.world.DysonSphereStructure;
import com.buuz135.dysoncubeproject.world.DysonSphereProgressSavedData;
import com.hrznstudio.titanium.block.BasicTileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
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

public class EMRailEjectorControllerBlock extends DefaultMultiblockControllerBlock<EMRailEjectorBlockEntity> {

    public static VoxelShape SHAPE = Stream.of(
            Block.box(-9, 0, -9, 25, 6, 25),
            Block.box(-2, 6, -2, 18, 12, 18),
            Block.box(2, 12, 2, 14, 32, 14)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape CONTROLLER_LOCAL_SHAPE = Shapes.join(SHAPE, Block.box(0, 0, 0, 16, 16, 16), BooleanOp.AND);

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
        if (level instanceof ServerLevel serverLevel) {
            if (placer != null) {
                var dyson = DysonSphereProgressSavedData.get(level);
                var subscribedSphere = dyson.getSubscribedFor(placer.getStringUUID());
                dyson.getSpheres().computeIfAbsent(subscribedSphere, s -> new DysonSphereStructure());
                dyson.setDirty();
                if (serverLevel.getBlockEntity(pos) instanceof EMRailEjectorBlockEntity blockEntity) {
                    blockEntity.setDysonSphereId(subscribedSphere);
                }
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
    public static MultiblockStructure MULTIBLOCK_STRUCTURE = new MultiblockStructure(3, 3, 3, SHAPE);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return CONTROLLER_LOCAL_SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext selectionContext) {
        return CONTROLLER_LOCAL_SHAPE;
    }

    @Override
    public NonNullList<ItemStack> getDynamicDrops(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        if (tileentity instanceof EMRailEjectorBlockEntity emRailEjectorBlock && emRailEjectorBlock.getInput() != null) {
            for (int i = 0; i < emRailEjectorBlock.getInput().getSlots(); ++i) {
                stacks.add(emRailEjectorBlock.getInput().getStackInSlot(i));
            }
        }
        return stacks;
    }
}
