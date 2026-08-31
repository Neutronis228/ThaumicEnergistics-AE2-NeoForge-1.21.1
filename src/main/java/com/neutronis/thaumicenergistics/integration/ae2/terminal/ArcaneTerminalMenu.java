package com.neutronis.thaumicenergistics.integration.ae2.terminal;

import java.util.List;

import com.google.common.base.Preconditions;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.stacks.AEItemKey;
import appeng.helpers.ICraftingGridMenu;
import appeng.helpers.InventoryAction;
import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.crafting.CraftConfirmMenu;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.CraftingMatrixSlot;
import appeng.me.storage.LinkStatusRespectingInventory;
import appeng.util.inv.PlayerInternalInventory;
import com.neutronis.thaumicenergistics.ThaumicEnergistics;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import appeng.core.network.serverbound.InventoryActionPacket;
import thaumcraft.common.crafting.arcane.TCArcaneWorkbenchCrafting;

/** Crafting-terminal menu extended with primal crystals, aura state and an Arcane Charger slot. */
public final class ArcaneTerminalMenu extends MEStorageMenu implements ICraftingGridMenu {
    public static final SlotSemantic ARCANE_CRYSTAL = SlotSemantics.register(
            ThaumicEnergistics.MOD_ID + ":ARCANE_CRYSTAL",
            false,
            50);

    private final ArcaneTerminalPart terminal;
    private final CraftingMatrixSlot[] craftingSlots = new CraftingMatrixSlot[9];
    private final ArcaneCraftingTermSlot outputSlot;
    private final DataSlot availableVis = DataSlot.standalone();
    private final DataSlot chargerInstalled = DataSlot.standalone();
    private TCArcaneWorkbenchCrafting.ResolvedCraft currentCraft = TCArcaneWorkbenchCrafting.ResolvedCraft.empty();

    public ArcaneTerminalMenu(
            MenuType<ArcaneTerminalMenu> type,
            int id,
            Inventory playerInventory,
            ArcaneTerminalPart terminal) {
        super(type, id, playerInventory, terminal, true);
        this.terminal = terminal;

        InternalInventory matrix = terminal.getSubInventory(ArcaneTerminalPart.INV_CRAFTING);
        for (int slot = 0; slot < craftingSlots.length; slot++) {
            addSlot(craftingSlots[slot] = new CraftingMatrixSlot(this, matrix, slot), SlotSemantics.CRAFTING_GRID);
        }

        var linkedStorage = new LinkStatusRespectingInventory(terminal.getInventory(), this::getLinkStatus);
        outputSlot = new ArcaneCraftingTermSlot(
                playerInventory.player,
                getActionSource(),
                energySource,
                linkedStorage,
                matrix,
                this,
                terminal);
        addSlot(outputSlot, SlotSemantics.CRAFTING_RESULT);

        InternalInventory crystals = terminal.getSubInventory(ArcaneTerminalPart.INV_CRYSTALS);
        for (int slot = 0; slot < crystals.size(); slot++) {
            addSlot(new AppEngSlot(crystals, slot), ARCANE_CRYSTAL);
        }
        setupUpgrades(terminal.getUpgrades());
        addDataSlot(availableVis);
        addDataSlot(chargerInstalled);
        updateOutput();
    }

    public ArcaneTerminalPart terminal() {
        return terminal;
    }

    public TCArcaneWorkbenchCrafting.ResolvedCraft currentCraft() {
        return currentCraft;
    }

    public int availableVis() {
        return availableVis.get();
    }

    public boolean hasArcaneCharger() {
        return chargerInstalled.get() != 0;
    }

    @Override
    public void broadcastChanges() {
        if (!isClientSide()) {
            availableVis.set(terminal.availableVis());
            chargerInstalled.set(terminal.hasArcaneCharger() ? 1 : 0);
            updateOutput();
        }
        super.broadcastChanges();
    }

    @Override
    public void slotsChanged(Container inventory) {
        updateOutput();
    }

    private void updateOutput() {
        if (getPlayer() instanceof ServerPlayer serverPlayer) {
            currentCraft = TCArcaneWorkbenchCrafting.resolve(serverPlayer, terminal);
            outputSlot.set(currentCraft.output());
        }
    }

    @Override
    public IEnergySource getEnergySource() {
        return energySource;
    }

    @Override
    public InternalInventory getCraftingMatrix() {
        return terminal.getSubInventory(ArcaneTerminalPart.INV_CRAFTING);
    }

    @Override
    public void startAutoCrafting(List<AutoCraftEntry> toCraft) {
        CraftConfirmMenu.openWithCraftingList(getActionHost(), (ServerPlayer) getPlayer(), getLocator(), toCraft);
    }

    public void clearCraftingGrid() {
        Preconditions.checkState(isClientSide());
        PacketDistributor.sendToServer(new InventoryActionPacket(
                InventoryAction.MOVE_REGION,
                craftingSlots[0].index,
                0));
    }

    public void clearToPlayerInventory() {
        if (isClientSide()) {
            return;
        }
        InternalInventory matrix = getCraftingMatrix();
        PlayerInternalInventory player = new PlayerInternalInventory(getPlayerInventory());
        for (int slot = 0; slot < matrix.size(); slot++) {
            matrix.setItemDirect(slot, player.addItems(matrix.getStackInSlot(slot)));
        }
    }
}
