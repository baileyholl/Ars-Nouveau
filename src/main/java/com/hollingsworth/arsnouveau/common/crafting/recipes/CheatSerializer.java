package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Lazy serialization that takes any codec and creates a stream codec for it.
 * Uses RegistryOps<Tag> (NBT) to ensure HolderSetCodec uses the registry-aware
 * decode path, which handles both single-element strings and list/tag forms.
 * Plain JsonOps causes HolderSetCodec to use decodeWithoutRegistry which only
 * accepts list form — asymmetric with encode that produces single-element strings.
 */
public class CheatSerializer {

    public static <T> StreamCodec<RegistryFriendlyByteBuf, T> create(MapCodec<T> codec) {
        return create(codec.codec());
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, T> create(Codec<T> codec) {
        return StreamCodec.of(
                (buf, val) -> CheatSerializer.toNetwork(codec, buf, val), (buf) -> CheatSerializer.fromNetwork(codec, buf)
        );
    }

    public static <T> T fromNetwork(Codec<T> codec, RegistryFriendlyByteBuf buf) {
        var ops = buf.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        Tag nbt = buf.readNbt(NbtAccounter.unlimitedHeap());
        if (nbt == null) throw new IllegalStateException("CheatSerializer.fromNetwork: null nbt");
        return codec.parse(ops, nbt).getOrThrow();
    }

    public static <T> void toNetwork(Codec<T> codec, RegistryFriendlyByteBuf buf, T obj) {
        var ops = buf.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        buf.writeNbt(codec.encodeStart(ops, obj).getOrThrow());
    }
}
