package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.imbuement_chamber.IImbuementRecipe;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ImbuementRecipe implements IImbuementRecipe {

    public final Ingredient input;
    public final ItemStack output;
    public final int source;

    public List<Ingredient> pedestalItems;
    public ResourceLocation id;

    private NonNullList<Ingredient> ingredients;
    public ImbuementRecipe(Ingredient input, ItemStack output, int source, List<Ingredient> pedestalItems) {
        this.input = input;
        this.output = output;
        this.source = source;
        this.pedestalItems = pedestalItems;
        ingredients = NonNullList.createWithCapacity(pedestalItems.size() + 1);
        ingredients.add(input);
        ingredients.addAll(pedestalItems);
    }

    public ImbuementRecipe(String id, Ingredient ingredient, ItemStack output, int source) {
        this(ArsNouveau.prefix(id), ingredient, output, source);
    }

    public ImbuementRecipe(ResourceLocation id, Ingredient ingredient, ItemStack output, int source) {
        this(ingredient, output, source, new ArrayList<>());
        this.id = id;
    }

    public ImbuementRecipe withPedestalItem(Ingredient i) {
        this.pedestalItems.add(i);
        return this;
    }

    public ImbuementRecipe withPedestalItem(ItemLike i) {
        this.pedestalItems.add(Ingredient.of(i));
        return this;
    }

    @Override
    public int getSourceCost(ImbuementTile imbuementTile) {
        return this.source;
    }

    public boolean doesReagentMatch(ItemStack reag) {
        return this.input.test(reag);
    }

    public Ingredient getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getSource() {
        return source;
    }

    public List<Ingredient> getPedestalItems() {
        return pedestalItems;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean matches(ImbuementTile pContainer, Level pLevel) {
        return this.input.test(pContainer.getItem(0)) && EnchantingApparatusRecipe.doItemsMatch(pContainer.getPedestalItems(), pedestalItems);
    }

    @Override
    public ItemStack assemble(ImbuementTile pCraftingContainer, HolderLookup.Provider pRegistries) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return this.output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.IMBUEMENT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return BuiltInRegistries.RECIPE_TYPE.get(ArsNouveau.prefix(RecipeRegistry.IMBUEMENT_RECIPE_ID));
    }

    public static class Serializer implements RecipeSerializer<ImbuementRecipe> {

        public static final MapCodec<ImbuementRecipe> CODEC =  RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("input").forGetter(ImbuementRecipe::getInput),
                ItemStack.CODEC.fieldOf("output").forGetter(ImbuementRecipe::getOutput),
                Codec.INT.fieldOf("source").forGetter(ImbuementRecipe::getSource),
                Ingredient.CODEC.listOf().fieldOf("pedestalItems").forGetter(ImbuementRecipe::getPedestalItems)
        ).apply(instance, ImbuementRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ImbuementRecipe> STREAM_CODEC = StreamCodec.of(
                ImbuementRecipe.Serializer::toNetwork, ImbuementRecipe.Serializer::fromNetwork
        );

        public static void toNetwork(RegistryFriendlyByteBuf buf, ImbuementRecipe recipe) {
            buf.writeInt(recipe.pedestalItems.size());
            for (Ingredient i : recipe.pedestalItems) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, i);
            }
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input);
            ItemStack.STREAM_CODEC.encode(buf, recipe.output);
            buf.writeInt(recipe.source);
        }

        public static ImbuementRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int length = buffer.readInt();
            List<Ingredient> stacks = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                try {
                    stacks.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
            return new ImbuementRecipe(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), ItemStack.STREAM_CODEC.decode(buffer), buffer.readInt(), stacks);
        }

        @Override
        public MapCodec<ImbuementRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ImbuementRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
