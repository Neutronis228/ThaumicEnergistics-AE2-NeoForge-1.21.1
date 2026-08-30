package com.neutronis.thaumicenergistics.init;

import com.neutronis.thaumicenergistics.ThaumicEnergistics;
import com.neutronis.thaumicenergistics.item.EssentiaStorageCell;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** First visible/content slice of the 1.21.1 port: native essentia storage cells. */
public final class TEItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThaumicEnergistics.MOD_ID);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB, ThaumicEnergistics.MOD_ID);

    public static final DeferredItem<Item> ESSENTIA_COMPONENT_1K = ITEMS.registerSimpleItem("essentia_component_1k");
    public static final DeferredItem<Item> ESSENTIA_COMPONENT_4K = ITEMS.registerSimpleItem("essentia_component_4k");
    public static final DeferredItem<Item> ESSENTIA_COMPONENT_16K = ITEMS.registerSimpleItem("essentia_component_16k");
    public static final DeferredItem<Item> ESSENTIA_COMPONENT_64K = ITEMS.registerSimpleItem("essentia_component_64k");

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

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = TABS.register(
            "main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.ThaumicEnergistics"))
                    .icon(() -> new ItemStack(ESSENTIA_CELL_64K.get()))
                    .displayItems((parameters, output) -> ITEMS.getEntries().forEach(item -> output.accept(item.get())))
                    .build());

    private TEItems() {
    }

    public static void initialize(IEventBus bus) {
        ITEMS.register(bus);
        TABS.register(bus);
    }
}
