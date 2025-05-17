package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.imbuement_chamber.IImbuementRecipe;
import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import com.hollingsworth.arsnouveau.common.items.data.WarpScrollData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class WarpScrollImbuementRecipe implements IImbuementRecipe {
    public ResourceLocation id;
    Ingredient input;
    ItemStack copyFrom;
    ItemStack output;
    int source;

    public WarpScrollImbuementRecipe(String id, Ingredient input, ItemStack copyFrom, ItemStack output, int source) {
        this(input, copyFrom, output, source);
        this.id = ArsNouveau.prefix(id);
    }

    private WarpScrollImbuementRecipe(Ingredient input, ItemStack copyFrom, ItemStack output, int source) {
        this.input = input;
        this.copyFrom = copyFrom;
        this.output = output;
        this.source = source;
    }

    public WarpScrollData getWarpScrollData(ImbuementTile tile) {
        if (tile.getPedestalItems().size() != 1) {
            return null;
        }

        var scroll = tile.getPedestalItems().get(0);
        var data = scroll.get(DataComponentRegistry.WARP_SCROLL);
        if (data == null) {
            return null;
        }

        return data.pos().isPresent() ? data : null;
    }

    @Override
    public int getSourceCost(ImbuementTile imbuementTile) {
        return this.source;
    }

    @Override
    public boolean matches(ImbuementTile input, Level level) {
        return this.getWarpScrollData(input) != null &&
               (input.stack.is(ItemsRegistry.WARP_SCROLL.get()) || input.stack.is(ItemsRegistry.STABLE_WARP_SCROLL.get())) &&
               input.stack.getOrDefault(DataComponentRegistry.WARP_SCROLL, new WarpScrollData(false)).pos().isEmpty();
    }

    @Override
    public ItemStack assemble(ImbuementTile input, HolderLookup.Provider registries) {
        ItemStack copy = this.output.copy();
        WarpScrollData data = this.getWarpScrollData(input);
        if (data != null) {
            copy.set(DataComponentRegistry.WARP_SCROLL, data.withCrossDim(copy.is(ItemsRegistry.STABLE_WARP_SCROLL.get())));
        }

        return copy;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return this.output.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.WARP_SCROLL_IMBUEMENT_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return BuiltInRegistries.RECIPE_TYPE.get(ArsNouveau.prefix(RecipeRegistry.WARP_SCROLL_IMBUEMENT_RECIPE_ID));
    }

    public Ingredient getInput() {
        return this.input;
    }

    public ItemStack getCopyFrom() {
        return this.copyFrom;
    }

    public ItemStack getOutput() {
        return this.output;
    }

    public int getSource() {
        return this.source;
    }

    public static class Serializer implements RecipeSerializer<WarpScrollImbuementRecipe> {
        public static final MapCodec<WarpScrollImbuementRecipe> CODEC =  RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("input").forGetter(WarpScrollImbuementRecipe::getInput),
                ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("copyFrom").forGetter(WarpScrollImbuementRecipe::getCopyFrom),
                ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("output").forGetter(WarpScrollImbuementRecipe::getOutput),
                Codec.INT.fieldOf("source").forGetter(WarpScrollImbuementRecipe::getSource)
        ).apply(instance, WarpScrollImbuementRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, WarpScrollImbuementRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, WarpScrollImbuementRecipe::getInput,
                ItemStack.STREAM_CODEC, WarpScrollImbuementRecipe::getCopyFrom,
                ItemStack.STREAM_CODEC, WarpScrollImbuementRecipe::getOutput,
                ByteBufCodecs.VAR_INT, WarpScrollImbuementRecipe::getSource,
                WarpScrollImbuementRecipe::new
        );

        @Override
        public @NotNull MapCodec<WarpScrollImbuementRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, WarpScrollImbuementRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
