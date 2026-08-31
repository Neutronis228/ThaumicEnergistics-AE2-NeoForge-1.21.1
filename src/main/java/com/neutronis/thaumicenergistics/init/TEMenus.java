package com.neutronis.thaumicenergistics.init;

import appeng.menu.implementations.MenuTypeBuilder;
import com.neutronis.thaumicenergistics.ThaumicEnergistics;
import com.neutronis.thaumicenergistics.integration.ae2.terminal.ArcaneTerminalMenu;
import com.neutronis.thaumicenergistics.integration.ae2.terminal.ArcaneTerminalPart;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class TEMenus {
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(
            Registries.MENU,
            ThaumicEnergistics.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<ArcaneTerminalMenu>> ARCANE_TERMINAL = MENUS.register(
            "arcane_terminal",
            () -> MenuTypeBuilder
                    .create(ArcaneTerminalMenu::new, ArcaneTerminalPart.class)
                    .buildUnregistered(ThaumicEnergistics.id("arcane_terminal")));

    private TEMenus() {
    }

    public static void initialize(IEventBus bus) {
        MENUS.register(bus);
    }
}
