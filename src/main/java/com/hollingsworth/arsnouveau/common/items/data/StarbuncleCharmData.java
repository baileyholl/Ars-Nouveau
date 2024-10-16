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
            ComponentSerialization.CODEC.optionalFieldOf("name").forGetter(StarbuncleCharmData::getName),
            Codec.STRING.optionalFieldOf("color", DyeColor.ORANGE.getName()).forGetter(StarbuncleCharmData::getColor),
//            Block.CODEC.fieldOf("path").forGetter(data -> data.pathBlock),
            BlockPos.CODEC.optionalFieldOf("bed").forGetter(StarbuncleCharmData::getBedPos),
            ItemStack.OPTIONAL_CODEC.optionalFieldOf("cosmetic").forGetter(StarbuncleCharmData::getCosmetic),
            ResourceLocation.CODEC.optionalFieldOf("behavior", StarbyTransportBehavior.TRANSPORT_ID).forGetter(StarbuncleCharmData::getBehavior),
            CompoundTag.CODEC.optionalFieldOf("behaviorTag", new CompoundTag()).forGetter(StarbuncleCharmData::getBehaviorTag),
            Codec.STRING.optionalFieldOf("adopter", "").forGetter(StarbuncleCharmData::getAdopter),
            Codec.STRING.optionalFieldOf("bio", "").forGetter(StarbuncleCharmData::getBio)
    ).apply(instance, StarbuncleCharmData::new));

    public static StreamCodec<RegistryFriendlyByteBuf, StarbuncleCharmData> STREAM_CODEC = ANCodecs.composite(
            ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs::optional),
            StarbuncleCharmData::getName,
            ByteBufCodecs.STRING_UTF8,
            StarbuncleCharmData::getColor,
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs::optional),
            StarbuncleCharmData::getBedPos,
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs::optional),
            StarbuncleCharmData::getCosmetic,
            ResourceLocation.STREAM_CODEC,
            StarbuncleCharmData::getBehavior,
            ByteBufCodecs.COMPOUND_TAG,
            StarbuncleCharmData::getBehaviorTag,
            ByteBufCodecs.STRING_UTF8,
            StarbuncleCharmData::getAdopter,
            ByteBufCodecs.STRING_UTF8,
            StarbuncleCharmData::getBio,
            StarbuncleCharmData::new
    );


    private final Optional<Component> name;
    private final String color;
    private final Optional<ItemStack> cosmetic;
    private final Block pathBlock;
    private final Optional<BlockPos> bedPos;
    private final ResourceLocation behavior;
    private final CompoundTag behaviorTag;
    private final String adopter;
    private final String bio;

    public StarbuncleCharmData(Optional<Component> name, String color,  Optional<BlockPos> bedPos, Optional<ItemStack> cosmetic, ResourceLocation behavior, CompoundTag behaviorTag, String adopter, String bio) {
        this(name, color, null, bedPos, cosmetic, behavior, behaviorTag, adopter, bio);
    }

    public StarbuncleCharmData(Optional<Component> name, String color, Block pathBlock,  Optional<BlockPos> bedPos, Optional<ItemStack> cosmetic,ResourceLocation behavior,  CompoundTag behaviorTag, String adopter, String bio) {
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
        this(Optional.empty(), DyeColor.ORANGE.getName(), null, Optional.empty(), Optional.empty(), StarbyTransportBehavior.TRANSPORT_ID, new CompoundTag(), "", "");

    }

    @Override
    public Codec<StarbuncleCharmData> getCodec() {
        return StarbuncleCharmData.CODEC.codec();
    }

    public Mutable mutable() {
        return new Mutable(name.orElse(null), color, cosmetic.orElse(null), pathBlock, bedPos.orElse(null), behavior, behaviorTag, adopter, bio);
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
        if(behavior != null){
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
        return Objects.equals(name, that.name) && Objects.equals(color, that.color) && Objects.equals(pathBlock, that.pathBlock) && Objects.equals(bedPos, that.bedPos) && Objects.equals(behavior, that.behavior) && Objects.equals(adopter, that.adopter) && Objects.equals(bio, that.bio) && ItemStack.isSameItemSameComponents(cosmetic.orElse(ItemStack.EMPTY), that.cosmetic.orElse(ItemStack.EMPTY)) && Objects.equals(behaviorTag, that.behaviorTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, cosmetic, pathBlock, bedPos, behavior, adopter, bio, behaviorTag);
    }

    public String getColor() {
        return color;
    }

    public Optional<Component> getName() {
        return name;
    }

    public Optional<ItemStack> getCosmetic() {
        return cosmetic;
    }

    public Block getPathBlock() {
        return pathBlock;
    }

    public Optional<BlockPos> getBedPos() {
        return bedPos;
    }

    public ResourceLocation getBehavior() {
        return behavior;
    }

    public CompoundTag getBehaviorTag() {
        return behaviorTag;
    }

    public String getAdopter() {
        return adopter;
    }

    public String getBio() {
        return bio;
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
            this(null, DyeColor.ORANGE.getName(), null, null, null, StarbyTransportBehavior.TRANSPORT_ID, new CompoundTag(), "", "");
        }

        public StarbuncleCharmData immutable() {
            return new StarbuncleCharmData(Optional.ofNullable(name), color, pathBlock, Optional.ofNullable(bedPos), Optional.ofNullable(cosmetic), behaviorKey, behaviorTag, adopter, bio);
        }
    }
}

