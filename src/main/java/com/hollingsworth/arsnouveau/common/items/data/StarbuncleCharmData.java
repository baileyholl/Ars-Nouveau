package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.entity.ChangeableBehavior;
import com.hollingsworth.arsnouveau.api.item.NBTComponent;
import com.hollingsworth.arsnouveau.api.registry.BehaviorRegistry;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.goal.carbuncle.StarbyTransportBehavior;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.block.Block;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class StarbuncleCharmData implements NBTComponent<StarbuncleCharmData>, TooltipProvider {

    public static MapCodec<StarbuncleCharmData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(data -> data.name),
            Codec.STRING.optionalFieldOf("color", DyeColor.ORANGE.getName()).forGetter(data -> data.color),
//            Block.CODEC.fieldOf("path").forGetter(data -> data.pathBlock),
            BlockPos.CODEC.optionalFieldOf("bed").forGetter(data -> data.bedPos),
            ItemStack.CODEC.optionalFieldOf("cosmetic", ItemStack.EMPTY).forGetter(data -> data.cosmetic),
            ResourceLocation.CODEC.optionalFieldOf("behavior", StarbyTransportBehavior.TRANSPORT_ID).forGetter(data -> data.behavior),
            CompoundTag.CODEC.optionalFieldOf("behaviorTag", new CompoundTag()).forGetter(data -> data.behaviorTag),
            Codec.STRING.optionalFieldOf("adopter", null).forGetter(data -> data.adopter),
            Codec.STRING.optionalFieldOf("bio", null).forGetter(data -> data.bio)
    ).apply(instance, StarbuncleCharmData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, StarbuncleCharmData> STREAM_CODEC = ANCodecs.composite(
            ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs::optional),
            s -> s.name,
            ByteBufCodecs.STRING_UTF8,
            s -> s.color,
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs::optional),
            s -> s.bedPos,
            ItemStack.STREAM_CODEC,
            s -> s.cosmetic,
            ResourceLocation.STREAM_CODEC,
            s -> s.behavior,
            ByteBufCodecs.COMPOUND_TAG,
            s -> s.behaviorTag,
            ByteBufCodecs.STRING_UTF8,
            s -> s.adopter,
            ByteBufCodecs.STRING_UTF8,
            s -> s.bio,
            StarbuncleCharmData::new
    );



    public final Optional<Component> name;
    public final String color;
    public final ItemStack cosmetic;
    public final Block pathBlock;
    public final Optional<BlockPos> bedPos;
    public final ResourceLocation behavior;
    public final CompoundTag behaviorTag;
    public final String adopter;
    public final String bio;

    public StarbuncleCharmData(Optional<Component> name, String color,  Optional<BlockPos> bedPos, ItemStack cosmetic, ResourceLocation behavior, CompoundTag behaviorTag, String adopter, String bio) {
        this(name, color, null, bedPos, cosmetic, behavior, behaviorTag, adopter, bio);
    }

    public StarbuncleCharmData(Optional<Component> name, String color, Block pathBlock,  Optional<BlockPos> bedPos, ItemStack cosmetic,ResourceLocation behavior,  CompoundTag behaviorTag, String adopter, String bio) {
        this.name = name;
        this.color = color;
        this.cosmetic = cosmetic;
        this.pathBlock = pathBlock;
        this.bedPos = bedPos;
        this.behavior = behavior;
        this.adopter = adopter;
        this.bio = bio;
        this.behaviorTag = behaviorTag;
    }

    public StarbuncleCharmData() {
        this(Optional.empty(), DyeColor.ORANGE.getName(), null, Optional.empty(), ItemStack.EMPTY, StarbyTransportBehavior.TRANSPORT_ID, new CompoundTag(), null, null);

    }

    @Override
    public Codec<StarbuncleCharmData> getCodec() {
        return StarbuncleCharmData.CODEC.codec();
    }

    public Mutable mutable() {
        return new Mutable(name.orElse(null), color, cosmetic, pathBlock, bedPos.orElse(null), behavior, new CompoundTag(), adopter, bio);
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltip2, TooltipFlag pTooltipFlag) {
        if (!name.isEmpty()) {
            tooltip2.accept(name.get());
        }
        if(adopter != null){
            tooltip2.accept(Component.translatable("ars_nouveau.adopter", adopter).withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
        }
        if(bio != null){
            tooltip2.accept(Component.literal(bio).withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE)));
        }
        if(behavior != null && context != null){
            // danger zone
            try{
                ChangeableBehavior behavior = BehaviorRegistry.create(this.behavior, new Starbuncle(ArsNouveau.proxy.getClientWorld(), true), this.behaviorTag);
                if(behavior != null){
                    behavior.getTooltip(tooltip2);
                }
            }catch (Exception e){
                e.printStackTrace();
                // :-)
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StarbuncleCharmData that = (StarbuncleCharmData) o;
        return Objects.equals(name, that.name) && Objects.equals(color, that.color) && Objects.equals(cosmetic, that.cosmetic) && Objects.equals(pathBlock, that.pathBlock) && Objects.equals(bedPos, that.bedPos) && Objects.equals(behavior, that.behavior) && Objects.equals(adopter, that.adopter) && Objects.equals(bio, that.bio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, cosmetic, pathBlock, bedPos, behavior, adopter, bio);
    }

    public static class Mutable {
        public Component name;
        public String color;
        public ItemStack cosmetic;
        public Block pathBlock;
        public BlockPos bedPos;
        public ResourceLocation behaviorKey;
        public CompoundTag behaviorTag;
        public String adopter;
        public String bio;

        public Mutable(Component name, String color, ItemStack cosmetic, Block pathBlock, BlockPos bedPos, ResourceLocation behaviorKey, CompoundTag behaviorTag, String adopter, String bio) {
            this.name = name;
            this.color = color;
            this.cosmetic = cosmetic;
            this.behaviorKey = behaviorKey;
            this.pathBlock = pathBlock;
            this.bedPos = bedPos;
            this.behaviorTag = behaviorTag;
            this.adopter = adopter;
            this.bio = bio;
        }

        public Mutable() {
            this(null, DyeColor.ORANGE.getName(), ItemStack.EMPTY, null, BlockPos.ZERO, StarbyTransportBehavior.TRANSPORT_ID, new CompoundTag(), null, null);
        }

        public StarbuncleCharmData immutable() {
            return new StarbuncleCharmData(Optional.ofNullable(name), color, pathBlock, Optional.ofNullable(bedPos), cosmetic, behaviorKey, behaviorTag, adopter, bio);
        }
    }
}

