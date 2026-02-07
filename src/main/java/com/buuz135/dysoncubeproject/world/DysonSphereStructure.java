package com.buuz135.dysoncubeproject.world;

import com.buuz135.dysoncubeproject.Config;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class DysonSphereStructure implements INBTSerializable<CompoundTag> {

    private int beams;
    private int solarPanels;
    private long storedPower;
    private long lastConsumedPower;

    public DysonSphereStructure() {
        this(0, 0);
    }

    public DysonSphereStructure(int beams, int solarPanels) {
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
        return beams * Config.BEAM_TO_SOLAR_PANEL_RATIO;
    }

    public double getProgress() {
        return solarPanels / (double) Config.MAX_SOLAR_PANELS;
    }

    public int getMaxBeams() {
        return Config.MAX_SOLAR_PANELS / Config.BEAM_TO_SOLAR_PANEL_RATIO;
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
        long powerGenerated = (long) this.solarPanels * Config.POWER_PER_SAIL;
        this.storedPower = Math.min(powerGenerated, this.storedPower + powerGenerated);
    }

    public long extractPower(long amount) {
        long extracted = Math.min(amount, this.storedPower);
        this.storedPower -= extracted;
        this.lastConsumedPower += extracted;
        return extracted;
    }

    public long getStoredPower() {
        return storedPower;
    }

    public long getLastConsumedPower() {
        return lastConsumedPower;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("beams", beams);
        compoundTag.putInt("solarPanels", solarPanels);
        compoundTag.putLong("storedPower", storedPower);
        compoundTag.putLong("lastConsumedPower", lastConsumedPower);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        this.beams = compoundTag.getInt("beams");
        this.solarPanels = compoundTag.getInt("solarPanels");
        this.storedPower = compoundTag.getLong("storedPower");
        this.lastConsumedPower = compoundTag.getLong("lastConsumedPower");
    }
}
