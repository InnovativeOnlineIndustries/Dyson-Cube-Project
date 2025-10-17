package com.buuz135.dysoncubeproject.datagen;


import com.buuz135.dysoncubeproject.DCPContent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

public class DCPLangItemProvider extends LanguageProvider {

    public DCPLangItemProvider(DataGenerator gen, String modid, String locale) {
        super(gen.getPackOutput(), modid, locale);
    }

    @Override
    protected void addTranslations() {
        this.add("itemGroup.dyson_cube_project", "Dyson Cube Project");
        this.add(DCPContent.Blocks.EM_RAILEJECTOR_CONTROLLER.asItem(), "EM Rail Ejector Controller");
        this.add(DCPContent.Blocks.RAY_RECEIVER_CONTROLLER.asItem(), "Ray Receiver Controller");
        formatItem(DCPContent.Blocks.MULTIBLOCK_STRUCTURE.asItem());
        formatItem(DCPContent.Items.BEAM.get());
        formatItem(DCPContent.Items.BEAM_PACKAGE.get());
        formatItem(DCPContent.Items.SOLAR_SAIL.get());
        formatItem(DCPContent.Items.SOLAR_SAIL_PACKAGE.get());

        // GUI localization for DysonProgressGuiAddon
        this.add("gui.dysoncubeproject.dyson_information", "Dyson Information");
        this.add("gui.dysoncubeproject.progress", "Progress: %s%%");
        this.add("gui.dysoncubeproject.power_gen", "Power Gen: %s FE");
        this.add("gui.dysoncubeproject.power_con", "Power Con: %s FE");
        this.add("gui.dysoncubeproject.beams", "Beams: %s");
        this.add("gui.dysoncubeproject.sails", "Sails: %s/%s");
        this.add("gui.dysoncubeproject.needs_more_beams", "Needs more beams");
        this.add("gui.dysoncubeproject.subscribe", "Subscribe to this sphere");
        this.add("tooltip.dysoncubeproject.contains_solar_sails", "Contains %s solar sail(s)");
        this.add("tooltip.dysoncubeproject.contains_beams", "Contains %s beam(s)");
    }

    private void formatItem(Item item) {
        this.add(item, WordUtils.capitalize(BuiltInRegistries.ITEM.getKey(item).getPath().replace("_", " ")));
    }
}
