package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
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

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class EnchantingApparatusRecipe implements IEnchantingRecipe {

    private final Ingredient reagent;
    private final ItemStack result;
    private final List<Ingredient> pedestalItems;
    private final int sourceCost;
    private final boolean keepNbtOfReagent;


    public EnchantingApparatusRecipe(Ingredient reagent, ItemStack result, List<Ingredient> pedestalItems, int sourceCost, boolean keepNbtOfReagent){
        this.reagent = reagent;
        this.result = result;
        this.pedestalItems = pedestalItems;
        this.sourceCost = sourceCost;
        this.keepNbtOfReagent = keepNbtOfReagent;

    }

    public boolean excludeJei(){
        return false;
    }

    @Override
    public boolean matches(ApparatusRecipeInput input, Level level, @Nullable Player player) {
        var pedestalItems = input.pedestals().stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
        return doesReagentMatch(input, level, player) && this.pedestalItems.size() == pedestalItems.size() && doItemsMatch(pedestalItems, this.pedestalItems);
    }

    public boolean doesReagentMatch(ApparatusRecipeInput input, Level level, @Nullable Player player) {
        return this.reagent.test(input.catalyst());
    }

    // Function to check if both arrays are same
    public static boolean doItemsMatch(List<ItemStack> inputs, List<Ingredient> recipeItems) {
        StackedContents recipeitemhelper = new StackedContents();
        for (ItemStack i : inputs)
            recipeitemhelper.accountStack(i, 1);

        return inputs.size() == recipeItems.size() && (net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(inputs, recipeItems) != null);
    }

    @Override
    public ItemStack assemble(ApparatusRecipeInput input, HolderLookup.Provider p_346030_) {
        ItemStack result = this.result.copy();
        ItemStack reagent = input.catalyst();
        if (keepNbtOfReagent && reagent.hasTag()) {
            result.setTag(reagent.getTag());
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
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return this.result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.APPARATUS_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
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
        public MapCodec<EnchantingApparatusRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, EnchantingApparatusRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
