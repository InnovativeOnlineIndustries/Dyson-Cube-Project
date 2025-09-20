package com.buuz135.dysoncubeproject.client;

import com.buuz135.dysoncubeproject.DCPContent;
import com.buuz135.dysoncubeproject.DysonCubeProject;
import com.buuz135.dysoncubeproject.block.tile.EMRailEjectorBlockEntity;
import com.buuz135.dysoncubeproject.client.render.HologramRender;
import com.buuz135.dysoncubeproject.client.render.SkyRender;
import com.buuz135.dysoncubeproject.client.tile.EMRailEjectorRender;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.model.SimpleModelState;

public class ClientSetup {

    public static void init() {
        EventManager.forge(RenderHighlightEvent.Block.class).process(HologramRender::blockOverlayEvent).subscribe();
        EventManager.forge(RenderLevelStageEvent.class).process(SkyRender::onRenderStage).subscribe();
        EventManager.mod(RegisterShadersEvent.class).process(ClientSetup::registerShaders).subscribe();
        EventManager.mod(ModelEvent.BakingCompleted.class).process(event -> {
            DCPExtraModels.EM_RAILEJECTOR_GUN = bakeModel(ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "block/em_railejector_gun"), event.getModelBakery());
            DCPExtraModels.EM_RAILEJECTOR_PROJECTILE = bakeModel(ResourceLocation.fromNamespaceAndPath(DysonCubeProject.MODID, "block/em_railejector_projectile"), event.getModelBakery());
        }).subscribe();
        EventManager.mod(EntityRenderersEvent.RegisterRenderers.class).process(event -> {
            event.registerBlockEntityRenderer((BlockEntityType<? extends EMRailEjectorBlockEntity>) DCPContent.Blocks.EM_RAILEJECTOR_CONTROLLER.type().get(), context -> new EMRailEjectorRender());
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
