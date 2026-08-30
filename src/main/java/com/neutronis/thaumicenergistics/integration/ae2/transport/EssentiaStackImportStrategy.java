package com.neutronis.thaumicenergistics.integration.ae2.transport;

import appeng.api.behaviors.StackImportStrategy;
import appeng.api.behaviors.StackTransferContext;
import appeng.api.config.Actionable;
import com.neutronis.thaumicenergistics.ThaumicEnergistics;
import com.neutronis.thaumicenergistics.integration.ae2.EssentiaKey;
import com.neutronis.thaumicenergistics.integration.ae2.EssentiaKeyType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.essentia.transport.TCEssentiaCapabilities;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;

/** Moves essentia from Thaumcraft transport endpoints into the ME network using AE2's normal Import Bus. */
public final class EssentiaStackImportStrategy implements StackImportStrategy {
    private final BlockCapabilityCache<TCEssentiaTransport, Direction> cache;
    private final Direction side;

    public EssentiaStackImportStrategy(ServerLevel level, BlockPos fromPos, Direction fromSide) {
        this.cache = BlockCapabilityCache.create(TCEssentiaCapabilities.BLOCK, level, fromPos, fromSide);
        this.side = fromSide;
    }

    @Override
    public boolean transfer(StackTransferContext context) {
        if (!context.isKeyTypeEnabled(EssentiaKeyType.INSTANCE)) {
            return false;
        }

        var transport = cache.getCapability();
        if (transport == null || !transport.canOutputTo(side)) {
            return false;
        }

        var visible = transport.getEssentia(side);
        if (visible == null || visible.isEmpty() || Aspect.getAspect(visible.aspect()) == null) {
            return false;
        }

        var key = EssentiaKey.of(visible.aspect());
        if (context.isInFilter(key) == context.isInverted()) {
            return false;
        }

        long maxForOps = (long) context.getOperationsRemaining() * EssentiaKeyType.INSTANCE.getAmountPerOperation();
        int requested = (int) Math.min(Integer.MAX_VALUE, Math.min(maxForOps, visible.amount()));
        if (requested <= 0) {
            return false;
        }

        var network = context.getInternalStorage().getInventory();
        long canInsert = network.insert(key, requested, Actionable.SIMULATE, context.getActionSource());
        if (canInsert <= 0) {
            return false;
        }

        int canTake = transport.takeEssentia(key.aspectTag(), (int) Math.min(Integer.MAX_VALUE, canInsert), side, true);
        if (canTake <= 0) {
            return false;
        }

        int taken = transport.takeEssentia(key.aspectTag(), canTake, side, false);
        if (taken <= 0) {
            return false;
        }

        long inserted = network.insert(key, taken, Actionable.MODULATE, context.getActionSource());
        if (inserted < taken) {
            int overflow = (int) (taken - inserted);
            int returned = transport.canInputFrom(side)
                    ? transport.addEssentia(key.aspectTag(), overflow, side, false)
                    : 0;
            if (returned < overflow) {
                ThaumicEnergistics.LOGGER.error(
                        "Import bus overflow: {} essentia of {} could not be returned to adjacent Thaumcraft storage",
                        overflow - returned,
                        key.aspectTag());
            }
        }

        if (inserted > 0) {
            long opsUsed = Math.max(1L,
                    (inserted + EssentiaKeyType.INSTANCE.getAmountPerOperation() - 1L)
                            / EssentiaKeyType.INSTANCE.getAmountPerOperation());
            context.reduceOperationsRemaining(opsUsed);
            return true;
        }
        return false;
    }
}
