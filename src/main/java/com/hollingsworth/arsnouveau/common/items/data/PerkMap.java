package com.hollingsworth.arsnouveau.common.items.data;

import com.google.common.collect.ImmutableMap;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.common.perk.StarbunclePerk;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Objects;

public record PerkMap(Map<IPerk, CompoundTag> map) {
    public static Codec<PerkMap> CODEC =  Codec.unboundedMap(Codec.STRING, CompoundTag.CODEC).xmap((stringMap) ->{
        var builder = ImmutableMap.<IPerk, CompoundTag>builder();
        stringMap.forEach((key, value) ->{
            if(key != null) {
                var loc = ResourceLocation.tryParse(key);
                if(loc == null){
                    return;
                }
                var perk = PerkRegistry.getPerkMap().getOrDefault(loc, StarbunclePerk.INSTANCE);
                builder.put(perk, value);
            }
        });
        return new PerkMap(builder.build());
    }, obj -> {
        var builder = ImmutableMap.<String, CompoundTag>builder();
        obj.map.forEach((key, value) -> builder.put(key.getRegistryName().toString(), value));
        return builder.build();
    });

    public CompoundTag getOrDefault(IPerk slot, CompoundTag tag) {
        return map.getOrDefault(slot, tag);
    }

    public CompoundTag get(IPerk slot) {
        return map.get(slot);
    }

    public PerkMap put(IPerk slot, CompoundTag tag){
        return new PerkMap(Util.copyAndPut(map, slot, tag));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerkMap that = (PerkMap) o;
        return Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(map);
    }

}
