package com.buuz135.dysoncubeproject.client;

import com.buuz135.dysoncubeproject.DCPAttachments;
import com.buuz135.dysoncubeproject.DCPContent;
import com.buuz135.dysoncubeproject.DysonCubeProject;
import com.buuz135.dysoncubeproject.block.tile.EMRailEjectorBlockEntity;
import com.buuz135.dysoncubeproject.block.tile.RayReceiverBlockEntity;
import com.buuz135.dysoncubeproject.client.render.HologramRender;
import com.buuz135.dysoncubeproject.client.render.SkyRender;
import com.buuz135.dysoncubeproject.client.tile.EMRailEjectorRender;
import com.buuz135.dysoncubeproject.client.tile.RayReceiverRender;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.model.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class ClientSetup {

    public static void init() {
        EventManager.forge(RenderHighlightEvent.Block.class).process(HologramRender::blockOverlayEvent).subscribe();
        EventManager.forge(RenderLevelStageEvent.class).process(SkyRender::onRenderStage).subscribe();
        EventManager.mod(RegisterShadersEvent.class).process(ClientSetup::registerShaders).subscribe();
        EventManager.mod(ModelEvent.BakingCompleted.class).process(event -> {
            DCPExtraModels.EM_RAILEJECTOR_BASE = bakeModel(ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "block/em_railejector_base"), event.getModelBakery());
            DCPExtraModels.EM_RAILEJECTOR_GUN = bakeModel(ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "block/em_railejector_gun"), event.getModelBakery());
            DCPExtraModels.EM_RAILEJECTOR_PROJECTILE = bakeModel(ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "block/em_railejector_projectile"), event.getModelBakery());
            DCPExtraModels.RAY_RECEIVER_BASE = bakeModel(ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "block/ray_receiver_base"), event.getModelBakery());
            DCPExtraModels.RAY_RECEIVER_PLATE = bakeModel(ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "block/ray_receiver_plate"), event.getModelBakery());
            DCPExtraModels.RAY_RECEIVER_LENS = bakeModel(ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "block/ray_receiver_lens"), event.getModelBakery());
            DCPExtraModels.RAY_RECEIVER_LENS_STANDS = bakeModel(ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "block/ray_receiver_lens_stands"), event.getModelBakery());
        }).subscribe();
        EventManager.mod(EntityRenderersEvent.RegisterRenderers.class).process(event -> {
            event.registerBlockEntityRenderer((BlockEntityType<? extends EMRailEjectorBlockEntity>) DCPContent.Blocks.EM_RAILEJECTOR_CONTROLLER.type().get(), context -> new EMRailEjectorRender());
            event.registerBlockEntityRenderer((BlockEntityType<? extends RayReceiverBlockEntity>) DCPContent.Blocks.RAY_RECEIVER_CONTROLLER.type().get(), context -> new RayReceiverRender());
        }).subscribe();
        EventManager.forge(ItemTooltipEvent.class).process(itemTooltipEvent -> {
            var stack = itemTooltipEvent.getItemStack();
            if (stack.getOrDefault(DCPAttachments.SOLAR_SAIL, 0) > 0) {
                itemTooltipEvent.getToolTip().add(Component.literal("Contains " + stack.getOrDefault(DCPAttachments.SOLAR_SAIL, 0) + " solar sail(s)")
                        .withColor(DCPContent.CYAN_COLOR));
            }
            if (stack.getOrDefault(DCPAttachments.BEAM, 0) > 0) {
                itemTooltipEvent.getToolTip().add(Component.literal("Contains " + stack.getOrDefault(DCPAttachments.BEAM, 0) + " beam(s)")
                        .withColor(DCPContent.CYAN_COLOR));
            }
        }).subscribe();
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
        // Register Holographic Hex shader
        try {
            ShaderInstance shader = new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "holo_hex"), DefaultVertexFormat.POSITION_COLOR);
            event.registerShader(shader, s -> DCPShaders.HOLO_HEX = s);
        } catch (Exception e) {
            DCPShaders.HOLO_HEX = null;
        }
        // Register Rail Electric shader
        try {
            ShaderInstance shader = new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "rail_electric"), DefaultVertexFormat.POSITION_COLOR);
            event.registerShader(shader, s -> DCPShaders.RAIL_ELECTRIC = s);
        } catch (Exception e) {
            DCPShaders.RAIL_ELECTRIC = null;
        }
        // Register Rail Beam shader
        try {
            ShaderInstance shader = new ShaderInstance(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "rail_beam"), DefaultVertexFormat.POSITION_COLOR);
            event.registerShader(shader, s -> DCPShaders.RAIL_BEAM = s);
        } catch (Exception e) {
            DCPShaders.RAIL_BEAM = null;
        }
    }

    private static BakedModel bakeModel(ResourceLocation model, ModelBakery modelBakery) {
        var modelResourceLocation = new ModelResourceLocation(model, "standalone");
        UnbakedModel unbakedModel = modelBakery.getModel(model);
        ModelBaker baker = modelBakery.new ModelBakerImpl((modelLoc, material) -> material.sprite(), modelResourceLocation);
        return unbakedModel.bake(baker, Material::sprite, new SimpleModelState(Transformation.identity()));
    }
}
