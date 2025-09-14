package com.buuz135.dysoncubeproject.client;

import com.buuz135.dysoncubeproject.world.ClientDysonSphere;
import com.buuz135.dysoncubeproject.world.DysonSphereProgressSavedData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import com.mojang.math.Axis;

public class ClientSkyRender {

    private static final ResourceLocation GREEN_SUN_TEX = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/white.png");

    public static void onRenderStage(RenderLevelStageEvent event) { //TODO HIDE WHEN RAINING
        // Draw after vanilla sky (sun/moon) so we render "on top of the sun"
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        var subscribedTo = ClientDysonSphere.DYSON_SPHERE_PROGRESS.getSubscribedPlayers().getOrDefault(mc.player.getStringUUID(), mc.player.getStringUUID());
        var sphere = ClientDysonSphere.DYSON_SPHERE_PROGRESS.getSpheres().getOrDefault(subscribedTo, null);

        if (sphere == null) return;
        var progress = (float) sphere.getProgress();
        progress = mc.level.getGameTime() % 300 / 300.0f;

        PoseStack pose = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        float skyAngle = mc.level.getTimeOfDay(event.getPartialTick().getGameTimeDeltaTicks()) * 360.0f;

        // 1) Draw the green textured quad in front of the sun
        if (false) {
            pose.pushPose();
            pose.mulPose(event.getModelViewMatrix());
            pose.mulPose(Axis.YP.rotationDegrees(-90.0F));
            pose.mulPose(Axis.XP.rotationDegrees(71));
            pose.mulPose(Axis.XP.rotationDegrees(skyAngle));

            pose.translate(0.0f, 0.0f, -300.0f);

            float sTex = 30.0f;
            VertexConsumer vcTex = buffer.getBuffer(RenderType.entityTranslucent(GREEN_SUN_TEX));
            // Full white so texture shows as-is, set fullbright light so it pops in sky
            int light = 0xF000F0; // maximum brightness
            emitTex(vcTex, pose, -sTex, sTex, 0.0f, 0.0f, 0.0f, light, 0, 1, 0, 1);
            emitTex(vcTex, pose, sTex, sTex, 0.0f, 1.0f, 0.0f, light, 0, 1, 0, 1);
            emitTex(vcTex, pose, sTex, -sTex, 0.0f, 1.0f, 1.0f, light, 0, 1, 0, 1);
            emitTex(vcTex, pose, -sTex, -sTex, 0.0f, 0.0f, 1.0f, light, 0, 1, 0, 1);
            pose.popPose();
            buffer.endBatch(RenderType.entityTranslucent(GREEN_SUN_TEX));
        }


        // 2) If our custom shader is available, update uniforms and draw the shader quad similarly
        if (DCPShaders.DYSON_SUN != null) {
            // Update custom uniforms for the shader
            ShaderInstance shader = DCPShaders.DYSON_SUN;

            try {

                var uTime = shader.getUniform("uTime");
                if (uTime != null) uTime.set((Minecraft.getInstance().level.getGameTime() % 100000) / 20.0f);
                var uValid = shader.getUniform("uValid");
                if (uValid != null) uValid.set(true ? 1.0f : 0.0f);
            } catch (Throwable ignored) {
            }


            pose.pushPose();
            pose.mulPose(event.getModelViewMatrix());
            pose.mulPose(Axis.YP.rotationDegrees(-90.0F));
            pose.mulPose(Axis.XP.rotationDegrees(90));
            pose.mulPose(Axis.XP.rotationDegrees(skyAngle));

            pose.translate(-30.0f, 0.0f, -310.0f);

            // Build a square in local XY plane; shader expects half-extent ~30 (it divides by 60.0 for UV)
            float s = 30.0f;
            float r = 1.0f, g = 1.0f, b = 1.0f, a = 1.0f;

            VertexConsumer vc = buffer.getBuffer(DysonSunRenderType.dysonSun());

            // Quad (counter-clockwise) at z=0 in local space after transformations
            emit(vc, pose, 0, s, 0.0f, r, g, b, a);
            emit(vc, pose, s * 2 * progress, s, 0.0f, r, g, b, a);
            emit(vc, pose, s * 2 * progress, -s, 0.0f, r, g, b, a);
            emit(vc, pose, 0, -s, 0.0f, r, g, b, a);

            pose.popPose();

            // Flush this render type to ensure it draws this frame
            buffer.endBatch(DysonSunRenderType.dysonSun());
        }

    }

    private static void emit(VertexConsumer vc, PoseStack pose, float x, float y, float z, float r, float g, float b, float a) {
        // Submit world-space coordinates; the shader's ModelViewMat from the pose will handle transforms
        vc.addVertex(pose.last().pose(), x, y, z).setColor(r, g, b, a);
    }

    private static void emitTex(VertexConsumer vc, PoseStack pose, float x, float y, float z, float u, float v, int light, float r, float g, float b, float a) {
        vc.addVertex(pose.last().pose(), x, y, z)
                .setColor(r, g, b, a)
                .setNormal(0, 0, 0)
                .setUv(u, v)
                .setOverlay(0)
                .setLight(light);
    }
}
