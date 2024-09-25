package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface CasterCapability<T extends SpellCaster> extends INBTSerializable<CompoundTag> {

    T getSpellCaster();

    void setSpellCaster(T caster);

    default T getSpellCaster(CompoundTag tag){
        Tag casterTag = tag.get("caster");
        if(casterTag == null){
            return getSpellCaster();
        }
        return ANCodecs.decode(getCodec(), casterTag);
    }

    Codec<T> getCodec();


    default Tag encode(){
        return ANCodecs.encode(getCodec(), getSpellCaster());
    }
}
