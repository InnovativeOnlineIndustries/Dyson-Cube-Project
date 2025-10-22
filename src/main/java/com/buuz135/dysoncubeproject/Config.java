package com.buuz135.dysoncubeproject;

import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

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
    public static int RAY_RECEIVER_EXTRACT_POWER = 50_000_000;

    @ConfigVal(comment = "The power that the ray receiver buffer has")
    @ConfigVal.InRangeInt(min = 1)
    public static int RAY_RECEIVER_POWER_BUFFER = 100_000_000;

    @ConfigVal(comment = "The power that the em railejector buffer has")
    @ConfigVal.InRangeInt(min = 1)
    public static int RAIL_EJECTOR_POWER_BUFFER = 400_000;

    @ConfigVal(comment = "The power that the em railejector consumes each tick per sent item")
    @ConfigVal.InRangeInt(min = 1)
    public static int RAIL_EJECTOR_CONSUME = 40;
}
