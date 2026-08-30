package com.neutronis.thaumicenergistics.integration.ae2;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.mojang.serialization.MapCodec;
import com.neutronis.thaumicenergistics.ThaumicEnergistics;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/** Native ME storage key space for Thaumcraft essentia. */
public final class EssentiaKeyType extends AEKeyType {
    public static final EssentiaKeyType INSTANCE = new EssentiaKeyType();

    private EssentiaKeyType() {
        super(
                ResourceLocation.fromNamespaceAndPath(ThaumicEnergistics.MOD_ID, "essentia"),
                EssentiaKey.class,
                Component.translatable("keytype.thaumicenergistics.essentia")
        );
    }

    @Override
    public MapCodec<? extends AEKey> codec() {
        return EssentiaKey.MAP_CODEC;
    }

    @Override
    public int getAmountPerOperation() {
        return 8;
    }

    @Override
    public int getAmountPerByte() {
        return 8;
    }

    @Override
    public int getAmountPerUnit() {
        return 1;
    }

    @Override
    public String getUnitSymbol() {
        return " essentia";
    }

    @Override
    public EssentiaKey readFromPacket(RegistryFriendlyByteBuf input) {
        return EssentiaKey.fromPacket(input);
    }

    @Override
    public @Nullable AEKey loadKeyFromTag(HolderLookup.Provider registries, CompoundTag tag) {
        if (!tag.contains("aspect")) {
            return null;
        }
        return EssentiaKey.of(tag.getString("aspect"));
    }
}
