//package com.hollingsworth.arsnouveau.client.patchouli;
//
//
//import com.hollingsworth.arsnouveau.common.crafting.recipes.EnchantmentRecipe;
//import net.minecraft.client.Minecraft;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.Ingredient;
//import net.minecraft.world.item.crafting.RecipeManager;
//import net.minecraft.world.level.Level;
//import vazkii.patchouli.api.IComponentProcessor;
//import vazkii.patchouli.api.IVariable;
//import vazkii.patchouli.api.IVariableProvider;
//
//import java.util.Arrays;
//import java.util.stream.Collectors;
//
//public class EnchantmentProcessor implements IComponentProcessor {
//    EnchantmentRecipe recipe;
//
//    @Override
//    public void setup(Level level, IVariableProvider variables) {
//        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
//        String recipeID = variables.get("recipe").asString();
//        recipe = (EnchantmentRecipe) manager.byKey(ResourceLocation.tryParse(recipeID)).orElse(null);
//    }
//
//    @Override
//    public IVariable process(Level level, String key) {
//        if (recipe == null)
//            return null;
//        if (key.equals("enchantment"))
//            return IVariable.wrap(recipe.enchantment.getDescriptionId());
//        if (key.equals("level"))
//            return IVariable.wrap(recipe.enchantLevel);
//
//        if (key.startsWith("item")) {
//            int index = Integer.parseInt(key.substring(4)) - 1;
//            if (recipe.pedestalItems.size() <= index)
//                return IVariable.from(ItemStack.EMPTY);
//            Ingredient ingredient = recipe.pedestalItems.get(Integer.parseInt(key.substring(4)) - 1);
//            return IVariable.wrapList(Arrays.stream(ingredient.getItems()).map(IVariable::from).collect(Collectors.toList()));
//        }
//
//        return null;
//    }
//}
