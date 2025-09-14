package com.buuz135.dysoncubeproject.client;

import com.buuz135.dysoncubeproject.DysonCubeProject;
import com.buuz135.dysoncubeproject.client.render.HologramRender;
import com.buuz135.dysoncubeproject.client.render.SkyRender;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class ClientSetup {

    public static void init() {
        EventManager.forge(RenderHighlightEvent.Block.class).process(HologramRender::blockOverlayEvent).subscribe();
        EventManager.forge(RenderLevelStageEvent.class).process(SkyRender::onRenderStage).subscribe();
        EventManager.mod(RegisterShadersEvent.class).process(ClientSetup::registerShaders).subscribe();
    }

    public static void registerShaders(RegisterShadersEvent event) {
        try {
            ShaderInstance shader = new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "hologram"), DefaultVertexFormat.POSITION_COLOR);
            event.registerShader(shader, s -> DCPShaders.HOLOGRAM = s);
        } catch (Exception e) {
            DCPShaders.HOLOGRAM = null;
        }
        // Register Dyson Sun shader
        try {
            ShaderInstance shader = new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "dyson_sun"), DefaultVertexFormat.POSITION_COLOR);
            event.registerShader(shader, s -> DCPShaders.DYSON_SUN = s);
        } catch (Exception e) {
            DCPShaders.DYSON_SUN = null;
        }
    }
}
