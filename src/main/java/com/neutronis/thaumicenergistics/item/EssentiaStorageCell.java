package com.neutronis.thaumicenergistics.item;

import appeng.api.stacks.AEKey;
import appeng.items.storage.BasicStorageCell;
import com.neutronis.thaumicenergistics.integration.ae2.EssentiaKey;
import com.neutronis.thaumicenergistics.integration.ae2.EssentiaKeyType;
import net.minecraft.world.item.ItemStack;

/** Digital essentia cell preserving the legacy 1K/4K/16K/64K behaviour. */
public final class EssentiaStorageCell extends BasicStorageCell {
    public EssentiaStorageCell(Properties properties, double idleDrain, int kilobytes, int bytesPerType) {
        super(properties, idleDrain, kilobytes, bytesPerType, 12, EssentiaKeyType.INSTANCE);
    }

    @Override
    public boolean isBlackListed(ItemStack cellItem, AEKey requestedAddition) {
        return !(requestedAddition instanceof EssentiaKey);
    }
}
