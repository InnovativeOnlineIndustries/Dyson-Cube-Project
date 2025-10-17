package com.buuz135.dysoncubeproject;

import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ConfigFile
public class Config {

    @ConfigVal(comment = "The maximum number of solar panels the Dyson Sphere can have")
    @ConfigVal.InRangeInt(min = 1)
    public static int MAX_SOLAR_PANELS = 50_000_000;

    @ConfigVal(comment = "How many solar panels each beam can support")
    @ConfigVal.InRangeInt(min = 1)
    public static int BEAM_TO_SOLAR_PANEL_RATIO = 6;

    @ConfigVal(comment = "The amount of power generated per sail")
    @ConfigVal.InRangeInt(min = 0)
    public static int POWER_PER_SAIL = 20;

    @ConfigVal(comment = "Always show sphere at max progress")
    public static boolean SHOW_AT_MAX_PROGRESS = false;

    @ConfigVal(comment = "The power that the ray receiver can extract from the sphere every tick")
    @ConfigVal.InRangeInt(min = 1)
    public static int RAY_RECEIVER_EXTRACT_POWER = 5_000_000;
}
