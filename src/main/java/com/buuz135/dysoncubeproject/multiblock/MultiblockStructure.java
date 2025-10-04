package com.buuz135.dysoncubeproject.multiblock;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MultiblockStructure {

    private final int sizeX, sizeY, sizeZ;
    private final VoxelShape shape;

    public MultiblockStructure(int sizeX, int sizeY, int sizeZ, VoxelShape shape) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.shape = shape;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getSizeZ() {
        return sizeZ;
    }

    public boolean validateSpace(LevelAccessor level, BlockPos anchor) {
        int sizeX = this.getSizeX();
        int sizeY = this.getSizeY();
        int sizeZ = this.getSizeZ();
        if (sizeX <= 0 || sizeY <= 0 || sizeZ <= 0) return false;

        // Controller sits at bottom-center of the structure footprint
        int halfX = sizeX / 2; // floor
        int halfZ = sizeZ / 2;
        BlockPos min = anchor.offset(-halfX, 0, -halfZ);
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    if (!level.getBlockState(min.offset(x, y, z)).isAir()) return false;
                }
            }
        }
        return true;
    }

    public AABB getAABB(BlockPos anchor) {
        return new AABB(anchor.getX() - sizeX, anchor.getY(), anchor.getZ() - sizeZ, anchor.getX() + sizeX, anchor.getY() + sizeY, anchor.getZ() + sizeZ);
    }

    public VoxelShape getShape() {
        return shape;
    }
}
