package com.neutronis.thaumicenergistics.integration.ae2.terminal;

import java.util.List;

import appeng.api.config.Actionable;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import appeng.helpers.InventoryAction;
import appeng.menu.slot.CraftingTermSlot;
import appeng.util.Platform;
import appeng.util.inv.CarriedItemInventory;
import appeng.util.inv.PlayerInternalInventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.crafting.IArcaneWorkbench;
import thaumcraft.common.crafting.arcane.TCArcaneWorkbenchCrafting;

/**
 * AE2 result slot that commits Thaumcraft arcane recipes through the same atomic server helper as a physical workbench.
 */
public final class ArcaneCraftingTermSlot extends CraftingTermSlot {
    private final IActionSource actionSource;
    private final MEStorage storage;
    private final ArcaneTerminalMenu menu;
    private final ArcaneTerminalPart terminal;

    public ArcaneCraftingTermSlot(
            Player player,
            IActionSource actionSource,
            IEnergySource energySource,
            MEStorage storage,
            InternalInventory matrix,
            ArcaneTerminalMenu menu,
            ArcaneTerminalPart terminal) {
        super(player, actionSource, energySource, storage, matrix, matrix, menu);
        this.actionSource = actionSource;
        this.storage = storage;
        this.menu = menu;
        this.terminal = terminal;
    }

    @Override
    public void doClick(InventoryAction action, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || getItem().isEmpty()) {
            return;
        }

        InternalInventory target;
        int maxCrafts;
        int resultCount = Math.max(1, getItem().getCount());
        if (action == InventoryAction.CRAFT_SHIFT || action == InventoryAction.CRAFT_ALL) {
            target = new PlayerInternalInventory(player.getInventory());
            int stackCrafts = Math.max(1, getItem().getMaxStackSize() / resultCount);
            maxCrafts = action == InventoryAction.CRAFT_ALL ? stackCrafts * Inventory.INVENTORY_SIZE : stackCrafts;
        } else {
            target = new CarriedItemInventory(menu);
            maxCrafts = action == InventoryAction.CRAFT_STACK
                    ? Math.max(1, getItem().getMaxStackSize() / resultCount)
                    : 1;
        }

        ItemStack initialOutput = getItem().copy();
        for (int craft = 0; craft < maxCrafts; craft++) {
            TCArcaneWorkbenchCrafting.ResolvedCraft resolved = TCArcaneWorkbenchCrafting.resolve(serverPlayer, terminal);
            ItemStack output = resolved.output();
            if (output.isEmpty()
                    || !ItemStack.isSameItemSameComponents(initialOutput, output)
                    || !target.simulateAdd(output).isEmpty()) {
                break;
            }

            ItemStack[] before = snapshot(terminal);
            if (!TCArcaneWorkbenchCrafting.craft(serverPlayer, terminal, resolved)) {
                break;
            }

            ItemStack overflow = target.addItems(output);
            if (!overflow.isEmpty()) {
                Platform.spawnDrops(player.level(), player.blockPosition(), List.of(overflow));
                break;
            }
            replenishConsumed(before, terminal);
            menu.slotsChanged(menu.getCraftingMatrix().toContainer());
        }
    }

    private static ItemStack[] snapshot(IArcaneWorkbench workbench) {
        ItemStack[] result = new ItemStack[IArcaneWorkbench.SLOT_COUNT];
        for (int slot = 0; slot < result.length; slot++) {
            result[slot] = workbench.getArcaneItem(slot).copy();
        }
        return result;
    }

    private void replenishConsumed(ItemStack[] before, IArcaneWorkbench workbench) {
        for (int slot = 0; slot < before.length; slot++) {
            ItemStack template = before[slot];
            if (template.isEmpty()) {
                continue;
            }
            ItemStack current = workbench.getArcaneItem(slot);
            int missing;
            if (current.isEmpty()) {
                missing = template.getCount();
            } else if (ItemStack.isSameItemSameComponents(template, current)) {
                missing = Math.max(0, template.getCount() - current.getCount());
            } else {
                continue;
            }
            if (missing == 0) {
                continue;
            }

            AEItemKey key = AEItemKey.of(template);
            long extracted = storage.extract(key, missing, Actionable.MODULATE, actionSource);
            if (extracted <= 0) {
                continue;
            }
            ItemStack refill = key.toStack((int) extracted);
            current = workbench.getArcaneItem(slot);
            if (current.isEmpty()) {
                workbench.setArcaneItem(slot, refill);
            } else if (ItemStack.isSameItemSameComponents(current, refill)
                    && current.getCount() + refill.getCount() <= current.getMaxStackSize()) {
                ItemStack merged = current.copy();
                merged.grow(refill.getCount());
                workbench.setArcaneItem(slot, merged);
            } else {
                storage.insert(key, extracted, Actionable.MODULATE, actionSource);
            }
        }
        workbench.setArcaneChanged();
    }
}
