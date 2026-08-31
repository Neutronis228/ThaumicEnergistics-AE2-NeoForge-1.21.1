package com.neutronis.thaumicenergistics;

import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.client.StorageCellModels;
import appeng.api.parts.PartModels;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;
import appeng.parts.automation.StackWorldBehaviors;
import com.neutronis.thaumicenergistics.client.EssentiaKeyRenderer;
import com.neutronis.thaumicenergistics.init.TEItems;
import com.neutronis.thaumicenergistics.integration.ae2.EssentiaKeyType;
import com.neutronis.thaumicenergistics.integration.ae2.terminal.ArcaneTerminalPart;
import com.neutronis.thaumicenergistics.integration.ae2.terminal.EssentiaTerminalPart;
import com.neutronis.thaumicenergistics.integration.ae2.transport.EssentiaExternalStorageStrategy;
import com.neutronis.thaumicenergistics.integration.ae2.transport.EssentiaStackExportStrategy;
import com.neutronis.thaumicenergistics.integration.ae2.transport.EssentiaStackImportStrategy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ThaumicEnergistics.MOD_ID)
public final class ThaumicEnergistics {
    public static final String MOD_ID = "thaumicenergistics";
    public static final Logger LOGGER = LoggerFactory.getLogger("ThaumicEnergistics");

    public ThaumicEnergistics(IEventBus bus) {
        TEItems.initialize(bus);

        bus.addListener((RegisterEvent event) -> {
            if (!event.getRegistryKey().equals(Registries.BLOCK)) {
                return;
            }
            AEKeyTypes.register(EssentiaKeyType.INSTANCE);
            LOGGER.info("Registered native AE2 essentia key type: {}", EssentiaKeyType.INSTANCE.getId());
        });

        GenericSlotCapacities.register(
                EssentiaKeyType.INSTANCE,
                GenericSlotCapacities.getMap().get(AEKeyType.fluids()));

        // Make the custom key space usable by the normal AE2 automation parts.
        // This lets both standard AE2 buses and the classic TE-labelled bus parts
        // interact with Thaumcraft jars, alembics and other essentia endpoints.
        StackWorldBehaviors.registerImportStrategy(EssentiaKeyType.INSTANCE, EssentiaStackImportStrategy::new);
        StackWorldBehaviors.registerExportStrategy(EssentiaKeyType.INSTANCE, EssentiaStackExportStrategy::new);
        StackWorldBehaviors.registerExternalStorageStrategy(
                EssentiaKeyType.INSTANCE,
                EssentiaExternalStorageStrategy::new);

        bus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(this::commonSetup));

        if (FMLEnvironment.dist == Dist.CLIENT) {
            EssentiaKeyRenderer.initialize(bus);
        }
    }

    private void commonSetup() {
        StorageCellModels.registerModel(TEItems.ESSENTIA_CELL_1K, id("block/drive/cells/essentia_cell_1k"));
        StorageCellModels.registerModel(TEItems.ESSENTIA_CELL_4K, id("block/drive/cells/essentia_cell_4k"));
        StorageCellModels.registerModel(TEItems.ESSENTIA_CELL_16K, id("block/drive/cells/essentia_cell_16k"));
        StorageCellModels.registerModel(TEItems.ESSENTIA_CELL_64K, id("block/drive/cells/essentia_cell_64k"));
        // Higher tiers intentionally reuse the 64K drive-face artwork for now.
        StorageCellModels.registerModel(TEItems.ESSENTIA_CELL_256K, id("block/drive/cells/essentia_cell_64k"));
        StorageCellModels.registerModel(TEItems.ESSENTIA_CELL_1024K, id("block/drive/cells/essentia_cell_64k"));
        PartModels.registerModels(
                EssentiaTerminalPart.MODEL_OFF,
                EssentiaTerminalPart.MODEL_ON,
                ArcaneTerminalPart.MODEL_OFF,
                ArcaneTerminalPart.MODEL_ON);
        LOGGER.info("Thaumic Energistics essentia storage, legacy buses and terminal foundations initialized");
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
