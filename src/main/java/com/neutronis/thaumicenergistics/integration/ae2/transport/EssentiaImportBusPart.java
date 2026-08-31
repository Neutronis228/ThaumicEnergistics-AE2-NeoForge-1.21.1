package com.neutronis.thaumicenergistics.integration.ae2.transport;

import appeng.api.parts.IPartItem;
import appeng.api.stacks.GenericStack;
import appeng.parts.automation.ImportBusPart;
import com.neutronis.thaumicenergistics.integration.ae2.EssentiaKey;
import com.neutronis.thaumicenergistics.integration.ae2.EssentiaKeyType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.TCEssentiaItemHelper;

/** Import bus that accepts a filled phial as an essentia filter instead of an item filter. */
public final class EssentiaImportBusPart extends ImportBusPart {
    public EssentiaImportBusPart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public boolean onUseItemOn(ItemStack stack, Player player, InteractionHand hand, Vec3 hitPos) {
        Aspect aspect = TCEssentiaItemHelper.aspectFromStack(stack);
        if (!TCEssentiaItemHelper.isFilledPhial(stack) || aspect == null) {
            return super.onUseItemOn(stack, player, hand, hitPos);
        }
        if (!isClientSide()) {
            getKeyTypeSelection().setEnabled(EssentiaKeyType.INSTANCE, true);
            putFirstFilter(new GenericStack(EssentiaKey.of(aspect), 1));
            player.displayClientMessage(
                    Component.translatable("message.thaumicenergistics.bus_filter", aspect.getName()),
                    true
            );
        }
        return true;
    }

    private void putFirstFilter(GenericStack filter) {
        int slot = 0;
        for (int index = 0; index < getConfig().size(); index++) {
            if (getConfig().getStack(index) == null) {
                slot = index;
                break;
            }
        }
        getConfig().setStack(slot, filter);
    }
}
