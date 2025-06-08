package com.hollingsworth.arsnouveau.client.emi;

import com.google.common.collect.AbstractIterator;
import com.hollingsworth.arsnouveau.common.event.EventHandler;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class EmiMultiInputRecipe<T> implements EmiRecipe {
    protected T recipe;
    protected ResourceLocation id;
    protected MultiProvider multiProvider;

    protected Vec2 point;
    protected Vec2 center;

    public EmiMultiInputRecipe(ResourceLocation id, T recipe, MultiProvider multiProvider) {
        this.id = id;
        this.recipe = recipe;
        this.multiProvider = multiProvider;
        this.reset();
    }

    public T getRecipe() {
        return this.recipe;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return this.id;
    }

    public void reset() {
        var w = this.getDisplayWidth();
        var h = this.getDisplayHeight();

        this.center = new Vec2((int) (w * 0.5) - 8, (int) (h * 0.5) - 9);
        this.point = center.add(new Vec2(0, -32));
    }

    @Override
    public int getDisplayWidth() {
        return 120;
    }

    public MultiProvider getMultiProvider() {
        return this.multiProvider;
    }

    public EmiIngredient getCenter() {
        return this.multiProvider.getEmiCenter();
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        this.reset();
        MultiProvider provider = multiProvider;
        List<Ingredient> inputs = provider.input;
        double angleBetweenEach = 360.0 / inputs.size();
        var centerIngredient = this.getCenter();
        if (centerIngredient != null) {
            widgets.addSlot(centerIngredient, (int) this.center.x, (int) this.center.y);
        }

        for (EmiIngredient input : provider.getEmiInputs()) {
            widgets.addSlot(input, (int) point.x, (int) point.y);
            point = rotatePointAbout(point, center, angleBetweenEach);
        }

        var outputs = this.getOutputs();
        if (!outputs.isEmpty()) {
            widgets.addSlot(outputs.getFirst(), 100, 3).recipeContext(this);
        }
    }

    List<EmiIngredient> inputs = null;

    /**
     * Returns cached inputs generated once from {@link EmiMultiInputRecipe#generateInputs}.
     * Do not call from {@link EmiMultiInputRecipe#generateInputs} as it will result in a stack overflow.
     *
     * @return A list of ingredients required for the recipe.
     * 	Inputs will consider this recipe a use when exploring recipes.
     */
    @Override
    public final List<EmiIngredient> getInputs() {
        if (this.inputs == null) {
            this.inputs = this.generateInputs();
            EventHandler.reloadListeners.add(e -> {
                this.inputs = null;
                return false;
            });
        }

        return this.inputs;
    }

    protected List<EmiIngredient> generateInputs() {
        int size = this.multiProvider.input.size();
        if (this.multiProvider.hasCenter()) {
            size += 1;
        }

        ArrayList<EmiIngredient> inputs = new ArrayList<>(size);
        if (this.multiProvider.hasCenter()) {
            inputs.add(this.multiProvider.getEmiCenter());
        }

        for (var entry : this.multiProvider.input) {
            inputs.add(EmiIngredient.of(entry));
        }

        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(EmiStack.of(this.multiProvider.output));
    }

    public static Vec2 rotatePointAbout(Vec2 in, Vec2 about, double degrees) {
        double rad = degrees * Math.PI / 180.0;
        double newX = Math.cos(rad) * (in.x - about.x) - Math.sin(rad) * (in.y - about.y) + about.x;
        double newY = Math.sin(rad) * (in.x - about.x) + Math.cos(rad) * (in.y - about.y) + about.y;
        return new Vec2((float) newX, (float) newY);
    }

    public record MultiProvider(ItemStack output, List<Ingredient> input, @Nullable Ingredient optionalCenter) {
        public EmiStack getEmiOutput() {
            return EmiStack.of(output);
        }

        public boolean hasCenter() {
            return optionalCenter != null;
        }

        @Nullable
        public EmiIngredient getEmiCenter() {
            if (optionalCenter == null) {
                return null;
            }
            return EmiIngredient.of(optionalCenter);
        }

        public Iterable<EmiIngredient> getEmiInputs() {
            return new Iterable<>() {
                @NotNull
                @Override
                public Iterator<EmiIngredient> iterator() {
                    return new AbstractIterator<>() {
                        final Iterator<Ingredient> iterator = input.iterator();

                        @Nullable
                        @Override
                        protected EmiIngredient computeNext() {
                            if (!iterator.hasNext()) {
                                return endOfData();
                            }

                            return EmiIngredient.of(iterator.next());
                        }
                    };
                }
            };
        }
    }
}
