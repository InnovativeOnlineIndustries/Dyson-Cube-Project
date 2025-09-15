package com.buuz135.dysoncubeproject.client.tile;

import com.buuz135.dysoncubeproject.block.tile.EMRailEjectorBlockEntity;
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

public class EMRailEjectorRender implements BlockEntityRenderer<EMRailEjectorBlockEntity> {
    @Override
    public void render(EMRailEjectorBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int combinedLightIn, int combinedOverlayIn) {
        poseStack.pushPose();
        poseStack.translate(0, 2.5, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90));

        poseStack.translate(1, 0, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));

        // Aim gun by current yaw/pitch
        poseStack.rotateAround(Axis.XP.rotationDegrees(360 - entity.getCurrentYaw()), 0, 0.5f, 0.5f);
        poseStack.rotateAround(Axis.ZP.rotationDegrees(360 - entity.getCurrentPitch()), 0, 0.5f, 0.5f);

        // Render the gun
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), multiBufferSource.getBuffer(RenderType.solid()), null, DCPExtraModels.EM_RAILEJECTOR_GUN, 255, 255, 255, combinedLightIn, combinedOverlayIn);

        // Futuristic charging effect: electric arcs around the muzzle just before firing
        if (entity.getLevel() != null) {
            long gameTime = entity.getLevel().getGameTime();
            float period = 100f; // ticks for full cycle
            float shootWindow = 24f; // ticks we show the projectile
            float chargeWindow = 35f; // ticks before shot used to show electricity
            float t = (float) ((gameTime + (double) partialTicks) % period);
            if (t >= period - chargeWindow) {
                float chargeT = (t - (period - chargeWindow)) / chargeWindow; // 0..1
                float intensity = 0 + (float) Math.pow(chargeT, 3.0); // ramp up

                poseStack.pushPose();
                // Move to muzzle area (approximate) in local gun space
                poseStack.translate(0.12, 0.45, 0.5);

                if (DCPShaders.RAIL_ELECTRIC != null) {
                    try {
                        ShaderInstance shader = DCPShaders.RAIL_ELECTRIC;
                        var uTime = shader.getUniform("uTime");
                        if (uTime != null) uTime.set((entity.getLevel().getGameTime() + partialTicks) / 20.0f);
                        var uInt = shader.getUniform("uIntensity");
                        if (uInt != null) uInt.set(intensity);
                    } catch (Throwable ignored) {
                    }

                    RenderType rt = DCPRenderTypes.railElectricLines();
                    VertexConsumer lines = multiBufferSource.getBuffer(rt);

                    int segments = 7;
                    float baseRadius = 0.62f + 0.05f * (float) Math.sin((gameTime + partialTicks) * 0.2f);
                    float jitter = 0.05f;
                    int r = 100;
                    int g = 200;
                    int b = 255;
                    int a = Math.min(255, 60 + (int) (195 * intensity));

                    // Draw multiple short jittery segments forming rough arcs around the barrel (YZ plane circle)
                    for (int ring = 0; ring < 38 * chargeT; ring++) {
                        float ringOffsetX = 0.05f * ring; // along barrel
                        for (int i = 0; i < segments; i++) {
                            float seed = i * 17.0f + ring * 31.0f + entity.getLevel().getRandom().nextFloat() * 6f;
                            float ang = (float) Math.toRadians((i * (360f / segments)) + (float) Math.sin((gameTime + partialTicks + seed) * 0.6f) * 20f);
                            float ang2 = ang + (float) Math.toRadians(10 + (float) Math.sin((gameTime + partialTicks + seed * 1.37f) * 0.9f) * 12f);
                            float rad1 = baseRadius + (float) Math.sin((gameTime + partialTicks + seed) * 0.8f) * jitter;
                            float rad2 = baseRadius + 0.07f + (float) Math.sin((gameTime + partialTicks + seed * 0.77f) * 0.8f) * jitter;

                            float y1 = (float) (Math.cos(ang) * rad1);
                            float z1 = (float) (Math.sin(ang) * rad1);
                            float y2 = (float) (Math.cos(ang2) * rad2);
                            float z2 = (float) (Math.sin(ang2) * rad2);

                            // Slight expansion as it charges
                            float expand = 0.04f * intensity;
                            y1 *= (1.0f + expand);
                            z1 *= (1.0f + expand);
                            y2 *= (1.0f + expand);
                            z2 *= (1.0f + expand);

                            // First point
                            float rf = r / 255f, gf = g / 255f, bf = b / 255f, af = a / 255f;
                            lines.addVertex(poseStack.last().pose(), 0.0f + ringOffsetX, y1, z1).setColor(rf, gf, bf, af);
                            // Second point (small advancement along barrel axis)
                            lines.addVertex(poseStack.last().pose(), 0.12f + ringOffsetX, y2, z2).setColor(rf, gf, bf, af);
                        }
                    }
                }
                poseStack.popPose();
            }

            // Render a small projectile cube shooting out periodically
            if (DCPExtraModels.EM_RAILEJECTOR_PROJECTILE != null) {
                if (t < shootWindow) {
                    float progress = t / shootWindow; // 0..1
                    float distance = 0.5f + progress * 100f; // blocks from muzzle
                    poseStack.pushPose();
                    // Move to muzzle area (approximate) in local gun space
                    poseStack.translate(0.75, -0.1, 0);
                    // Move forward along barrel direction (local +X)
                    poseStack.translate(distance, 0, 0);
                    // Slight scale wobble could be added; model is already small (2/16 block)
                    Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                            poseStack.last(),
                            multiBufferSource.getBuffer(RenderType.solid()),
                            null,
                            DCPExtraModels.EM_RAILEJECTOR_PROJECTILE,
                            255, 255, 255,
                            combinedLightIn,
                            combinedOverlayIn
                    );
                    poseStack.popPose();
                }
            }
        }

        poseStack.popPose();
    }
}
