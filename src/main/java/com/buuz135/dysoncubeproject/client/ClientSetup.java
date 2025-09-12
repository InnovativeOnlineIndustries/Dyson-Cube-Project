package com.buuz135.dysoncubeproject.client;

import com.buuz135.dysoncubeproject.DysonCubeProject;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;


public class ClientSetup {

    public static void init() {
        EventManager.forge(RenderHighlightEvent.Block.class).process(ClientEvents::blockOverlayEvent).subscribe();
        EventManager.mod(RegisterShadersEvent.class).process(ClientSetup::registerShaders).subscribe();
    }

    public static void registerShaders(RegisterShadersEvent event) {
        try {
            ShaderInstance shader = new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "hologram"), DefaultVertexFormat.POSITION_COLOR);
            event.registerShader(shader, s -> DCPShaders.HOLOGRAM = s);
        } catch (Exception e) {
            // Swallow to avoid crashing client if shader fails to load; will fallback to line rendering only
            System.out.println(e);
            DCPShaders.HOLOGRAM = null;
        }
    }
}
