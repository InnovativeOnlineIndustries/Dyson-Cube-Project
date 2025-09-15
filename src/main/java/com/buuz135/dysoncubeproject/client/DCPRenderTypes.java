package com.buuz135.dysoncubeproject.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class DCPRenderTypes {

    private static RenderType DYSON_SUN;
    private static RenderType HOLOGRAM;
    private static RenderType RAIL_ELECTRIC_LINES;

    public static RenderType dysonSun() {
        if (DYSON_SUN == null) {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> DCPShaders.DYSON_SUN))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(true);
            DYSON_SUN = RenderType.create("dysoncubeproject_sun",
                    DefaultVertexFormat.POSITION_COLOR,
                    VertexFormat.Mode.QUADS,
                    256,
                    false,
                    true,
                    state);
        }
        return DYSON_SUN;
    }

    public static RenderType hologram() {
        if (HOLOGRAM == null) {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> DCPShaders.HOLOGRAM))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(true);
            HOLOGRAM = RenderType.create("dysoncubeproject_hologram",
                    DefaultVertexFormat.POSITION_COLOR,
                    VertexFormat.Mode.QUADS,
                    256,
                    false,
                    true,
                    state);
        }
        return HOLOGRAM;
    }

    public static RenderType railElectricLines() {
        if (RAIL_ELECTRIC_LINES == null) {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> DCPShaders.RAIL_ELECTRIC))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(true);
            RAIL_ELECTRIC_LINES = RenderType.create("dysoncubeproject_rail_electric_lines",
                    DefaultVertexFormat.POSITION_COLOR,
                    VertexFormat.Mode.LINES,
                    256,
                    false,
                    true,
                    state);
        }
        return RAIL_ELECTRIC_LINES;
    }
}
