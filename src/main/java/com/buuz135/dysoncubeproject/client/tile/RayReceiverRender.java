package com.buuz135.dysoncubeproject.client.tile;

import com.buuz135.dysoncubeproject.block.RayReceiverControllerBlock;
import com.buuz135.dysoncubeproject.block.tile.EMRailEjectorBlockEntity;
import com.buuz135.dysoncubeproject.block.tile.RayReceiverBlockEntity;
import com.buuz135.dysoncubeproject.client.DCPExtraModels;
import com.buuz135.dysoncubeproject.client.DCPRenderTypes;
import com.buuz135.dysoncubeproject.client.DCPShaders;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.phys.AABB;

public class RayReceiverRender implements BlockEntityRenderer<RayReceiverBlockEntity> {

    private static void drawBoxTopFace(PoseStack pose, VertexConsumer vc, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a) {
        // Top (y = maxY)
        emit(vc, pose, (float) minX, (float) maxY, (float) minZ, r, g, b, a);
        emit(vc, pose, (float) minX, (float) maxY, (float) maxZ, r, g, b, a);
        emit(vc, pose, (float) maxX, (float) maxY, (float) maxZ, r, g, b, a);
        emit(vc, pose, (float) maxX, (float) maxY, (float) minZ, r, g, b, a);
    }

    private static void drawBoxSideFace(PoseStack pose, VertexConsumer vc, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a) {

        // North (z = minZ)
        emit(vc, pose, (float) minX, (float) minY, (float) minZ, r, g, b, a);
        emit(vc, pose, (float) minX, (float) maxY, (float) minZ, r, g, b, a);
        emit(vc, pose, (float) maxX, (float) maxY, (float) minZ, r, g, b, a);
        emit(vc, pose, (float) maxX, (float) minY, (float) minZ, r, g, b, a);
        // South (z = maxZ)
        emit(vc, pose, (float) minX, (float) minY, (float) maxZ, r, g, b, a);
        emit(vc, pose, (float) maxX, (float) minY, (float) maxZ, r, g, b, a);
        emit(vc, pose, (float) maxX, (float) maxY, (float) maxZ, r, g, b, a);
        emit(vc, pose, (float) minX, (float) maxY, (float) maxZ, r, g, b, a);
        // West (x = minX)
        emit(vc, pose, (float) minX, (float) minY, (float) minZ, r, g, b, a);
        emit(vc, pose, (float) minX, (float) minY, (float) maxZ, r, g, b, a);
        emit(vc, pose, (float) minX, (float) maxY, (float) maxZ, r, g, b, a);
        emit(vc, pose, (float) minX, (float) maxY, (float) minZ, r, g, b, a);
        // East (x = maxX)
        emit(vc, pose, (float) maxX, (float) minY, (float) minZ, r, g, b, a);
        emit(vc, pose, (float) maxX, (float) maxY, (float) minZ, r, g, b, a);
        emit(vc, pose, (float) maxX, (float) maxY, (float) maxZ, r, g, b, a);
        emit(vc, pose, (float) maxX, (float) minY, (float) maxZ, r, g, b, a);
    }

    private static void emit(VertexConsumer vc, PoseStack pose, float x, float y, float z, float r, float g, float b, float a) {
        // Manually transform by the current pose since VertexConsumer may not accept matrix directly in this version
        vc.addVertex(pose.last().pose(), x, y, z).setColor(r, g, b, a);
    }

    @Override
    public void render(RayReceiverBlockEntity rayReceiverBlockEntity, float partial, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLightIn, int combinedOverlayIn) {
        poseStack.pushPose();
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), multiBufferSource.getBuffer(RenderType.solid()), null, DCPExtraModels.RAY_RECEIVER_BASE, 255, 255, 255, combinedLightIn, combinedOverlayIn);

        poseStack.translate(0, 2, 0);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), multiBufferSource.getBuffer(RenderType.solid()), null, DCPExtraModels.RAY_RECEIVER_PLATE, 255, 255, 255, combinedLightIn, combinedOverlayIn);
        poseStack.pushPose();
        poseStack.translate(0, 2, 1);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), multiBufferSource.getBuffer(RenderType.solid()), null, DCPExtraModels.RAY_RECEIVER_LENS_STANDS, 255, 255, 255, combinedLightIn, combinedOverlayIn);

        //poseStack.rotateAround(Axis.XP.rotationDegrees(360 - rayReceiverBlockEntity.getCurrentYaw()), 0, 0.5f, 0.5f);
        poseStack.rotateAround(Axis.XP.rotationDegrees(-90), 0, 0.55f, 0.5f);
        poseStack.rotateAround(Axis.XP.rotationDegrees(360 - rayReceiverBlockEntity.getCurrentPitch() - 180), 0, 0.55f, 0.5f);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), multiBufferSource.getBuffer(RenderType.solid()), null, DCPExtraModels.RAY_RECEIVER_LENS, 255, 255, 255, combinedLightIn, combinedOverlayIn);

        poseStack.popPose();

        if (DCPShaders.HOLO_HEX != null) {
            // Update custom uniforms for the shader
            ShaderInstance shader = DCPShaders.HOLO_HEX;

            try {

                var uTime = shader.getUniform("uTime");
                if (uTime != null) uTime.set((Minecraft.getInstance().level.getGameTime() % 100000) / 20.0f);
                var uValid = shader.getUniform("uValid");
                if (uValid != null) uValid.set(true ? 1.0f : 0.0f);
                var uSize = shader.getUniform("uSize");
                if (uSize != null) uSize.set(0.75f);
                var uCam = shader.getUniform("uCamPos");
                if (uCam != null) {
                    var c = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
                    try {
                        uCam.set((float) c.x, (float) c.y, (float) c.z);
                    } catch (Throwable t) {
                        try {
                            uCam.set((float) c.x, (float) c.y, (float) c.z, 1.0f);
                        } catch (Throwable ignored2) {
                        }
                    }
                }
            } catch (Throwable ignored) {
            }

            float r = 0.5f, g = 0.9f, b = 0.9f, a = 0.7f;

            VertexConsumer quad = multiBufferSource.getBuffer(DCPRenderTypes.holoHex());

            drawBoxTopFace(poseStack, quad, -1, 0, -1, 2, 0.3, 2, r, g, b, 0.85f);

            drawBoxSideFace(poseStack, quad, 0.2499, 0.5, 0.2499, 0.751, 1.75, 0.751, r, g, b, 0.25f);

        }

        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(RayReceiverBlockEntity blockEntity) {
        return RayReceiverControllerBlock.MULTIBLOCK_STRUCTURE.getAABB(blockEntity.getBlockPos());
    }
}
