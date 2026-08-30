package com.neutronis.thaumicenergistics.item;

import appeng.api.stacks.AEKey;
import appeng.items.storage.BasicStorageCell;
import com.neutronis.thaumicenergistics.integration.ae2.EssentiaKey;
import com.neutronis.thaumicenergistics.integration.ae2.EssentiaKeyType;
import net.minecraft.world.item.ItemStack;

/** Digital essentia cell preserving legacy behavior while allowing larger modern tiers. */
public final class EssentiaStorageCell extends BasicStorageCell {
    public static final int LEGACY_TYPE_LIMIT = 12;

    public EssentiaStorageCell(Properties properties, double idleDrain, int kilobytes, int bytesPerType) {
        this(properties, idleDrain, kilobytes, bytesPerType, LEGACY_TYPE_LIMIT);
    }

    public EssentiaStorageCell(
            Properties properties,
            double idleDrain,
            int kilobytes,
            int bytesPerType,
            int typeLimit) {
        super(properties, idleDrain, kilobytes, bytesPerType, typeLimit, EssentiaKeyType.INSTANCE);
    }

    @Override
    public boolean isBlackListed(ItemStack cellItem, AEKey requestedAddition) {
        return !(requestedAddition instanceof EssentiaKey);
    }
}
