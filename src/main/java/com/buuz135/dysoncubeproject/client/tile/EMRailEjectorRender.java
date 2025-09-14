package com.buuz135.dysoncubeproject.client.tile;

import com.buuz135.dysoncubeproject.block.tile.EMRailEjectorBlockEntity;
import com.buuz135.dysoncubeproject.client.DCPExtraModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

public class EMRailEjectorRender implements BlockEntityRenderer<EMRailEjectorBlockEntity> {
    @Override
    public void render(EMRailEjectorBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLightIn, int combinedOverlayIn) {
        poseStack.pushPose();
        poseStack.translate(0, 2.5, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90));

        poseStack.translate(1, 0, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));


        poseStack.rotateAround(Axis.XP.rotationDegrees(360 - entity.getCurrentYaw()), 0, 0.5f, 0.5f);
        poseStack.rotateAround(Axis.ZP.rotationDegrees(360 - entity.getCurrentPitch()), 0, 0.5f, 0.5f);

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), multiBufferSource.getBuffer(RenderType.solid()), null, DCPExtraModels.EM_RAILEJECTOR_GUN, 255, 255, 255, combinedLightIn, combinedOverlayIn);

        poseStack.popPose();
    }
}
