package com.buuz135.dysoncubeproject.client.render;

import com.buuz135.dysoncubeproject.block.DefaultMultiblockControllerBlock;
import com.buuz135.dysoncubeproject.client.DCPRenderTypes;
import com.buuz135.dysoncubeproject.client.DCPShaders;
import com.buuz135.dysoncubeproject.multiblock.MultiblockStructure;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;

public class HologramRender {

    public static void blockOverlayEvent(RenderHighlightEvent.Block event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        // Check if the player is holding a multiblock controller in either hand
        DefaultMultiblockControllerBlock<?> controller = getHeldController(player.getMainHandItem());
        if (controller == null) controller = getHeldController(player.getOffhandItem());
        if (controller == null) return;


        if (!(event.getTarget() instanceof BlockHitResult hit)) return;
        BlockPos anchor = hit.getBlockPos().relative(hit.getDirection());

        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        MultiblockStructure structure = controller.getMultiblockStructure();
        int sizeX = structure.getSizeX();
        int sizeY = structure.getSizeY();
        int sizeZ = structure.getSizeZ();
        if (sizeX <= 0 || sizeY <= 0 || sizeZ <= 0) return;

        // Controller sits at bottom-center of the structure footprint
        int halfX = sizeX / 2; // floor
        int halfZ = sizeZ / 2; // floor
        BlockPos min = anchor.offset(-halfX, 0, -halfZ);
        BlockPos max = min.offset(sizeX, sizeY, sizeZ);

        boolean valid = structure.validateSpace(level, anchor);

        double eps = 0.0025D;
        double minX = min.getX() - eps;
        double minY = min.getY() - eps;
        double minZ = min.getZ() - eps;
        double maxX = max.getX() + eps;
        double maxY = max.getY() + eps;
        double maxZ = max.getZ() + eps;

        var pose = event.getPoseStack();
        MultiBufferSource buffer = event.getMultiBufferSource();
        Vec3 cam = event.getCamera().getPosition();
        pose.pushPose();
        pose.translate(-cam.x, -cam.y, -cam.z);

        // Hologram color: valid = cyan/teal pulse, invalid = red/orange pulse
        float time = (float) (level.getGameTime() % 2000);
        float s = (float) (0.5 + 0.5 * Math.sin(time * 0.02));
        float r = valid ? (0.2f + 0.3f * (1.0f - s)) : (0.8f + 0.2f * s);
        float g = valid ? (0.9f * s + 0.4f * (1.0f - s)) : (0.2f + 0.2f * (1.0f - s));
        float b = valid ? (0.9f) : (0.1f + 0.2f * (1.0f - s));
        float a = 0.9f;

        // Draw hologram faces with custom shader (if available)
        if (DCPShaders.HOLOGRAM != null) {
            try {
                var shader = DCPShaders.HOLOGRAM;
                var uTime = shader.getUniform("uTime");
                if (uTime != null) uTime.set((level.getGameTime() % 100000) / 20.0f);
                var uValid = shader.getUniform("uValid");
                if (uValid != null) uValid.set(valid ? 1.0f : 0.0f);
            } catch (Throwable ignored) {
            }

            VertexConsumer quad = buffer.getBuffer(DCPRenderTypes.hologram());
            float faceAlpha = 0.85f; // base alpha; shader modulates further
            // Slight inset to avoid z-fighting with world blocks
            double inset = 0.0025;
            // Set camera uniform for world-space effects
            try {
                var shader = DCPShaders.HOLOGRAM;
                var uCam = shader.getUniform("uCamPos");
                if (uCam != null) {
                    var c = event.getCamera().getPosition();
                    // Attempt 3-float setter; if not available, fall back to 4-float
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


            drawBoxFaces(event.getPoseStack(), quad, minX + inset, minY + inset + 0.001, minZ + inset, maxX - inset, maxY - inset, maxZ - inset, r, g, b, faceAlpha);


            int centerX = min.getX() + halfX;
            int centerY = min.getY();
            int centerZ = min.getZ() + halfZ;
            double cMinX = centerX + 0.002;
            double cMinY = centerY + 0.002;
            double cMinZ = centerZ + 0.002;
            double cMaxX = centerX + 1.0 - 0.002;
            double cMaxY = centerY + 1.0 - 0.002;
            double cMaxZ = centerZ + 1.0 - 0.002;
            float hr = valid ? 0.35f : 1.0f;
            float hg = valid ? 1.0f : 0.35f;
            float hb = valid ? 1.0f : 0.25f;
            float ha = 0.95f;
            drawBoxFaces(event.getPoseStack(), quad, cMinX, cMinY, cMinZ, cMaxX, cMaxY, cMaxZ, hr, hg, hb, ha);
            return;
        }

        pose.popPose();
    }


    private static DefaultMultiblockControllerBlock<?> getHeldController(ItemStack stack) {
        if (stack == null) return null;
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof DefaultMultiblockControllerBlock<?> controller) {
            return controller;
        }
        return null;
    }

    private static void drawBoxFaces(PoseStack pose, VertexConsumer vc, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float a) {
        // Bottom (y = minY)
        emit(vc, pose, (float) minX, (float) minY, (float) minZ, r, g, b, a);
        emit(vc, pose, (float) maxX, (float) minY, (float) minZ, r, g, b, a);
        emit(vc, pose, (float) maxX, (float) minY, (float) maxZ, r, g, b, a);
        emit(vc, pose, (float) minX, (float) minY, (float) maxZ, r, g, b, a);
        // Top (y = maxY)
        emit(vc, pose, (float) minX, (float) maxY, (float) minZ, r, g, b, a);
        emit(vc, pose, (float) minX, (float) maxY, (float) maxZ, r, g, b, a);
        emit(vc, pose, (float) maxX, (float) maxY, (float) maxZ, r, g, b, a);
        emit(vc, pose, (float) maxX, (float) maxY, (float) minZ, r, g, b, a);
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
        org.joml.Matrix4f m = pose.last().pose();
        org.joml.Vector4f v = new org.joml.Vector4f(x, y, z, 1.0f);
        v.mul(m);
        vc.addVertex(v.x, v.y, v.z).setColor(r, g, b, a);
    }
}
