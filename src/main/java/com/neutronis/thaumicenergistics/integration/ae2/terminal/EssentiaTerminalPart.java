package com.neutronis.thaumicenergistics.integration.ae2.terminal;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.parts.PartModel;
import appeng.parts.reporting.ItemTerminalPart;
import com.neutronis.thaumicenergistics.ThaumicEnergistics;
import net.minecraft.resources.ResourceLocation;

/**
 * Native AE2 storage terminal with Thaumic Energistics identity.
 *
 * <p>AE2's terminal menu is key-type aware, so the registered Essentia key type is selected and synchronized by
 * AE2's server-authoritative terminal protocol instead of maintaining a second, fragile packet stack.</p>
 */
public final class EssentiaTerminalPart extends ItemTerminalPart {
    public static final ResourceLocation MODEL_OFF = ThaumicEnergistics.id("part/essentia_terminal_off");
    public static final ResourceLocation MODEL_ON = ThaumicEnergistics.id("part/essentia_terminal_on");
    private static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    private static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    private static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    public EssentiaTerminalPart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public IPartModel getStaticModels() {
        return selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }
}
