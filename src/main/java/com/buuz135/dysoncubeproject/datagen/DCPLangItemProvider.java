package com.buuz135.dysoncubeproject.datagen;


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
        this.add("itemGroup.dysonshpereprogram", "Dyson Sphere Program");

    }

    private void formatItem(Item item) {
        this.add(item, WordUtils.capitalize(BuiltInRegistries.ITEM.getKey(item).getPath().replace("_", " ")));
    }
}
