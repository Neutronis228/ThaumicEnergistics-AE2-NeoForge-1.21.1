package com.neutronis.thaumicenergistics.integration.ae2;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;

/** Modern AE2 resource identity for one Thaumcraft aspect. */
public final class EssentiaKey extends AEKey {
    public static final MapCodec<EssentiaKey> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("aspect").forGetter(EssentiaKey::aspectTag)
    ).apply(instance, EssentiaKey::of));
    public static final Codec<EssentiaKey> CODEC = MAP_CODEC.codec();

    private final String aspectTag;

    private EssentiaKey(String aspectTag) {
        this.aspectTag = normalize(aspectTag);
    }

    public static EssentiaKey of(String aspectTag) {
        return new EssentiaKey(aspectTag);
    }

    public static EssentiaKey of(Aspect aspect) {
        return new EssentiaKey(Objects.requireNonNull(aspect, "aspect").getTag());
    }

    private static String normalize(String tag) {
        var value = Objects.requireNonNull(tag, "aspectTag").trim().toLowerCase(Locale.ROOT);
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Aspect tag cannot be empty");
        }
        return value.intern();
    }

    public String aspectTag() {
        return aspectTag;
    }

    @Nullable
    public Aspect aspect() {
        return Aspect.getAspect(aspectTag);
    }

    public int color() {
        var aspect = aspect();
        return aspect != null ? aspect.getColor() : 0x7F7F7F;
    }

    @Override
    public AEKeyType getType() {
        return EssentiaKeyType.INSTANCE;
    }

    @Override
    public AEKey dropSecondary() {
        return this;
    }

    @Override
    public CompoundTag toTag(HolderLookup.Provider registries) {
        var tag = new CompoundTag();
        tag.putString("aspect", aspectTag);
        return tag;
    }

    @Override
    public Object getPrimaryKey() {
        return aspectTag;
    }

    @Override
    public ResourceLocation getId() {
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", aspectTag);
    }

    @Override
    public void writeToPacket(RegistryFriendlyByteBuf data) {
        data.writeUtf(aspectTag, 64);
    }

    public static EssentiaKey fromPacket(RegistryFriendlyByteBuf data) {
        return of(data.readUtf(64));
    }

    @Override
    protected Component computeDisplayName() {
        return Component.translatable("tc.aspect." + aspectTag);
    }

    @Override
    public void addDrops(long amount, List<ItemStack> drops, Level level, BlockPos pos) {
    }

    @Override
    public <T> @Nullable T get(DataComponentType<T> type) {
        return null;
    }

    @Override
    public boolean hasComponents() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof EssentiaKey other && aspectTag.equals(other.aspectTag));
    }

    @Override
    public int hashCode() {
        return aspectTag.hashCode();
    }

    @Override
    public String toString() {
        return "EssentiaKey[" + aspectTag + ']';
    }
}
