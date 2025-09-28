package com.buuz135.dysoncubeproject.world;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class DysonSphereConfiguration implements INBTSerializable<CompoundTag> {

    public static final int MAX_SOLAR_PANELS = 50_000_000;
    public static final int BEAM_TO_SOLAR_PANEL_RATIO = 6;
    public static final int POWER_PER_SOLAR_PANEL = 20;

    private int beams;
    private int solarPanels;
    private int storedPower;
    private int lastConsumedPower;

    public DysonSphereConfiguration() {
        this(0, 0);
    }

    public DysonSphereConfiguration(int beams, int solarPanels) {
        this.beams = beams;
        this.solarPanels = solarPanels;
    }

    public int getBeams() {
        return beams;
    }

    public void setBeams(int beams) {
        this.beams = beams;
    }

    public int getSolarPanels() {
        return solarPanels;
    }

    public void setSolarPanels(int solarPanels) {
        this.solarPanels = solarPanels;
    }

    public int getMaxSolarPanels() {
        return beams * BEAM_TO_SOLAR_PANEL_RATIO;
    }

    public double getProgress() {
        return solarPanels / (double) MAX_SOLAR_PANELS;
    }

    public int getMaxBeams() {
        return MAX_SOLAR_PANELS / BEAM_TO_SOLAR_PANEL_RATIO;
    }

    public void increaseBeams(int amount) {
        this.beams += amount;
        if (this.beams > getMaxBeams()) this.beams = getMaxBeams();
    }

    public void increaseSolarPanels(int amount) {
        this.solarPanels += amount;
        if (this.solarPanels > getMaxSolarPanels()) this.solarPanels = getMaxSolarPanels();
    }

    public void generatePower() {
        this.lastConsumedPower = 0;
        this.storedPower = Math.min(this.solarPanels * POWER_PER_SOLAR_PANEL, this.storedPower + this.solarPanels * POWER_PER_SOLAR_PANEL);
    }

    public int extractPower(int amount) {
        int extracted = Math.min(amount, this.storedPower);
        this.storedPower -= extracted;
        this.lastConsumedPower += extracted;
        return extracted;
    }

    public int getStoredPower() {
        return storedPower;
    }

    public int getLastConsumedPower() {
        return lastConsumedPower;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("beams", beams);
        compoundTag.putInt("solarPanels", solarPanels);
        compoundTag.putInt("storedPower", storedPower);
        compoundTag.putInt("lastConsumedPower", lastConsumedPower);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        this.beams = compoundTag.getInt("beams");
        this.solarPanels = compoundTag.getInt("solarPanels");
        this.storedPower = compoundTag.getInt("storedPower");
        this.lastConsumedPower = compoundTag.getInt("lastConsumedPower");
    }
}
