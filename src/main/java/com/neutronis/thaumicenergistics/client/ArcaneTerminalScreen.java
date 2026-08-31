package com.neutronis.thaumicenergistics.client;

import appeng.api.config.ActionItems;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ActionButton;
import appeng.core.AEConfig;
import com.neutronis.thaumicenergistics.integration.ae2.terminal.ArcaneTerminalMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class ArcaneTerminalScreen extends MEStorageScreen<ArcaneTerminalMenu> {
    public ArcaneTerminalScreen(
            ArcaneTerminalMenu menu,
            Inventory playerInventory,
            Component title,
            ScreenStyle style) {
        super(menu, playerInventory, title, style);

        ActionButton clear = new ActionButton(ActionItems.S_STASH, button -> menu.clearCraftingGrid());
        clear.setHalfSize(true);
        clear.setDisableBackground(true);
        widgets.add("clearCraftingGrid", clear);
    }

    @Override
    public void drawFG(GuiGraphics graphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(graphics, offsetX, offsetY, mouseX, mouseY);
        Component vis = Component.translatable(
                "gui.thaumicenergistics.vis_available",
                menu.availableVis());
        graphics.drawString(font, vis, 113, imageHeight - 121, ChatFormatting.DARK_PURPLE.getColor(), false);
        if (menu.hasArcaneCharger()) {
            graphics.drawString(
                    font,
                    Component.translatable("gui.thaumicenergistics.charger_active"),
                    113,
                    imageHeight - 111,
                    ChatFormatting.DARK_GREEN.getColor(),
                    false);
        }
    }

    @Override
    public void onClose() {
        if (AEConfig.instance().isClearGridOnClose()) {
            menu.clearCraftingGrid();
        }
        super.onClose();
    }
}
