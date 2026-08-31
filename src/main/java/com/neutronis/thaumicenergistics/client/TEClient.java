package com.neutronis.thaumicenergistics.client;

import appeng.init.client.InitScreens;
import com.neutronis.thaumicenergistics.init.TEMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public final class TEClient {
    private TEClient() {
    }

    public static void initialize(IEventBus bus) {
        bus.addListener(TEClient::registerScreens);
    }

    private static void registerScreens(RegisterMenuScreensEvent event) {
        InitScreens.register(
                event,
                TEMenus.ARCANE_TERMINAL.get(),
                ArcaneTerminalScreen::new,
                "/screens/terminals/arcane_terminal.json");
    }
}
