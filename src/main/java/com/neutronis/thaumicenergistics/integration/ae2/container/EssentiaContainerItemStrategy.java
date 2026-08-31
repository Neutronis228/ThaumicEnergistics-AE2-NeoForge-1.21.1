package com.neutronis.thaumicenergistics.integration.ae2.container;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;
import com.neutronis.thaumicenergistics.integration.ae2.EssentiaKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.TCEssentiaItemHelper;
import thaumcraft.common.items.TCPhialItem;
import thaumcraft.common.registry.TCSounds;

/** Makes Thaumcraft phials behave like native containers in every AE2 generic terminal. */
public final class EssentiaContainerItemStrategy
        implements ContainerItemStrategy<EssentiaKey, EssentiaContainerItemStrategy.Context> {

    @Override
    public @Nullable GenericStack getContainedStack(ItemStack stack) {
        Aspect aspect = TCEssentiaItemHelper.aspectFromStack(stack);
        int amount = TCEssentiaItemHelper.aspectAmount(stack);
        if (!TCEssentiaItemHelper.isFilledPhial(stack) || aspect == null || amount <= 0) {
            return null;
        }
        return new GenericStack(EssentiaKey.of(aspect), amount);
    }

    @Override
    public @Nullable Context findCarriedContext(Player player, AbstractContainerMenu menu) {
        ItemStack carried = menu.getCarried();
        return isSupportedPhial(carried) ? new CarriedContext(player, menu) : null;
    }

    @Override
    public @Nullable Context findPlayerSlotContext(Player player, int slot) {
        ItemStack stack = player.getInventory().getItem(slot);
        return isSupportedPhial(stack) ? new PlayerInventoryContext(player, slot) : null;
    }

    @Override
    public long extract(Context context, EssentiaKey key, long amount, Actionable mode) {
        ItemStack source = context.getStack();
        Aspect aspect = TCEssentiaItemHelper.aspectFromStack(source);
        int stored = TCEssentiaItemHelper.aspectAmount(source);
        if (!TCEssentiaItemHelper.isFilledPhial(source)
                || aspect == null
                || !aspect.getTag().equals(key.aspectTag())
                || stored <= 0) {
            return 0;
        }
        long moved = Math.min(amount, stored);
        if (moved < stored) {
            return 0;
        }
        if (mode == Actionable.MODULATE) {
            source.shrink(1);
            context.addOverflow(TCEssentiaItemHelper.emptyPhial());
        }
        return stored;
    }

    @Override
    public long insert(Context context, EssentiaKey key, long amount, Actionable mode) {
        ItemStack source = context.getStack();
        Aspect aspect = key.aspect();
        if (!TCEssentiaItemHelper.isEmptyPhial(source)
                || aspect == null
                || amount < TCPhialItem.BASE_AMOUNT) {
            return 0;
        }
        if (mode == Actionable.MODULATE) {
            source.shrink(1);
            context.addOverflow(TCEssentiaItemHelper.filledPhial(aspect));
        }
        return TCPhialItem.BASE_AMOUNT;
    }

    @Override
    public void playFillSound(Player player, EssentiaKey key) {
        playJarSound(player, 1.1F);
    }

    @Override
    public void playEmptySound(Player player, EssentiaKey key) {
        playJarSound(player, 0.9F);
    }

    @Override
    public @Nullable GenericStack getExtractableContent(Context context) {
        return getContainedStack(context.getStack());
    }

    private static boolean isSupportedPhial(ItemStack stack) {
        return TCEssentiaItemHelper.isEmptyPhial(stack) || TCEssentiaItemHelper.isFilledPhial(stack);
    }

    private static void playJarSound(Player player, float pitch) {
        player.level().playSound(null, player.blockPosition(), TCSounds.JAR.get(), SoundSource.PLAYERS, 0.7F, pitch);
    }

    public interface Context {
        ItemStack getStack();

        void setStack(ItemStack stack);

        Player player();

        default void addOverflow(ItemStack result) {
            ItemStack current = getStack();
            if (current.isEmpty()) {
                setStack(result);
            } else if (!player().getInventory().add(result)) {
                player().drop(result, false);
            }
        }
    }

    private record CarriedContext(Player player, AbstractContainerMenu menu) implements Context {
        @Override
        public ItemStack getStack() {
            return menu.getCarried();
        }

        @Override
        public void setStack(ItemStack stack) {
            menu.setCarried(stack);
            menu.broadcastChanges();
        }
    }

    private record PlayerInventoryContext(Player player, int slot) implements Context {
        @Override
        public ItemStack getStack() {
            return player.getInventory().getItem(slot);
        }

        @Override
        public void setStack(ItemStack stack) {
            player.getInventory().setItem(slot, stack);
            player.inventoryMenu.broadcastChanges();
        }
    }
}
