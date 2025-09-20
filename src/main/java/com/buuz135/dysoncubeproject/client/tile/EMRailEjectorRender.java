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
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), multiBufferSource.getBuffer(RenderType.solid()), null, DCPExtraModels.EM_RAILEJECTOR_BASE, 255, 255, 255, combinedLightIn, combinedOverlayIn);

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
            float period = entity.getProgressBarComponent().getMaxProgress(); // ticks for full cycle
            float shootWindow = 24f; // ticks we show the projectile
            float chargeWindow = 35f; // ticks before shot used to show electricity
            float t = (float) (entity.getProgressBarComponent().getProgress());
            // CHARGING ANIMATION
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

            t = entity.getLevel().getGameTime() - entity.getLastExecution();
            //AFTER SHOOTING ANIMATION
            float progress = t / shootWindow; // 0..1
            if (t > 0 && t < shootWindow) {
                // 1) Rail beam: bright cross-shaped beam along barrel
                if (DCPShaders.RAIL_BEAM != null) {
                    try {
                        ShaderInstance shader = DCPShaders.RAIL_BEAM;
                        var uTime = shader.getUniform("uTime");
                        if (uTime != null) uTime.set((entity.getLevel().getGameTime() + partialTicks) / 20.0f);
                        var uInt = shader.getUniform("uIntensity");
                        // Peak at start, decay over window
                        float beamIntensity = 1.2f * (1.0f - progress);
                        if (uInt != null) uInt.set(beamIntensity);
                    } catch (Throwable ignored) {
                    }

                    VertexConsumer beam = multiBufferSource.getBuffer(DCPRenderTypes.railBeam());
                    poseStack.pushPose();
                    // Muzzle location approximation in local gun space
                    poseStack.translate(0.12, 0.45, 0.5);

                    float beamLen = 160.0f * ((2.0f - progress * 2)); // very long
                    float halfW = 0.10f + 0.06f * (1.0f - progress); // width tapers over time
                    float r = 0.9f, g = 1.0f, b = 1.0f, a = 1.0f;

                    // Quad 1: vertical ribbon (vary Y, Z=0)
                    // order CCW for front face; culling disabled anyway
                    beam.addVertex(poseStack.last().pose(), 0.0f, -halfW, 0.0f).setColor(r, g, b, a);
                    beam.addVertex(poseStack.last().pose(), beamLen, -halfW, 0.0f).setColor(r, g, b, a);
                    beam.addVertex(poseStack.last().pose(), beamLen, halfW, 0.0f).setColor(r, g, b, a);
                    beam.addVertex(poseStack.last().pose(), 0.0f, halfW, 0.0f).setColor(r, g, b, a);

                    // Quad 2: horizontal ribbon (vary Z, Y=0)
                    beam.addVertex(poseStack.last().pose(), 0.0f, 0.0f, -halfW).setColor(r, g, b, a);
                    beam.addVertex(poseStack.last().pose(), beamLen, 0.0f, -halfW).setColor(r, g, b, a);
                    beam.addVertex(poseStack.last().pose(), beamLen, 0.0f, halfW).setColor(r, g, b, a);
                    beam.addVertex(poseStack.last().pose(), 0.0f, 0.0f, halfW).setColor(r, g, b, a);

                    poseStack.popPose();
                }
            }

            // 2) Shockwave ring at the muzzle for first few ticks
            if (DCPShaders.RAIL_ELECTRIC != null) {
                float shockDur = 6.0f;
                if (t > 0 && t < shockDur) {
                    try {
                        ShaderInstance shader = DCPShaders.RAIL_ELECTRIC;
                        var uTime = shader.getUniform("uTime");
                        if (uTime != null) uTime.set((entity.getLevel().getGameTime() + partialTicks) / 20.0f);
                        var uInt = shader.getUniform("uIntensity");
                        if (uInt != null) uInt.set(1.0f);
                    } catch (Throwable ignored) {
                    }

                    VertexConsumer lines = multiBufferSource.getBuffer(DCPRenderTypes.railElectricLines());
                    poseStack.pushPose();
                    poseStack.translate(0.12, 0.45, 0.5);
                    float radius = 0.2f + 0.9f * (t / shockDur);
                    int segs = 32;
                    float rf = 1.0f, gf = 1.0f, bf = 1.0f, af = 1.0f;
                    for (int depth = 0; depth < 7; depth++) {
                        for (int i = 0; i < segs; i++) {
                            double a0 = (Math.PI * 2 * i) / segs;
                            double a1 = (Math.PI * 2 * (i + 1)) / segs;
                            float y0 = (float) (Math.cos(a0) * radius);
                            float z0 = (float) (Math.sin(a0) * radius);
                            float y1 = (float) (Math.cos(a1) * radius);
                            float z1 = (float) (Math.sin(a1) * radius);
                            lines.addVertex(poseStack.last().pose(), depth * 0.5f, y0, z0).setColor(rf, gf, bf, af);
                            lines.addVertex(poseStack.last().pose(), depth * 0.5f, y1, z1).setColor(rf, gf, bf, af);
                        }
                    }
                    poseStack.popPose();
                }
            }

            // Render a small projectile cube shooting out periodically
            if (DCPExtraModels.EM_RAILEJECTOR_PROJECTILE != null) {
                if (t > 0 && t < shootWindow) {

                    float distance = 0.5f + progress * 1000f; // blocks from muzzle
                    poseStack.pushPose();
                    // Move to muzzle area (approximate) in local gun space
                    poseStack.translate(0.75, -0.1, 0);
                    // Move forward along barrel direction (local +X)
                    poseStack.translate(distance, 0, 0);
                    // Render small cube projectile
                    Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                            poseStack.last(),
                            multiBufferSource.getBuffer(RenderType.solid()),
                            null,
                            DCPExtraModels.EM_RAILEJECTOR_PROJECTILE,
                            255, 255, 255,
                            combinedLightIn,
                            combinedOverlayIn
                    );

                    // Additive glow sprite using rail beam shader (if available)
                    if (DCPShaders.RAIL_BEAM != null) {
                        try {
                            ShaderInstance shader = DCPShaders.RAIL_BEAM;
                            var uTime = shader.getUniform("uTime");
                            if (uTime != null) uTime.set((entity.getLevel().getGameTime() + partialTicks) / 20.0f);
                            var uInt = shader.getUniform("uIntensity");
                            if (uInt != null) uInt.set(1.2f);
                        } catch (Throwable ignored) {
                        }
                        VertexConsumer glow = multiBufferSource.getBuffer(DCPRenderTypes.railBeam());
                        float s = 0.18f; // half size
                        float r = 0.9f, g = 1.0f, b = 1.0f, a = 1.0f;
                        // Two tiny cross quads centered at the projectile
                        glow.addVertex(poseStack.last().pose(), -s, -s, 0.0f).setColor(r, g, b, a);
                        glow.addVertex(poseStack.last().pose(), s, -s, 0.0f).setColor(r, g, b, a);
                        glow.addVertex(poseStack.last().pose(), s, s, 0.0f).setColor(r, g, b, a);
                        glow.addVertex(poseStack.last().pose(), -s, s, 0.0f).setColor(r, g, b, a);

                        glow.addVertex(poseStack.last().pose(), -s, 0.0f, -s).setColor(r, g, b, a);
                        glow.addVertex(poseStack.last().pose(), s, 0.0f, -s).setColor(r, g, b, a);
                        glow.addVertex(poseStack.last().pose(), s, 0.0f, s).setColor(r, g, b, a);
                        glow.addVertex(poseStack.last().pose(), -s, 0.0f, s).setColor(r, g, b, a);
                    }

                    poseStack.popPose();
                }
            }
        }

        poseStack.popPose();
    }
}
