package com.hollingsworth.arsnouveau.client.patchouli;

//
//public class ApparatusProcessor implements IComponentProcessor {
//    EnchantingApparatusRecipe recipe;
//
//    @Override
//    public void setup(IVariableProvider variables) {
//        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
//        String recipeID = variables.get("recipe").asString();
//        recipe = (EnchantingApparatusRecipe) manager.byKey(new ResourceLocation(recipeID)).orElse(null);
//    }
//
//    @Override
//    public IVariable process(String key) {
//        if (recipe == null)
//            return null;
//        if (key.equals("reagent"))
//            return IVariable.wrapList(Arrays.stream(recipe.reagent.getItems()).map(IVariable::from).collect(Collectors.toList()));
//
//        if (key.equals("recipe")) {
//            return IVariable.wrap(recipe.getId().toString());
//        }
//        if (key.equals("output")) {
//            return IVariable.from(recipe.result);
//        }
//        if (key.equals("footer")) {
//            return IVariable.wrap(recipe.result.getItem().getDescriptionId());
//        }
//
//        return null;
//    }
//}
