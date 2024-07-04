package com.hollingsworth.arsnouveau.common.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec2;

import java.util.UUID;

public class ANCodecs {

    public static Codec<Vec2> VEC2 = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("x").forGetter(v -> v.x),
            Codec.FLOAT.fieldOf("y").forGetter(v -> v.y)
    ).apply(instance, Vec2::new));

    public static Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);

    public static <T> Tag encode(Codec<T> codec, T value){
        return codec.encodeStart(NbtOps.INSTANCE, value).getOrThrow();
    }

    public static <T> T decode(Codec<T> codec, Tag tag){
        return codec.parse(NbtOps.INSTANCE, tag).getOrThrow();
    }

    public static <T> JsonElement toJson(Codec<T> codec, T value){
        return codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow();
    }
}
