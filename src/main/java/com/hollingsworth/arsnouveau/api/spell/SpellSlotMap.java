package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;

public record SpellSlotMap(Map<Integer, Spell> slots) {

    public static final Codec<SpellSlotMap> CODEC = ANCodecs.intMap(Spell.CODEC.codec(), SpellSlotMap::new, SpellSlotMap::slots);

    public static final StreamCodec<RegistryFriendlyByteBuf, SpellSlotMap> STREAM = StreamCodec.ofMember((val, buf) ->{
        var entries = val.slots.entrySet();
        buf.writeInt(entries.size());

        for (var entry : entries) {
            buf.writeInt(entry.getKey());
            Spell.STREAM.encode(buf, entry.getValue());
        }
    }, (buf) -> {
        int size = buf.readInt();
        Map<Integer, Spell> slots = Util.make(new HashMap<>(), map -> {
            for (int i = 0; i < size; i++) {
                int key = buf.readInt();
                Spell value = Spell.STREAM.decode(buf);
                map.put(key, value);
            }
        });
        return new SpellSlotMap(slots);
    });
    public Spell getOrDefault(int slot, Spell spell) {
        return slots.getOrDefault(slot, spell);
    }

    public Spell get(int slot) {
        return slots.get(slot);
    }

    public SpellSlotMap put(int slot, Spell spell){
        return new SpellSlotMap(Util.copyAndPut(slots, slot, spell));
    }
}
