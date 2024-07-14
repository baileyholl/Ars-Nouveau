package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.api.item.NBTComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.Objects;
import java.util.function.Consumer;

public class PersistentFamiliarData implements NBTComponent<PersistentFamiliarData>, TooltipProvider {

    public static MapCodec<PersistentFamiliarData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        ComponentSerialization.CODEC.optionalFieldOf("name", CommonComponents.EMPTY).forGetter(data -> data.name),
        Codec.STRING.optionalFieldOf("color", "").forGetter(data -> data.color),
        ItemStack.CODEC.optionalFieldOf("cosmetic", ItemStack.EMPTY).forGetter(data -> data.cosmetic)
    ).apply(instance, PersistentFamiliarData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, PersistentFamiliarData>  STREAM_CODEC = StreamCodec.composite(ComponentSerialization.STREAM_CODEC, s -> s.name,
            ByteBufCodecs.STRING_UTF8, s -> s.color, ItemStack.STREAM_CODEC, s -> s.cosmetic, PersistentFamiliarData::new);

    private final Component name;
    private final String color;
    private final ItemStack cosmetic;

    public PersistentFamiliarData(Component name, String color, ItemStack cosmetic) {
        this.name = name == null ? CommonComponents.EMPTY : name;
        this.color = color == null ? "" : color;
        this.cosmetic = cosmetic == null ? ItemStack.EMPTY : cosmetic;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersistentFamiliarData that = (PersistentFamiliarData) o;
        return Objects.equals(name, that.name) && Objects.equals(color, that.color) && Objects.equals(cosmetic, that.cosmetic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, cosmetic);
    }

    public Component name() {
        return name == CommonComponents.EMPTY ? null : name;
    }

    public String color() {
        return color.isEmpty() ? null : color;
    }

    public ItemStack cosmetic() {
        return cosmetic;
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
