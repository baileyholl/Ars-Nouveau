package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.debug.FixedStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GenericRecipeCache<R extends Recipe<I>, I extends RecipeInput> {
    public final RecipeType<R> recipeType;
    protected final FixedStack<RecipeHolder<? extends Recipe<I>>> cache;

    public GenericRecipeCache(RecipeType<R> recipeType, int size) {
        this.recipeType = recipeType;
        this.cache = new FixedStack<>(size);
        NeoForge.EVENT_BUS.addListener(this::onDatapackReload);
    }

    protected void onDatapackReload(OnDatapackSyncEvent event) {
        this.clear();
    }

    @SuppressWarnings("unchecked")
    public @Nullable RecipeHolder<R> get(Level level, I input) {
        for (var cached : this.cache) {
            if (cached.value() instanceof EmptyResultRecipe && cached.value().matches(input, level)) {
                return null;
            }

            var casted = (R) cached.value();
            if (casted.matches(input, level)) {
                return (RecipeHolder<R>) cached;
            }
        }

        var holder = level.getRecipeManager().getRecipeFor(this.recipeType, input, level);
        if (holder.isEmpty()) {
            this.cache.push(new RecipeHolder<>(EmptyResultRecipe.ID, new EmptyResultRecipe<>(input)));
            return null;
        }

        var recipe = holder.get();
        this.cache.push(recipe);
        return recipe;
    }

    public void clear() {
        this.cache.clear();
    }

    record EmptyResultRecipe<I extends RecipeInput>(RecipeInput input) implements Recipe<I> {
        public static final ResourceLocation ID = ArsNouveau.prefix("empty");

        @Override
        public boolean matches(@NotNull I input, @NotNull Level level) {
            if (this.input instanceof SingleRecipeInput single && input instanceof SingleRecipeInput other) {
                return ItemStack.isSameItemSameComponents(single.item(), other.item());
            }
            return this.input.equals(input);
        }

        @Override
        public @NotNull ItemStack assemble(@NotNull I input, HolderLookup.@NotNull Provider registries) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canCraftInDimensions(int width, int height) {
            return true;
        }

        @Override
        public @NotNull ItemStack getResultItem(HolderLookup.@NotNull Provider registries) {
            return ItemStack.EMPTY;
        }

        @Override
        public @NotNull RecipeSerializer<?> getSerializer() {
            return RecipeSerializer.SHAPELESS_RECIPE;
        }

        @Override
        public @NotNull RecipeType<?> getType() {
            return RecipeType.CRAFTING;
        }
    }
}
