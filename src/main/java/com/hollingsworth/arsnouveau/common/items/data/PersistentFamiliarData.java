package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.api.item.NBTComponent;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CheatSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public class PersistentFamiliarData implements NBTComponent<PersistentFamiliarData>, TooltipProvider {

    public static MapCodec<PersistentFamiliarData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ComponentSerialization.CODEC.fieldOf("name").forGetter(data -> data.name),
        Codec.STRING.fieldOf("color").forGetter(data -> data.color),
        ItemStack.CODEC.fieldOf("cosmetic").forGetter(data -> data.cosmetic)
    ).apply(instance, PersistentFamiliarData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, PersistentFamiliarData>  STREAM_CODEC = CheatSerializer.create(PersistentFamiliarData.CODEC);

    public final Component name;
    public final String color;
    public final ItemStack cosmetic;

    public PersistentFamiliarData(Component name, String color, ItemStack cosmetic) {
        this.name = name;
        this.color = color;
        this.cosmetic = cosmetic;
    }

    public PersistentFamiliarData(){
        this(Component.nullToEmpty(""), "", ItemStack.EMPTY);
    }

    public PersistentFamiliarData setName(Component name){
        return new PersistentFamiliarData(name, color, cosmetic);
    }

    public PersistentFamiliarData setColor(String color){
        return new PersistentFamiliarData(name, color, cosmetic);
    }

    public PersistentFamiliarData setCosmetic(ItemStack cosmetic){
        return new PersistentFamiliarData(name, color, cosmetic);
    }

    public static PersistentFamiliarData fromTag(Tag tag){
        return CODEC.codec().parse(NbtOps.INSTANCE, tag).getOrThrow();
    }

    @Override
    public Codec<PersistentFamiliarData> getCodec() {
        return CODEC.codec();
    }

    @Override
    public void addToTooltip(Item.TooltipContext pContext, Consumer<Component> pTooltipAdder, TooltipFlag pTooltipFlag) {
        if(name != null){
            pTooltipAdder.accept(name);
        }
    }

    public Mutable mutable(){
        return new Mutable(name, color, cosmetic);
    }

    public static class Mutable{
        public Component name;
        public String color;
        public ItemStack cosmetic;

        public Mutable(Component name, String color, ItemStack cosmetic) {
            this.name = name;
            this.color = color;
            this.cosmetic = cosmetic;
        }

        public PersistentFamiliarData toImmutable(){
            return new PersistentFamiliarData(name, color, cosmetic);
        }
    }
}
