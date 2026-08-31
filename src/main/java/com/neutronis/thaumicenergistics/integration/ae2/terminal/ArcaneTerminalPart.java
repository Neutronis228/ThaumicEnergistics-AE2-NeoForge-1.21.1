package com.neutronis.thaumicenergistics.integration.ae2.terminal;

import java.util.List;

import appeng.api.inventories.InternalInventory;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.parts.PartModel;
import appeng.parts.reporting.CraftingTerminalPart;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.filter.IAEItemFilter;
import com.neutronis.thaumicenergistics.ThaumicEnergistics;
import com.neutronis.thaumicenergistics.init.TEItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.crafting.IArcaneWorkbench;
import thaumcraft.common.crafting.arcane.TCArcaneWorkbenchCrafting;

/**
 * AE2 crafting-terminal host with the full Thaumcraft arcane-workbench inventory/aura contract.
 */
public final class ArcaneTerminalPart extends CraftingTerminalPart implements IArcaneWorkbench {
    public static final ResourceLocation INV_CRYSTALS = ThaumicEnergistics.id("arcane_terminal_crystals");
    public static final ResourceLocation INV_UPGRADES = ThaumicEnergistics.id("arcane_terminal_upgrades");
    public static final ResourceLocation MODEL_OFF = ThaumicEnergistics.id("part/arcane_terminal_off");
    public static final ResourceLocation MODEL_ON = ThaumicEnergistics.id("part/arcane_terminal_on");
    private static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    private static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    private static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    private final AppEngInternalInventory crystals = new AppEngInternalInventory(this, CRYSTAL_SLOT_COUNT, 64);
    private final IUpgradeInventory upgrades;

    public ArcaneTerminalPart(IPartItem<?> partItem) {
        super(partItem);
        crystals.setFilter(new IAEItemFilter() {
            @Override
            public boolean allowInsert(InternalInventory inventory, int slot, ItemStack stack) {
                return TCArcaneWorkbenchCrafting.isCrystal(
                        stack,
                        TCArcaneWorkbenchCrafting.PRIMAL_ASPECT_ORDER.get(slot));
            }
        });
        upgrades = UpgradeInventories.forMachine(partItem, 1, this::saveChanges);
    }

    public IUpgradeInventory getUpgrades() {
        return upgrades;
    }

    @Override
    public MenuType<?> getMenuType(Player player) {
        return com.neutronis.thaumicenergistics.init.TEMenus.ARCANE_TERMINAL.get();
    }

    public boolean hasArcaneCharger() {
        return upgrades.isInstalled(TEItems.UPGRADE_ARCANE.get());
    }

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean wrenched) {
        super.addAdditionalDrops(drops, wrenched);
        addInventoryDrops(drops, crystals);
        addInventoryDrops(drops, upgrades);
    }

    private static void addInventoryDrops(List<ItemStack> drops, InternalInventory inventory) {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                drops.add(stack);
            }
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        crystals.clear();
        upgrades.clear();
    }

    @Override
    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.readFromNBT(data, registries);
        crystals.readFromNBT(data, "arcaneCrystals", registries);
        upgrades.readFromNBT(data, "arcaneUpgrades", registries);
    }

    @Override
    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.writeToNBT(data, registries);
        crystals.writeToNBT(data, "arcaneCrystals", registries);
        upgrades.writeToNBT(data, "arcaneUpgrades", registries);
    }

    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if (INV_CRYSTALS.equals(id)) {
            return crystals;
        }
        if (INV_UPGRADES.equals(id)) {
            return upgrades;
        }
        return super.getSubInventory(id);
    }

    @Override
    public CraftingInput craftingInput() {
        InternalInventory matrix = super.getSubInventory(INV_CRAFTING);
        NonNullList<ItemStack> input = NonNullList.withSize(MATRIX_SLOT_COUNT, ItemStack.EMPTY);
        for (int slot = 0; slot < MATRIX_SLOT_COUNT; slot++) {
            input.set(slot, matrix.getStackInSlot(slot).copy());
        }
        return CraftingInput.of(3, 3, input);
    }

    @Override
    public Level arcaneLevel() {
        return getLevel();
    }

    @Override
    public BlockPos arcanePosition() {
        return getBlockEntity().getBlockPos();
    }

    @Override
    public int availableVis() {
        Level level = arcaneLevel();
        if (!(level instanceof ServerLevel)) {
            return 0;
        }
        if (!hasArcaneCharger()) {
            return (int) AuraHelper.getVis(level, arcanePosition());
        }
        int total = 0;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                total += (int) AuraHelper.getVis(level, arcanePosition().offset(x * 16, 0, z * 16));
            }
        }
        return total;
    }

    @Override
    public boolean canSpendVis(int amount) {
        return amount <= 0 || arcaneLevel() instanceof ServerLevel && availableVis() >= amount;
    }

    @Override
    public boolean spendVis(int amount) {
        if (amount <= 0) {
            return true;
        }
        Level level = arcaneLevel();
        if (!(level instanceof ServerLevel) || !canSpendVis(amount)) {
            return false;
        }
        if (!hasArcaneCharger()) {
            return AuraHelper.drainVis(level, arcanePosition(), amount, false) >= amount;
        }
        int remaining = amount;
        while (remaining > 0) {
            boolean drainedAny = false;
            for (int x = -1; x <= 1 && remaining > 0; x++) {
                for (int z = -1; z <= 1 && remaining > 0; z++) {
                    int request = Math.max(1, (remaining + 8) / 9);
                    int drained = (int) AuraHelper.drainVis(
                            level,
                            arcanePosition().offset(x * 16, 0, z * 16),
                            request,
                            false);
                    remaining -= drained;
                    drainedAny |= drained > 0;
                }
            }
            if (!drainedAny) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getArcaneItem(int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) {
            return ItemStack.EMPTY;
        }
        if (slot < CRYSTAL_SLOT_START) {
            return super.getSubInventory(INV_CRAFTING).getStackInSlot(slot);
        }
        return crystals.getStackInSlot(slot - CRYSTAL_SLOT_START);
    }

    @Override
    public ItemStack removeArcaneItem(int slot, int amount) {
        if (slot < 0 || slot >= SLOT_COUNT) {
            return ItemStack.EMPTY;
        }
        if (slot < CRYSTAL_SLOT_START) {
            return super.getSubInventory(INV_CRAFTING).extractItem(slot, amount, false);
        }
        return crystals.extractItem(slot - CRYSTAL_SLOT_START, amount, false);
    }

    @Override
    public void setArcaneItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= SLOT_COUNT) {
            return;
        }
        if (slot < CRYSTAL_SLOT_START) {
            super.getSubInventory(INV_CRAFTING).setItemDirect(slot, stack);
        } else {
            crystals.setItemDirect(slot - CRYSTAL_SLOT_START, stack);
        }
    }

    @Override
    public void setArcaneChanged() {
        saveChanges();
    }

    @Override
    public IPartModel getStaticModels() {
        return selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }
}
