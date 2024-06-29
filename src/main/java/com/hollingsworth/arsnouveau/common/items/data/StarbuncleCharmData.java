package com.hollingsworth.arsnouveau.common.items.data;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.entity.ChangeableBehavior;
import com.hollingsworth.arsnouveau.api.item.NBTComponent;
import com.hollingsworth.arsnouveau.api.registry.BehaviorRegistry;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CheatSerializer;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
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
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class StarbuncleCharmData implements NBTComponent<StarbuncleCharmData>, TooltipProvider {

    public static MapCodec<StarbuncleCharmData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("name").forGetter(data -> data.name),
            Codec.STRING.optionalFieldOf("color", DyeColor.ORANGE.getName()).forGetter(data -> data.color),
            Block.CODEC.fieldOf("path").forGetter(data -> data.pathBlock),
            BlockPos.CODEC.optionalFieldOf("bed", BlockPos.ZERO).forGetter(data -> data.bedPos == null ? BlockPos.ZERO : data.bedPos),
            ItemStack.CODEC.optionalFieldOf("cosmetic", ItemStack.EMPTY).forGetter(data -> data.cosmetic),
            CompoundTag.CODEC.optionalFieldOf("behavior", new CompoundTag()).forGetter(data -> data.behaviorTag),
            Codec.STRING.optionalFieldOf("adopter", null).forGetter(data -> data.adopter),
            Codec.STRING.optionalFieldOf("bio", null).forGetter(data -> data.bio)
    ).apply(instance, StarbuncleCharmData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, StarbuncleCharmData> STREAM_CODEC = CheatSerializer.create(CODEC);
    public final Component name;
    public final String color;
    public final ItemStack cosmetic;
    public final Block pathBlock;
    public final BlockPos bedPos;
    public final CompoundTag behaviorTag;
    public final String adopter;
    public final String bio;

    public StarbuncleCharmData(Component name, String color, Block pathBlock, BlockPos bedPos, ItemStack cosmetic, CompoundTag behaviorTag, String adopter, String bio) {
        this.name = name;
        this.color = color;
        this.cosmetic = cosmetic;
        this.pathBlock = pathBlock;
        this.bedPos = bedPos;
        this.behaviorTag = behaviorTag;
        this.adopter = adopter;
        this.bio = bio;
    }

    public StarbuncleCharmData() {
        this(null, DyeColor.ORANGE.getName(), null, BlockPos.ZERO, ItemStack.EMPTY, new CompoundTag(), null, null);

    }

    @Override
    public Codec<StarbuncleCharmData> getCodec() {
        return StarbuncleCharmData.CODEC.codec();
    }

    public Mutable mutable() {
        return new Mutable(name, color, cosmetic, pathBlock, bedPos, behaviorTag, adopter, bio);
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltip2, TooltipFlag pTooltipFlag) {
        if (name != null) {
            tooltip2.accept(name);
        }
        if(adopter != null){
            tooltip2.accept(Component.translatable("ars_nouveau.adopter", adopter).withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
        }
        if(bio != null){
            tooltip2.accept(Component.literal(bio).withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE)));
        }
        if(behaviorTag != null && context != null){
            // danger zone
            try{
                ChangeableBehavior behavior = BehaviorRegistry.create(new Starbuncle(ArsNouveau.proxy.getClientWorld(), true), behaviorTag);
                if(behavior != null){
                    behavior.getTooltip(tooltip2);
                }
            }catch (Exception e){
                // :-)
            }
        }
    }

    public static class Mutable {
        public Component name;
        public String color;
        public ItemStack cosmetic;
        public Block pathBlock;
        public BlockPos bedPos;
        public CompoundTag behaviorTag;
        public String adopter;
        public String bio;

        public Mutable(Component name, String color, ItemStack cosmetic, Block pathBlock, BlockPos bedPos, CompoundTag behaviorTag, String adopter, String bio) {
            this.name = name;
            this.color = color;
            this.cosmetic = cosmetic;
            this.pathBlock = pathBlock;
            this.bedPos = bedPos;
            this.behaviorTag = behaviorTag;
            this.adopter = adopter;
            this.bio = bio;
        }

        public Mutable() {
            this(null, DyeColor.ORANGE.getName(), ItemStack.EMPTY, null, BlockPos.ZERO, new CompoundTag(), null, null);
        }

        public StarbuncleCharmData immutable() {
            return new StarbuncleCharmData(name, color, pathBlock, bedPos, cosmetic, behaviorTag, adopter, bio);
        }
    }
}

