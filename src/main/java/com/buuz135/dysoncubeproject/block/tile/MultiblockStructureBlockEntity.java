package com.buuz135.dysoncubeproject.block.tile;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.BasicTile;
import com.hrznstudio.titanium.block.tile.ITickableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class MultiblockStructureBlockEntity extends BasicTile<MultiblockStructureBlockEntity> implements ITickableBlockEntity<MultiblockStructureBlockEntity> {

    @Save
    private BlockPos controllerPos;

    // Transient cache for this placeholder block's split shape
    private VoxelShape cachedShape;

    public MultiblockStructureBlockEntity(BasicTileBlock<MultiblockStructureBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
    }

    public BlockPos getControllerPos() {
        return controllerPos;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void clientTick(Level level, BlockPos pos, BlockState state, MultiblockStructureBlockEntity blockEntity) {
        if (controllerPos != null) {
            var destroyingBlocks = Minecraft.getInstance().levelRenderer.destroyingBlocks;
            for (Integer i : destroyingBlocks.keySet()) {
                var progress = destroyingBlocks.get(i);
                if (progress.getPos().equals(pos)) {
                    Minecraft.getInstance().levelRenderer.destroyBlockProgress(i, controllerPos, progress.getProgress());
                }
            }
        }
    }

    @Override
    public ItemInteractionResult onActivated(Player player, InteractionHand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (controllerPos != null && this.level.getBlockEntity(controllerPos) instanceof BasicTile<?> controller) {
            return controller.onActivated(player, hand, facing, hitX, hitY, hitZ);
        }
        return super.onActivated(player, hand, facing, hitX, hitY, hitZ);
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
        // Invalidate cached shape when controller changes
        this.cachedShape = null;
        markForUpdate();
    }

    public VoxelShape getCachedShape() {
        return cachedShape;
    }

    public void setCachedShape(VoxelShape cachedShape) {
        this.cachedShape = cachedShape;
    }
}
