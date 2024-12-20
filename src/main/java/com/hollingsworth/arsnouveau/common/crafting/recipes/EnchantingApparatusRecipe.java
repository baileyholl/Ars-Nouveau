package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class EnchantingApparatusRecipe implements IEnchantingRecipe {

    private final Ingredient reagent;
    private final ItemStack result;
    private final List<Ingredient> pedestalItems;
    private final int sourceCost;
    private final boolean keepNbtOfReagent;

    private NonNullList<Ingredient> ingredients;
    public EnchantingApparatusRecipe(Ingredient reagent, ItemStack result, List<Ingredient> pedestalItems, int sourceCost, boolean keepNbtOfReagent) {
        this.reagent = reagent;
        this.result = result;
        this.pedestalItems = pedestalItems;
        this.sourceCost = sourceCost;
        this.keepNbtOfReagent = keepNbtOfReagent;
        ingredients = NonNullList.createWithCapacity(pedestalItems.size() + 1);
        ingredients.add(reagent);
        ingredients.addAll(pedestalItems);
    }

    public boolean excludeJei() {
        return false;
    }

    @Override
    public boolean matches(ApparatusRecipeInput input, Level level, @Nullable Player player) {
        if(this.pedestalItems.size() != input.pedestals().size()){
            return false;
        }
        return doesReagentMatch(input, level, player) && doPedestalsMatch(input);
    }

    public boolean doPedestalsMatch(ApparatusRecipeInput input){
        if(this.pedestalItems.size() != input.pedestals().size()){
            return false;
        }
        var pedestalItems = input.pedestals().stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
        return doItemsMatch(pedestalItems, this.pedestalItems);
    }

    public boolean doesReagentMatch(ApparatusRecipeInput input, Level level, @Nullable Player player) {
        return this.reagent.test(input.catalyst());
    }

    // Function to check if both arrays are same
    public static boolean doItemsMatch(List<ItemStack> inputs, List<Ingredient> recipeItems) {
        if(inputs.size() != recipeItems.size()){
            return false;
        }
        StackedContents recipeitemhelper = new StackedContents();
        for (ItemStack i : inputs)
            recipeitemhelper.accountStack(i, 1);

        return (net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(inputs, recipeItems) != null);
    }

    @Override
    public @NotNull ItemStack assemble(ApparatusRecipeInput input, HolderLookup.@NotNull Provider p_346030_) {
        ItemStack result = this.result.copy();
        ItemStack reagent = input.catalyst();
        if (keepNbtOfReagent) {
            result.applyComponents(reagent.getComponentsPatch());
            result.setDamageValue(0);
        }
        return result.copy();
    }

    @Override
    public boolean consumesSource() {
        return sourceCost() > 0;
    }

    @Override
    public int sourceCost() {
        return sourceCost;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider pRegistries) {
        return this.result.copy();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.APPARATUS_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeRegistry.APPARATUS_TYPE.get();
    }

    public Ingredient reagent() {
        return reagent;
    }

    public ItemStack result() {
        return result;
    }

    public List<Ingredient> pedestalItems() {
        return pedestalItems;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public boolean keepNbtOfReagent() {
        return keepNbtOfReagent;
    }

    public static class Serializer implements RecipeSerializer<EnchantingApparatusRecipe> {

        public static MapCodec<EnchantingApparatusRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("reagent").forGetter(EnchantingApparatusRecipe::reagent),
                ItemStack.CODEC.fieldOf("result").forGetter(EnchantingApparatusRecipe::result),
                Ingredient.CODEC.listOf().fieldOf("pedestalItems").forGetter(EnchantingApparatusRecipe::pedestalItems),
                Codec.INT.fieldOf("sourceCost").forGetter(EnchantingApparatusRecipe::sourceCost),
                Codec.BOOL.fieldOf("keepNbtOfReagent").forGetter(EnchantingApparatusRecipe::keepNbtOfReagent)
        ).apply(instance, EnchantingApparatusRecipe::new));

        public static StreamCodec<RegistryFriendlyByteBuf, EnchantingApparatusRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC,
                EnchantingApparatusRecipe::reagent,
                ItemStack.STREAM_CODEC,
                EnchantingApparatusRecipe::result,
                Serializers.INGREDIENT_LIST_STREAM,
                EnchantingApparatusRecipe::pedestalItems,
                ByteBufCodecs.VAR_INT,
                EnchantingApparatusRecipe::sourceCost,
                ByteBufCodecs.BOOL,
                EnchantingApparatusRecipe::keepNbtOfReagent,
                EnchantingApparatusRecipe::new
        );

        @Override
        public @NotNull MapCodec<EnchantingApparatusRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, EnchantingApparatusRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
