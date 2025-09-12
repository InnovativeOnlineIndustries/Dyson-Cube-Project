package com.buuz135.dysoncubeproject.multiblock;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class MultiblockStructure {

    private final int sizeX, sizeY, sizeZ;

    public MultiblockStructure(int sizeX, int sizeY, int sizeZ) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
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

    public boolean validateSpace(Level level, BlockPos pos) {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    if (!level.getBlockState(pos.offset(x, y, z)).isAir()) return false;
                }
            }
        }
        return true;
    }

    public void build(Level level, BlockPos pos, Block controller) {

    }

}
