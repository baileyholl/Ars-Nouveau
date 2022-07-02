package com.hollingsworth.arsnouveau.api.familiar;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class PersistentFamiliarData<T extends Entity> {
    public Component name;
    public String color;
    public ItemStack cosmetic;

    public PersistentFamiliarData(CompoundTag tag) {
        this.name = tag.contains("name") ? Component.Serializer.fromJson(tag.getString("name")) : null;
        this.color = tag.contains("color") ? tag.getString("color") : null;
        this.cosmetic = tag.contains("cosmetic") ? ItemStack.of(tag.getCompound("cosmetic")) : null;
    }

    public CompoundTag toTag(CompoundTag tag) {
        if (name != null)
            tag.putString("name", Component.Serializer.toJson(name));
        if (color != null) {
            tag.putString("color", color);
        }
        if (cosmetic != null) {
            tag.put("cosmetic", cosmetic.save(new CompoundTag()));
        }
        return tag;
    }
}
