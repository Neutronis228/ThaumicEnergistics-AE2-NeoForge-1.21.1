package com.neutronis.thaumicenergistics.integration.ae2;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.essentia.transport.TCEssentiaStorage;

/** Presents a Thaumcraft essentia store as a native AE2 MEStorage. */
public final class EssentiaStorageAdapter implements MEStorage {
    private final TCEssentiaStorage storage;

    public EssentiaStorageAdapter(TCEssentiaStorage storage) {
        this.storage = Objects.requireNonNull(storage, "storage");
    }

    public TCEssentiaStorage thaumcraftStorage() {
        return storage;
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        return what instanceof EssentiaKey key && storage.amount(key.aspectTag()) > 0;
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        MEStorage.checkPreconditions(what, amount, mode, source);
        if (!(what instanceof EssentiaKey key) || Aspect.getAspect(key.aspectTag()) == null || amount == 0) {
            return 0;
        }
        int requested = (int) Math.min(Integer.MAX_VALUE, amount);
        return storage.add(key.aspectTag(), requested, mode == Actionable.SIMULATE);
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        MEStorage.checkPreconditions(what, amount, mode, source);
        if (!(what instanceof EssentiaKey key) || amount == 0) {
            return 0;
        }
        int requested = (int) Math.min(Integer.MAX_VALUE, amount);
        return storage.take(key.aspectTag(), requested, mode == Actionable.SIMULATE);
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        storage.snapshot().forEach((aspectTag, amount) -> {
            if (amount != null && amount > 0 && Aspect.getAspect(aspectTag) != null) {
                out.add(EssentiaKey.of(aspectTag), amount.longValue());
            }
        });
    }

    @Override
    public Component getDescription() {
        return Component.translatable("gui.thaumicenergistics.storage.essentia");
    }
}
