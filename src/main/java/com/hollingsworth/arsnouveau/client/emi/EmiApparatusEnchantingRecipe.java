package com.hollingsworth.arsnouveau.client.emi;

import com.hollingsworth.arsnouveau.common.crafting.recipes.ApparatusRecipeInput;
import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantmentRecipe;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ReactiveEnchantmentRecipe;
import com.hollingsworth.arsnouveau.common.util.HolderHelper;
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

import java.util.ArrayList;
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
        ItemStack dummy = this.getRecipe().enchantLevel > 1 ? EnchantedBookItem.createForEnchantment(new EnchantmentInstance(HolderHelper.unwrap(level, recipe.enchantmentKey), recipe.enchantLevel-1)) : Items.BOOK.getDefaultInstance();
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
                stack = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(HolderHelper.unwrap(level, recipe.enchantmentKey), recipe.enchantLevel));
            } else {
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
    public List<EmiIngredient> getInputs() {
        var inputs = super.getInputs();
        if (!inputs.isEmpty()) {
            inputs.set(0, EmiIngredient.of(this.getEnchantable().stream().map(EmiStack::of).toList()));
        }
        return inputs;
    }

    protected List<ItemStack> enchantableCache = null;

    private List<ItemStack> getEnchantable() {
        if (this.recipe instanceof ReactiveEnchantmentRecipe) {
            enchantableCache = new ArrayList<>();
            for (var item : BuiltInRegistries.ITEM) {
                var stack = item.getDefaultInstance();
                this.addName(stack);
                enchantableCache.add(stack);
            }
            return enchantableCache;
        }

        if (enchantableCache != null) {
            return enchantableCache;
        }

        enchantableCache = new ArrayList<>();
        var level = Minecraft.getInstance().level;
        var enchantment = HolderHelper.unwrap(level, recipe.enchantmentKey);
        for (var item : BuiltInRegistries.ITEM) {
            var stack = item.getDefaultInstance();
            this.addName(stack);
            if (stack.is(Items.ENCHANTED_BOOK)) {
                stack = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, recipe.enchantLevel - 1));
            } else {
                stack.enchant(enchantment, recipe.enchantLevel - 1);
            }

            var apparatus = new ApparatusRecipeInput(stack, List.of(), null);
            try {
                if (recipe.doesReagentMatch(apparatus, level, null)) {
                    enchantableCache.add(stack);
                }
            } catch (Exception ignored) {}
        }

        return enchantableCache;
    }

    private ItemStack getStack(Random r, List<ItemStack> of, ItemStack def) {
        return of.isEmpty() ? def : of.get(r.nextInt(of.size()));
    }
}
