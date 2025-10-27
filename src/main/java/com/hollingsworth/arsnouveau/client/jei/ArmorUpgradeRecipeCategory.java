package com.hollingsworth.arsnouveau.client.jei;

import com.hollingsworth.arsnouveau.api.perk.PerkSlot;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.common.armor.AnimatedMagicArmor;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ArmorUpgradeRecipe;
import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArmorUpgradeRecipeCategory extends EnchantingApparatusRecipeCategory<ArmorUpgradeRecipe> {

    public ArmorUpgradeRecipeCategory(IGuiHelper helper) {
        super(helper);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ArmorUpgradeRecipe recipe, IFocusGroup focuses) {
        MultiProvider provider = multiProvider.apply(recipe);
        List<Ingredient> inputs = provider.input();
        double angleBetweenEach = 360.0 / inputs.size();

        List<ItemStack> stacks = PerkRegistry.getPerkProviderItems().stream().filter(item -> item instanceof AnimatedMagicArmor ama && ama.getMinTier() < recipe.tier).map(Item::getDefaultInstance).toList();
        List<ItemStack> outputs = new ArrayList<>();

        if (!focuses.isEmpty()) {
            //takes a copy of the magic armor hovered
            List<ItemStack> list = focuses.getItemStackFocuses(RecipeIngredientRole.CATALYST).map(i -> i.getTypedValue().getIngredient().copy()).filter(i -> i.getItem() instanceof AnimatedMagicArmor).toList();
            List<ItemStack> list2 = focuses.getItemStackFocuses(RecipeIngredientRole.OUTPUT).map(i -> i.getTypedValue().getIngredient().copy()).filter(i -> i.getItem() instanceof AnimatedMagicArmor).toList();
            if (!list.isEmpty()) {
                stacks = list;
            } else if (!list2.isEmpty()) {
                stacks = list2;
            }
        }
        for (ItemStack stack : stacks) {
            ItemStack copy = stack.copy();
            List<List<PerkSlot>> perkProvider = PerkRegistry.getPerkProvider(stack.getItem());
            if (perkProvider != null) {
                ArmorPerkHolder perkHolder = stack.getOrDefault(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder(null, List.of(), 0, new HashMap<>()));
                stack.set(DataComponentRegistry.ARMOR_PERKS, perkHolder.setTier(recipe.tier - 1));
                ArmorPerkHolder copyHolder = copy.getOrDefault(DataComponentRegistry.ARMOR_PERKS, new ArmorPerkHolder(null, List.of(), 0, new HashMap<>()));
                copy.set(DataComponentRegistry.ARMOR_PERKS, copyHolder.setTier(recipe.tier));
            }
            outputs.add(copy);
        }
        builder.addSlot(RecipeIngredientRole.INPUT, 48, 45).addItemStacks(stacks);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 10).addItemStacks(outputs);
        for (Ingredient input : inputs) {
            builder.addSlot(RecipeIngredientRole.INPUT, (int) point.x, (int) point.y).addIngredients(input);
            point = rotatePointAbout(point, center, angleBetweenEach);
        }

    }

    @Override
    public Component getTitle() {
        return Component.translatable("ars_nouveau.armor_upgrade");
    }

    @Override
    public RecipeType<ArmorUpgradeRecipe> getRecipeType() {
        return JEIArsNouveauPlugin.ARMOR_RECIPE_TYPE;
    }

    @Override
    public void draw(ArmorUpgradeRecipe recipe, @NotNull IRecipeSlotsView slotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        Font renderer = Minecraft.getInstance().font;
        graphics.drawString(renderer, Component.translatable("ars_nouveau.tier", 1 + recipe.tier), 0, 0, 10, false);

        if (recipe.consumesSource())
            graphics.drawString(renderer, Component.translatable("ars_nouveau.source", recipe.sourceCost()), 0, 100, 10, false);
    }
}
