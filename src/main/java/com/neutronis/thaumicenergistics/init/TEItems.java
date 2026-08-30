package com.neutronis.thaumicenergistics.init;

import appeng.items.parts.PartItem;
import appeng.parts.automation.ExportBusPart;
import appeng.parts.automation.ImportBusPart;
import appeng.parts.automation.StorageLevelEmitterPart;
import appeng.parts.storagebus.StorageBusPart;
import com.neutronis.thaumicenergistics.ThaumicEnergistics;
import com.neutronis.thaumicenergistics.item.EssentiaStorageCell;
import com.neutronis.thaumicenergistics.item.LegacyNamedItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Visible/content registration for the modern Thaumic Energistics port. */
public final class TEItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThaumicEnergistics.MOD_ID);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB, ThaumicEnergistics.MOD_ID);

    public static final DeferredItem<LegacyNamedItem> DIFFUSION_CORE = ITEMS.register(
            "diffusion_core",
            () -> new LegacyNamedItem(new Item.Properties(), "diffusion_core"));
    public static final DeferredItem<LegacyNamedItem> COALESCENCE_CORE = ITEMS.register(
            "coalescence_core",
            () -> new LegacyNamedItem(new Item.Properties(), "coalescence_core"));

    public static final DeferredItem<Item> ESSENTIA_COMPONENT_1K = ITEMS.registerSimpleItem("essentia_component_1k");
    public static final DeferredItem<Item> ESSENTIA_COMPONENT_4K = ITEMS.registerSimpleItem("essentia_component_4k");
    public static final DeferredItem<Item> ESSENTIA_COMPONENT_16K = ITEMS.registerSimpleItem("essentia_component_16k");
    public static final DeferredItem<Item> ESSENTIA_COMPONENT_64K = ITEMS.registerSimpleItem("essentia_component_64k");
    public static final DeferredItem<Item> ESSENTIA_COMPONENT_256K = ITEMS.registerSimpleItem("essentia_component_256k");
    public static final DeferredItem<Item> ESSENTIA_COMPONENT_1024K = ITEMS.registerSimpleItem("essentia_component_1024k");

    public static final DeferredItem<EssentiaStorageCell> ESSENTIA_CELL_1K = ITEMS.register(
            "essentia_cell_1k",
            () -> new EssentiaStorageCell(new Item.Properties().stacksTo(1), 0.5, 1, 8));
    public static final DeferredItem<EssentiaStorageCell> ESSENTIA_CELL_4K = ITEMS.register(
            "essentia_cell_4k",
            () -> new EssentiaStorageCell(new Item.Properties().stacksTo(1), 1.0, 4, 32));
    public static final DeferredItem<EssentiaStorageCell> ESSENTIA_CELL_16K = ITEMS.register(
            "essentia_cell_16k",
            () -> new EssentiaStorageCell(new Item.Properties().stacksTo(1), 1.5, 16, 128));
    public static final DeferredItem<EssentiaStorageCell> ESSENTIA_CELL_64K = ITEMS.register(
            "essentia_cell_64k",
            () -> new EssentiaStorageCell(new Item.Properties().stacksTo(1), 2.0, 64, 512));
    public static final DeferredItem<EssentiaStorageCell> ESSENTIA_CELL_256K = ITEMS.register(
            "essentia_cell_256k",
            () -> new EssentiaStorageCell(new Item.Properties().stacksTo(1), 2.5, 256, 2048));
    public static final DeferredItem<EssentiaStorageCell> ESSENTIA_CELL_1024K = ITEMS.register(
            "essentia_cell_1024k",
            () -> new EssentiaStorageCell(new Item.Properties().stacksTo(1), 3.0, 1024, 8192));

    /*
     * Keep the classic Thaumic Energistics bus items and Thaumonomicon progression.
     * Their placed parts deliberately reuse AE2's proven modern bus implementations.
     * Because EssentiaKeyType registers native import/export/external-storage strategies,
     * these legacy-looking parts are fully functional with essentia instead of being dummies.
     */
    public static final DeferredItem<PartItem<ImportBusPart>> ESSENTIA_IMPORT = ITEMS.register(
            "essentia_import",
            () -> new PartItem<>(new Item.Properties(), ImportBusPart.class, ImportBusPart::new));
    public static final DeferredItem<PartItem<ExportBusPart>> ESSENTIA_EXPORT = ITEMS.register(
            "essentia_export",
            () -> new PartItem<>(new Item.Properties(), ExportBusPart.class, ExportBusPart::new));
    public static final DeferredItem<PartItem<StorageBusPart>> ESSENTIA_STORAGE = ITEMS.register(
            "essentia_storage",
            () -> new PartItem<>(new Item.Properties(), StorageBusPart.class, StorageBusPart::new));
    public static final DeferredItem<PartItem<StorageLevelEmitterPart>> ESSENTIA_LEVEL_EMITTER = ITEMS.register(
            "essentia_level_emitter",
            () -> new PartItem<>(new Item.Properties(), StorageLevelEmitterPart.class, StorageLevelEmitterPart::new));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = TABS.register(
            "main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.ThaumicEnergistics"))
                    .icon(() -> new ItemStack(ESSENTIA_CELL_1024K.get()))
                    .displayItems((parameters, output) -> ITEMS.getEntries().forEach(item -> output.accept(item.get())))
                    .build());

    private TEItems() {
    }

    public static void initialize(IEventBus bus) {
        ITEMS.register(bus);
        TABS.register(bus);
    }
}
