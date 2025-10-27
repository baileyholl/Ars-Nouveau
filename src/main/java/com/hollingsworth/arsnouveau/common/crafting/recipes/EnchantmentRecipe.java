package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnchantmentRecipe extends EnchantingApparatusRecipe {

    public ResourceKey<Enchantment> enchantmentKey;
    public int enchantLevel;

    public EnchantmentRecipe(List<Ingredient> pedestalItems, ResourceKey<Enchantment> enchantmentKey, int level, int source) {
        super(Ingredient.EMPTY, ItemStack.EMPTY, pedestalItems, source, true);
        this.enchantmentKey = enchantmentKey;
        this.enchantLevel = level;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeRegistry.ENCHANTMENT_TYPE.get();
    }

    @Override
    public boolean excludeJei() {
        return true;
    }

    @Override
    public boolean matches(ApparatusRecipeInput input, Level level, @org.jetbrains.annotations.Nullable Player player) {
        // Check pedestal match first as it is less costly than enchantment checks
        return doPedestalsMatch(input) && doesReagentMatch(input, level, player);
    }

    public Holder<Enchantment> holderFor(Level level) {
        return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(enchantmentKey);
    }

    @Override
    public boolean doesReagentMatch(ApparatusRecipeInput input, Level world, @Nullable Player player) {
        ItemStack stack = input.catalyst();
        if (stack.isEmpty())
            return false;
        ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        var enchantment = holderFor(world);
        int level = enchantments.getLevel(enchantment);
        Collection<Holder<Enchantment>> enchantList = enchantments.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
        enchantList.remove(enchantment);
        if (stack.getItem() != Items.BOOK && stack.getItem() != Items.ENCHANTED_BOOK && !enchantment.value().canEnchant(stack)) {
            if (player != null) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.enchanting.incompatible"));
            }
            return false;
        }

        if (!EnchantmentHelper.isEnchantmentCompatible(enchantList, enchantment)) {
            if (player != null) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.enchanting.incompatible"));
            }
            return false;
        }

        if (!(this.enchantLevel - level == 1)) {
            if (player != null) {
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.enchanting.bad_level"));
            }
            return false;
        }

        return true;
    }

    @Override
    public @NotNull ItemStack assemble(ApparatusRecipeInput input, HolderLookup.@NotNull Provider lookup) {
        ItemStack inStack = input.catalyst();
        ItemStack stack = inStack.getItem() == Items.BOOK ? new ItemStack(Items.ENCHANTED_BOOK) : inStack.copy();
        ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        var mutable = new ItemEnchantments.Mutable(enchantments);
        var enchantment = lookup.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantmentKey);
        mutable.set(enchantment, enchantLevel);
        EnchantmentHelper.setEnchantments(stack, mutable.toImmutable());
        return stack;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider pRegistries) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.ENCHANTMENT_SERIALIZER.get();
    }

    public ResourceKey<Enchantment> enchantmentKey() {
        return enchantmentKey;
    }

    public int enchantLevel() {
        return enchantLevel;
    }

    public static class Serializer implements RecipeSerializer<EnchantmentRecipe> {

        public static MapCodec<EnchantmentRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.listOf().fieldOf("pedestalItems").forGetter(EnchantmentRecipe::pedestalItems),
                ResourceKey.codec(Registries.ENCHANTMENT).fieldOf("enchantment").forGetter(EnchantmentRecipe::enchantmentKey),
                Codec.INT.fieldOf("level").forGetter(EnchantmentRecipe::enchantLevel),
                Codec.INT.fieldOf("sourceCost").forGetter(EnchantmentRecipe::sourceCost)
        ).apply(instance, EnchantmentRecipe::new));

        public static StreamCodec<RegistryFriendlyByteBuf, EnchantmentRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.collection(ArrayList::new)),
                EnchantmentRecipe::pedestalItems,
                ResourceKey.streamCodec(Registries.ENCHANTMENT),
                EnchantmentRecipe::enchantmentKey,
                ByteBufCodecs.INT,
                EnchantmentRecipe::enchantLevel,
                ByteBufCodecs.INT,
                EnchantmentRecipe::sourceCost,
                EnchantmentRecipe::new
        );

        @Override
        public @NotNull MapCodec<EnchantmentRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, EnchantmentRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
