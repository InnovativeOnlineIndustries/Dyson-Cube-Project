package com.buuz135.dysoncubeproject.client.render;

import com.buuz135.dysoncubeproject.client.DCPRenderTypes;
import com.buuz135.dysoncubeproject.client.DCPShaders;
import com.buuz135.dysoncubeproject.world.ClientDysonSphere;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import com.mojang.math.Axis;

public class SkyRender {

    public static void onRenderStage(RenderLevelStageEvent event) { //TODO HIDE WHEN RAINING
        // Draw after vanilla sky (sun/moon) so we render "on top of the sun"
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.level.isRainingAt(mc.player.getOnPos())) return;

        var subscribedTo = ClientDysonSphere.DYSON_SPHERE_PROGRESS.getSubscribedPlayers().getOrDefault(mc.player.getStringUUID(), mc.player.getStringUUID());
        var sphere = ClientDysonSphere.DYSON_SPHERE_PROGRESS.getSpheres().getOrDefault(subscribedTo, null);

        if (sphere == null) return;
        var progress = (float) sphere.getProgress();
        progress = 1f;
        PoseStack pose = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        float skyAngle = mc.level.getTimeOfDay(event.getPartialTick().getGameTimeDeltaTicks()) * 360.0f;

        if (DCPShaders.DYSON_SUN != null) {
            // Update custom uniforms for the shader
            ShaderInstance shader = DCPShaders.HOLO_HEX;

            try {

                var uTime = shader.getUniform("uTime");
                if (uTime != null) uTime.set((Minecraft.getInstance().level.getGameTime() % 100000) / 20.0f);
                var uValid = shader.getUniform("uValid");
                if (uValid != null) uValid.set(true ? 1.0f : 0.0f);
                var uSize = shader.getUniform("uSize");
                if (uSize != null) uSize.set(25f);
            } catch (Throwable ignored) {
            }


            pose.pushPose();
            //pose.mulPose(event.getModelViewMatrix());
            pose.mulPose(Axis.YP.rotationDegrees(-90.0F));
            pose.mulPose(Axis.XP.rotationDegrees(90));
            pose.mulPose(Axis.XP.rotationDegrees(skyAngle));

            pose.translate(-30.0f, 0.0f, -310.0f);

            // Build a square in local XY plane; shader expects half-extent ~30 (it divides by 60.0 for UV)
            float s = 30.0f;
            float r = 0.5f, g = 0.9f, b = 0.9f, a = 0.7f;

            VertexConsumer vc = buffer.getBuffer(DCPRenderTypes.holoHex());

            // Quad (counter-clockwise) at z=0 in local space after transformations
            emit(vc, pose, 0, s, 0.0f, r, g, b, a);
            emit(vc, pose, s * 2 * progress, s, 0.0f, r, g, b, a);
            emit(vc, pose, s * 2 * progress, -s, 0.0f, r, g, b, a);
            emit(vc, pose, 0, -s, 0.0f, r, g, b, a);

            pose.popPose();

            // Flush this render type to ensure it draws this frame
            buffer.endBatch(DCPRenderTypes.dysonSun());
        }

    }

    private static void emit(VertexConsumer vc, PoseStack pose, float x, float y, float z, float r, float g, float b, float a) {
        // Submit world-space coordinates; the shader's ModelViewMat from the pose will handle transforms
        vc.addVertex(pose.last().pose(), x, y, z).setColor(r, g, b, a);
    }

}
