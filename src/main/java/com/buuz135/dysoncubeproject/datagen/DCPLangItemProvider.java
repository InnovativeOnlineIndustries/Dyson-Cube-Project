package com.buuz135.dysoncubeproject.datagen;


import com.buuz135.dysoncubeproject.DCPContent;
import com.buuz135.dysoncubeproject.DysonCubeProject;
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
        this.add("gui.dysoncubeproject.unsubscribe", "Unsubscribe to all spheres (You will return to your personal sphere)");
        this.add("tooltip.dysoncubeproject.contains_solar_sails", "Contains %s solar sail(s)");
        this.add("tooltip.dysoncubeproject.contains_beams", "Contains %s beam(s)");
        this.add("tooltip.dysoncubeproject.power_optional", "*Power Optional, with power it allows to ramp up how many beams/sails are ejected*");
        this.add("tooltip.dysoncubeproject.power_mandatory", "*Power Mandatory, with power it allows to ramp up how many beams/sails are ejected*");
        formatAdvancement("root.title", "Dyson Cube Project");
        formatAdvancement("root.description", "TODAY WE STEAL THE SUN!");
        var amounts = new int[]{5, 15, 25, 50, 75, 100};
        var texts = new String[]{
                "The Sun Has Been Notified",
                "Mildly Concerning Solar Encirclement",
                "Quarter-Sphere, Full Ambition",
                "Halfway to Stellar Landlord",
                "The Sun Is Looking Nervous",
                "Congrats! You Own Daylight"
        };
        var descriptions = new String[]{
                "5% of the sails are ejected. The Sun barely noticed. Probably.",
                "15% of the sails are ejected. Local photons have filed a complaint.",
                "25% of the sails are ejected. One quarter of the way to cosmic nonsense.",
                "50% of the sails are ejected. Half a sphere, twice the hubris.",
                "75% of the sails are ejected. The Sun is officially checking its escape options.",
                "100% of the sails are ejected. Daylight is now a subscription service."
        };
        for (int i = 0; i < amounts.length; i++) {
            formatAdvancement("em_railejector_controller/sphere_percentage_" + amounts[i] + ".title", texts[i]);
            formatAdvancement("em_railejector_controller/sphere_percentage_" + amounts[i] + ".description", descriptions[i]);
        }
    }

    private void formatItem(Item item) {
        this.add(item, WordUtils.capitalize(BuiltInRegistries.ITEM.getKey(item).getPath().replace("_", " ")));
    }

    private void formatAdvancement(String key, String value) {
        this.add("advancement." + DysonCubeProject.MODID + "." + key, value);
    }
}
