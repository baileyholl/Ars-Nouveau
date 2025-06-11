package com.hollingsworth.arsnouveau.client.emi;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ApparatusRecipeInput;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantmentRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ReactiveEnchantmentRecipe;
import com.hollingsworth.arsnouveau.common.util.HolderHelper;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTooltipComponents;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class EmiApparatusEnchantingRecipe extends EmiEnchantingApparatusRecipe<EnchantmentRecipe> {
    private final int uniq = ThreadLocalRandom.current().nextInt();

    public EmiApparatusEnchantingRecipe(ResourceLocation id, EnchantmentRecipe recipe) {
        super(id, recipe);
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EmiArsNouveauPlugin.APPARATUS_ENCHANTING_CATEGORY;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        this.reset();
        double angleBetweenEach = 360.0 / this.recipe.pedestalItems().size();

        MultiProvider provider = multiProvider;
        Level level = Minecraft.getInstance().level;
        ItemStack dummy = this.getRecipe().enchantLevel > 1 ? this.createEnchantedBook(this.recipe.enchantLevel - 1) : Items.BOOK.getDefaultInstance();
        Component message = this.getRecipe().enchantLevel == 1 ? Component.literal("Any compatible item") : Component.literal("Needs lower level enchantment");
        dummy.set(DataComponents.CUSTOM_NAME, message); //TODO Translatable

        var enchantable = this.getEnchantable();
        if (enchantable.isEmpty()) {
            enchantable.add(dummy);
        }

        widgets.addGeneratedSlot(r -> EmiStack.of(getStack(r, enchantable, dummy)), uniq, (int) this.center.x, (int) this.center.y).appendTooltip(() -> EmiTooltipComponents.getIngredientTooltipComponent(enchantable.stream().map(EmiStack::of).toList()));

        for (EmiIngredient input : provider.getEmiInputs()) {
            widgets.addSlot(input, (int) point.x, (int) point.y);
            point = rotatePointAbout(point, center, angleBetweenEach);
        }

        widgets.addGeneratedSlot(r -> {
            var stack = getStack(r, enchantable, dummy).copy();
            stack.remove(DataComponents.CUSTOM_NAME);
            if (stack.is(Items.ENCHANTED_BOOK)) {
                stack = this.createEnchantedBook();
            } else if (level != null) {
                stack.enchant(HolderHelper.unwrap(level, recipe.enchantmentKey), recipe.enchantLevel);
            }
            return EmiStack.of(stack);
        }, uniq, 100, 3).recipeContext(this);

        this.addSourceWidget(widgets);
    }

    private void addName(ItemStack stack) {
        Component message = this.getRecipe().enchantLevel == 1 ? Component.literal("Any compatible item") : Component.literal("Needs lower level enchantment");
        stack.set(DataComponents.CUSTOM_NAME, message); //TODO Translatable
    }

    @Override
    protected List<EmiIngredient> generateInputs() {
        var inputs = super.generateInputs();
        if (!inputs.isEmpty()) {
            inputs.set(0, EmiIngredient.of(this.getEnchantable().stream().map(EmiStack::of).toList()));
        }
        return inputs;
    }

    public ItemStack createEnchantedBook(int level) {
        return EnchantedBookItem.createForEnchantment(new EnchantmentInstance(HolderHelper.unwrap(Minecraft.getInstance().level, recipe.enchantmentKey), level));
    }

    public ItemStack createEnchantedBook() {
        return createEnchantedBook(this.recipe.enchantLevel);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(EmiStack.of(this.createEnchantedBook()));
    }

    protected List<ItemStack> enchantableCache = null;

    // Special static cache to fix re-initialization lag with many items.
    protected static List<ItemStack> reactiveEnchantableCache = null;

    private List<ItemStack> getEnchantable() {
        if (this.recipe instanceof ReactiveEnchantmentRecipe || this.recipe.enchantmentKey == EnchantmentRegistry.REACTIVE_ENCHANTMENT) {
            if (reactiveEnchantableCache == null) {
                ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
                for (var item : BuiltInRegistries.ITEM) {
                    if (item == Items.AIR) {
                        continue;
                    }
                    var stack = item.getDefaultInstance();
                    this.addName(stack);
                    builder.add(stack);
                }
                reactiveEnchantableCache = builder.build();
            }
            enchantableCache = reactiveEnchantableCache;
        }

        if (enchantableCache == null) {
            ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
            var level = Minecraft.getInstance().level;
            if (level != null && level.holder(recipe.enchantmentKey).isPresent()) {
                var enchantment = HolderHelper.unwrap(level, recipe.enchantmentKey);
                for (var item : BuiltInRegistries.ITEM) {
                    var stack = item.getDefaultInstance();
                    this.addName(stack);
                    if (stack.is(Items.ENCHANTED_BOOK)) {
                        stack = this.createEnchantedBook(this.recipe.enchantLevel - 1);
                    } else {
                        stack.enchant(enchantment, recipe.enchantLevel - 1);
                    }

                    var apparatus = new ApparatusRecipeInput(stack, List.of(), null);
                    try {
                        if (recipe.doesReagentMatch(apparatus, level, null)) {
                            builder.add(stack);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }

            enchantableCache = builder.build();
        }

        return enchantableCache;
    }

    private ItemStack getStack(Random r, List<ItemStack> of, ItemStack def) {
        return of.isEmpty() ? def : of.get(r.nextInt(of.size()));
    }
}
