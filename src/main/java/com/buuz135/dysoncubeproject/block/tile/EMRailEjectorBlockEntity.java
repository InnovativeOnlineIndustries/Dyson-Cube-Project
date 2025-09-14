package com.buuz135.dysoncubeproject.block.tile;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.BasicTile;
import com.hrznstudio.titanium.block.tile.ITickableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EMRailEjectorBlockEntity extends BasicTile<EMRailEjectorBlockEntity> implements ITickableBlockEntity<EMRailEjectorBlockEntity> {

    @Save
    private float currentYaw, currentPitch, targetYaw, targetPitch;


    public EMRailEjectorBlockEntity(BasicTileBlock<EMRailEjectorBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.currentYaw = 0;
        this.currentPitch = 0;
        this.targetYaw = 0; //HORIZONTAL
        this.targetPitch = 0; //VERTICAL
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, EMRailEjectorBlockEntity blockEntity) {

    }

    @Override
    public void clientTick(Level level, BlockPos pos, BlockState state, EMRailEjectorBlockEntity blockEntity) {
        this.targetPitch = level.getTimeOfDay(1f) * 360f;
        //this.targetPitch = 300;
        if (this.targetPitch <= 20) {
            this.targetPitch = 20;
        }

        if (this.targetPitch >= 360 - 20) {
            this.targetPitch = 20;
        }


        if (this.targetPitch <= 90) {
            this.targetYaw = 0;
        } else {
            this.targetYaw = 180;
        }

        if (this.targetPitch >= 90 && this.targetPitch <= 270) {
            this.targetPitch = 90;
        }

        if (this.targetPitch >= 360 - 90) {
            this.targetPitch = 360 - this.targetPitch;
        }

        if (level.isRaining()) {
            this.targetPitch = 90;
        }

        // Move currentPitch towards targetPitch by 1 each tick
        if (this.currentPitch <= this.targetPitch) {
            this.currentPitch = Math.min(this.currentPitch + 1, this.targetPitch);
        } else if (this.currentPitch > this.targetPitch) {
            this.currentPitch = Math.max(this.currentPitch - 1, this.targetPitch);
        }
        if (this.currentYaw <= this.targetYaw) {
            this.currentYaw = Math.min(this.currentYaw + 1, this.targetYaw);
        } else if (this.currentYaw > this.targetYaw) {
            this.currentYaw = Math.max(this.currentYaw - 1, this.targetYaw);
        }

    }


    public float getCurrentPitch() {
        return currentPitch;
    }

    public float getCurrentYaw() {
        return currentYaw;
    }
}
