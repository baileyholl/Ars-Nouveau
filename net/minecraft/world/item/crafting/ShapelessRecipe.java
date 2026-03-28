package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class ShapelessRecipe implements CraftingRecipe {
    final String group;
    final CraftingBookCategory category;
    final ItemStack result;
    final List<Ingredient> ingredients;
    private @Nullable PlacementInfo placementInfo;
    private final boolean isSimple;

    public ShapelessRecipe(String p_249640_, CraftingBookCategory p_249390_, ItemStack p_252071_, List<Ingredient> p_361103_) {
        this.group = p_249640_;
        this.category = p_249390_;
        this.result = p_252071_;
        this.ingredients = p_361103_;
        this.isSimple = p_361103_.stream().allMatch(Ingredient::isSimple);
    }

    @Override
    public RecipeSerializer<ShapelessRecipe> getSerializer() {
        return RecipeSerializer.SHAPELESS_RECIPE;
    }

    @Override
    public String group() {
        return this.group;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }

    @Override
    public PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(this.ingredients);
        }

        return this.placementInfo;
    }

    public boolean matches(CraftingInput p_346123_, Level p_44263_) {
        if (p_346123_.ingredientCount() != this.ingredients.size()) {
            return false;
        } else if (!isSimple) {
            var nonEmptyItems = new java.util.ArrayList<ItemStack>(p_346123_.ingredientCount());
            for (var item : p_346123_.items())
                if (!item.isEmpty())
                    nonEmptyItems.add(item);
            return net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(nonEmptyItems, this.ingredients) != null;
        } else {
            return p_346123_.size() == 1 && this.ingredients.size() == 1
                ? this.ingredients.getFirst().test(p_346123_.getItem(0))
                : p_346123_.stackedContents().canCraft(this, null);
        }
    }

    public ItemStack assemble(CraftingInput p_345555_, HolderLookup.Provider p_335725_) {
        return this.result.copy();
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(
            new ShapelessCraftingRecipeDisplay(
                this.ingredients.stream().map(Ingredient::display).toList(),
                new SlotDisplay.ItemStackSlotDisplay(this.result),
                new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)
            )
        );
    }

    public static class Serializer implements RecipeSerializer<ShapelessRecipe> {
        private static final MapCodec<ShapelessRecipe> CODEC = RecordCodecBuilder.mapCodec(
            p_360072_ -> p_360072_.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(p_301127_ -> p_301127_.group),
                    CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(p_301133_ -> p_301133_.category),
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(p_301142_ -> p_301142_.result),
                    Codec.lazyInitialized(() -> Ingredient.CODEC.listOf(1, ShapedRecipePattern.maxHeight * ShapedRecipePattern.maxWidth)).fieldOf("ingredients").forGetter(p_360071_ -> p_360071_.ingredients)
                )
                .apply(p_360072_, ShapelessRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            p_360074_ -> p_360074_.group,
            CraftingBookCategory.STREAM_CODEC,
            p_360073_ -> p_360073_.category,
            ItemStack.STREAM_CODEC,
            p_360070_ -> p_360070_.result,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
            p_360069_ -> p_360069_.ingredients,
            ShapelessRecipe::new
        );

        @Override
        public MapCodec<ShapelessRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShapelessRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
