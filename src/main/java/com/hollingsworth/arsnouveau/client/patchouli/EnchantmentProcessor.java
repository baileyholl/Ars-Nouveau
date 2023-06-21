package com.hollingsworth.arsnouveau.client.patchouli;

//
//public class EnchantmentProcessor implements IComponentProcessor {
//    EnchantmentRecipe recipe;
//
//    @Override
//    public void setup(IVariableProvider variables) {
//        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
//        String recipeID = variables.get("recipe").asString();
//        recipe = (EnchantmentRecipe) manager.byKey(new ResourceLocation(recipeID)).orElse(null);
//    }
//
//    @Override
//    public IVariable process(String key) {
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
