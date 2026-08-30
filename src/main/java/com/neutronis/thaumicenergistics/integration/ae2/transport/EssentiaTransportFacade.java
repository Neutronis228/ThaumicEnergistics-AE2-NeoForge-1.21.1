package com.neutronis.thaumicenergistics.integration.ae2.transport;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import com.neutronis.thaumicenergistics.integration.ae2.EssentiaKey;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.essentia.transport.TCEssentiaTransport;

/**
 * Exposes a sided Thaumcraft essentia transport endpoint (jar, alembic, tube-compatible device)
 * as native AE2 storage. This is the modern equivalent of the old TE essentia container adapter.
 */
public record EssentiaTransportFacade(
        TCEssentiaTransport transport,
        Direction side,
        boolean extractableOnly,
        Runnable changeListener) implements MEStorage {

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        MEStorage.checkPreconditions(what, amount, mode, source);
        if (!(what instanceof EssentiaKey key)
                || amount <= 0
                || Aspect.getAspect(key.aspectTag()) == null
                || !transport.canInputFrom(side)) {
            return 0;
        }

        int requested = (int) Math.min(Integer.MAX_VALUE, amount);
        int inserted = transport.addEssentia(key.aspectTag(), requested, side, mode.isSimulate());
        if (inserted > 0 && mode == Actionable.MODULATE) {
            changeListener.run();
        }
        return inserted;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        MEStorage.checkPreconditions(what, amount, mode, source);
        if (!(what instanceof EssentiaKey key)
                || amount <= 0
                || !transport.canOutputTo(side)) {
            return 0;
        }

        int requested = (int) Math.min(Integer.MAX_VALUE, amount);
        int extracted = transport.takeEssentia(key.aspectTag(), requested, side, mode.isSimulate());
        if (extracted > 0 && mode == Actionable.MODULATE) {
            changeListener.run();
        }
        return extracted;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        if (!transport.canOutputTo(side)) {
            return;
        }

        var visible = transport.getEssentia(side);
        if (visible == null || visible.isEmpty() || Aspect.getAspect(visible.aspect()) == null) {
            return;
        }

        if (extractableOnly
                && transport.takeEssentia(visible.aspect(), Math.min(visible.amount(), 1), side, true) <= 0) {
            return;
        }

        out.add(EssentiaKey.of(visible.aspect()), visible.amount());
    }

    @Override
    public Component getDescription() {
        return Component.translatable("gui.thaumicenergistics.storage.essentia");
    }
}
