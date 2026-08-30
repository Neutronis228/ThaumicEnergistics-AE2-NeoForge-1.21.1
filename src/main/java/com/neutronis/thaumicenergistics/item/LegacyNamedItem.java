package com.neutronis.thaumicenergistics.item;

import com.neutronis.thaumicenergistics.ThaumicEnergistics;
import net.minecraft.world.item.Item;

/** Uses the original Thaumic Energistics 1.12-style .name localization keys. */
public class LegacyNamedItem extends Item {
    private final String legacyPath;

    public LegacyNamedItem(Properties properties, String legacyPath) {
        super(properties);
        this.legacyPath = legacyPath;
    }

    @Override
    public String getDescriptionId() {
        return "item." + ThaumicEnergistics.MOD_ID + "." + legacyPath + ".name";
    }
}
