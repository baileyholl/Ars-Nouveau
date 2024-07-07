package com.hollingsworth.arsnouveau.common.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec2;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ANCodecs {

    public static Codec<Vec2> VEC2 = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("x").forGetter(v -> v.x),
            Codec.FLOAT.fieldOf("y").forGetter(v -> v.y)
    ).apply(instance, Vec2::new));

    public static <T> Tag encode(Codec<T> codec, T value){
        return codec.encodeStart(NbtOps.INSTANCE, value).getOrThrow();
    }

    public static <T> T decode(Codec<T> codec, Tag tag){
        return codec.parse(NbtOps.INSTANCE, tag).getOrThrow();
    }

    public static <T> JsonElement toJson(Codec<T> codec, T value){
        return codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow();
    }

    /**
     * Creates an unbounded map codec that uses integer keys.
     */
    public static <MapVal, Obj> Codec<Obj> intMap(Codec<MapVal> codec, Function<Map<Integer, MapVal>, Obj> constructor, Function<Obj, Map<Integer, MapVal>> intMap){
        return Codec.unboundedMap(Codec.STRING, codec).xmap((stringMap) ->{
            Map<Integer, MapVal> map = new HashMap<>(stringMap.size());
            stringMap.forEach((key, value) -> map.put(Integer.parseInt(key), value));
            return constructor.apply(map);
        }, obj -> {
            var ints = intMap.apply(obj);
            Map<String, MapVal> stringMap = new HashMap<>(ints.size());
            ints.forEach((key, value) -> stringMap.put(key.toString(), value));
            return stringMap;
        });
    }
}
