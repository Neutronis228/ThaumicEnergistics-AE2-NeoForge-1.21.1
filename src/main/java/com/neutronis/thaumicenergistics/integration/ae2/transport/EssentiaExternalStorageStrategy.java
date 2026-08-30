package com.neutronis.thaumicenergistics.integration.ae2.transport;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.storage.MEStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.jetbrains.annotations.Nullable;
import thaumcraft.common.essentia.transport.TCEssentiaCapabilities;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;

/** Lets AE2 Storage Buses expose Thaumcraft jars and other essentia endpoints. */
public final class EssentiaExternalStorageStrategy implements ExternalStorageStrategy {
    private final BlockCapabilityCache<TCEssentiaTransport, Direction> cache;
    private final Direction side;

    public EssentiaExternalStorageStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        this.cache = BlockCapabilityCache.create(TCEssentiaCapabilities.BLOCK, level, fromPos, fromSide);
        this.side = fromSide;
    }

    @Nullable
    @Override
    public MEStorage createWrapper(boolean extractableOnly, Runnable injectOrExtractCallback) {
        var transport = cache.getCapability();
        if (transport == null) {
            return null;
        }
        return new EssentiaTransportFacade(transport, side, extractableOnly, injectOrExtractCallback);
    }
}
