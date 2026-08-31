package com.neutronis.thaumicenergistics.integration.ae2.terminal;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.parts.PartModel;
import appeng.parts.reporting.CraftingTerminalPart;
import com.neutronis.thaumicenergistics.ThaumicEnergistics;
import net.minecraft.resources.ResourceLocation;

/**
 * Functional AE2 crafting-terminal foundation for the restored Arcane Terminal.
 *
 * <p>The normal AE2 crafting grid, inventory extraction, persistence and network synchronization work now. The
 * Thaumcraft arcane-recipe and aura-vis bridge is intentionally a following parity slice.</p>
 */
public final class ArcaneTerminalPart extends CraftingTerminalPart {
    public static final ResourceLocation MODEL_OFF = ThaumicEnergistics.id("part/arcane_terminal_off");
    public static final ResourceLocation MODEL_ON = ThaumicEnergistics.id("part/arcane_terminal_on");
    private static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    private static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    private static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    public ArcaneTerminalPart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public IPartModel getStaticModels() {
        return selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }
}
