package com.neutronis.thaumicenergistics;

import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.client.StorageCellModels;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;
import com.neutronis.thaumicenergistics.init.TEItems;
import com.neutronis.thaumicenergistics.integration.ae2.EssentiaKeyType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
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

        bus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(this::commonSetup));
    }

    private void commonSetup() {
        StorageCellModels.registerModel(TEItems.ESSENTIA_CELL_1K, id("block/drive/cells/essentia_cell_1k"));
        StorageCellModels.registerModel(TEItems.ESSENTIA_CELL_4K, id("block/drive/cells/essentia_cell_4k"));
        StorageCellModels.registerModel(TEItems.ESSENTIA_CELL_16K, id("block/drive/cells/essentia_cell_16k"));
        StorageCellModels.registerModel(TEItems.ESSENTIA_CELL_64K, id("block/drive/cells/essentia_cell_64k"));
        LOGGER.info("Thaumic Energistics alpha1 storage foundation initialized");
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
