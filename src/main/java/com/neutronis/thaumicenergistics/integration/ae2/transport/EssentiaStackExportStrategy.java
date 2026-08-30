package com.neutronis.thaumicenergistics.integration.ae2.transport;

import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import com.neutronis.thaumicenergistics.ThaumicEnergistics;
import com.neutronis.thaumicenergistics.integration.ae2.EssentiaKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.essentia.transport.TCEssentiaCapabilities;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;

/** Moves essentia from the ME network into Thaumcraft endpoints using AE2's normal Export Bus. */
public final class EssentiaStackExportStrategy implements StackExportStrategy {
    private final BlockCapabilityCache<TCEssentiaTransport, Direction> cache;
    private final Direction side;

    public EssentiaStackExportStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        this.cache = BlockCapabilityCache.create(TCEssentiaCapabilities.BLOCK, level, fromPos, fromSide);
        this.side = fromSide;
    }

    @Override
    public long transfer(StackTransferContext context, AEKey what, long maxAmount) {
        if (!(what instanceof EssentiaKey key) || Aspect.getAspect(key.aspectTag()) == null || maxAmount <= 0) {
            return 0;
        }

        var transport = cache.getCapability();
        if (transport == null || !transport.canInputFrom(side)) {
            return 0;
        }

        var network = context.getInternalStorage().getInventory();
        long extracted = StorageHelper.poweredExtraction(
                context.getEnergySource(),
                network,
                what,
                maxAmount,
                context.getActionSource(),
                Actionable.SIMULATE);
        if (extracted <= 0) {
            return 0;
        }

        int canAccept = transport.addEssentia(
                key.aspectTag(),
                (int) Math.min(Integer.MAX_VALUE, extracted),
                side,
                true);
        if (canAccept <= 0) {
            return 0;
        }

        extracted = StorageHelper.poweredExtraction(
                context.getEnergySource(),
                network,
                what,
                canAccept,
                context.getActionSource(),
                Actionable.MODULATE);
        if (extracted <= 0) {
            return 0;
        }

        int inserted = transport.addEssentia(
                key.aspectTag(),
                (int) Math.min(Integer.MAX_VALUE, extracted),
                side,
                false);

        if (inserted < extracted) {
            long overflow = extracted - inserted;
            long restored = network.insert(what, overflow, Actionable.MODULATE, context.getActionSource());
            if (restored < overflow) {
                ThaumicEnergistics.LOGGER.error(
                        "Export bus overflow: {} essentia of {} could not be restored to the ME network",
                        overflow - restored,
                        key.aspectTag());
            }
        }

        return inserted;
    }

    @Override
    public long push(AEKey what, long maxAmount, Actionable mode) {
        if (!(what instanceof EssentiaKey key) || Aspect.getAspect(key.aspectTag()) == null || maxAmount <= 0) {
            return 0;
        }
        var transport = cache.getCapability();
        if (transport == null || !transport.canInputFrom(side)) {
            return 0;
        }
        return transport.addEssentia(
                key.aspectTag(),
                (int) Math.min(Integer.MAX_VALUE, maxAmount),
                side,
                mode.isSimulate());
    }
}
