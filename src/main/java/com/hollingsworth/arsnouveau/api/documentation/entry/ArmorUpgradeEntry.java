package com.hollingsworth.arsnouveau.api.documentation.entry;

import com.hollingsworth.arsnouveau.api.documentation.SinglePageCtor;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import com.hollingsworth.arsnouveau.common.crafting.recipes.ArmorUpgradeRecipe;
import com.hollingsworth.arsnouveau.common.items.data.ArmorPerkHolder;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ArmorUpgradeEntry extends PedestalRecipeEntry{
    RecipeHolder<ArmorUpgradeRecipe> recipeHolder;
    public ArmorUpgradeEntry(BaseDocScreen parent, int x, int y, int width, int height, RecipeHolder<ArmorUpgradeRecipe> recipe) {
        super(parent, x, y, width, height);
        this.title = Component.translatable("block.ars_nouveau.enchanting_apparatus");
        this.recipeHolder = recipe;

        if(recipe != null && recipe.value() != null) {
            this.ingredients = recipe.value().pedestalItems();
            this.reagentStack = Ingredient.of(new ItemStack(ItemsRegistry.ARCANIST_ROBES.get()));
            this.outputStack = new ItemStack(ItemsRegistry.ARCANIST_ROBES.get());
            ArmorPerkHolder perkHolder = PerkUtil.getPerkHolder(outputStack);
            if(!(perkHolder instanceof ArmorPerkHolder armorPerkHolder)){
                outputStack = outputStack.copy();
                return;
            }
            outputStack.set(DataComponentRegistry.ARMOR_PERKS, armorPerkHolder.setTier(recipe.value().tier));
        }
    }

    public static SinglePageCtor create(RecipeHolder<ArmorUpgradeRecipe> recipe){
        return (parent, x, y, width, height) -> new ArmorUpgradeEntry(parent, x, y, width, height, recipe);
    }
}
