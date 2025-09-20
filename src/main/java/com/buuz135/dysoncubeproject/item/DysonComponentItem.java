package com.buuz135.dysoncubeproject.item;

import com.buuz135.dysoncubeproject.DCPAttachments;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DysonComponentItem extends Item {

    public DysonComponentItem(int solarSail, int beam, TitaniumTab tab) {
        super(new Properties().component(DCPAttachments.SOLAR_SAIL, solarSail).component(DCPAttachments.BEAM, beam));
        tab.getTabList().add(this);
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        super.verifyComponentsAfterLoad(stack);
    }
}
