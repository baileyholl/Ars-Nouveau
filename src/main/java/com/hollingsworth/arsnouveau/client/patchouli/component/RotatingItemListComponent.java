package com.hollingsworth.arsnouveau.client.patchouli.component;

//public class RotatingItemListComponent extends RotatingItemListComponentBase {
//    @SerializedName("recipe_name")
//    public String recipeName;
//
//    @SerializedName("recipe_type")
//    public String recipeType;
//
//    @Override
//    protected List<Ingredient> makeIngredients() {
//        ClientLevel world = Minecraft.getInstance().level;
//        if (world == null) return new ArrayList<>();
//
//        Map<ResourceLocation, ? extends Recipe<?>> map;
//        if ("enchanting_apparatus".equals(recipeType)) {
//            EnchantingApparatusRecipe recipe = world.getRecipeManager().getAllRecipesFor(RecipeRegistry.APPARATUS_TYPE.get()).stream().filter(f -> f.id.toString().equals(recipeName)).findFirst().orElse(null);
//            for(RecipeType type : ArsNouveauAPI.getInstance().getEnchantingRecipeTypes()){
//                RecipeType<IEnchantingRecipe> enchantingRecipeRecipeType = (RecipeType<IEnchantingRecipe>) type;
//                Recipe<?> recipe1 = world.getRecipeManager().getAllRecipesFor(enchantingRecipeRecipeType).stream().filter(f -> f.getId().toString().equals(recipeName)).findFirst().orElse(null);
//                if(recipe1 instanceof EnchantingApparatusRecipe enchantingApparatusRecipe){
//                    recipe = enchantingApparatusRecipe;
//                    break;
//                }
//            }
//            return recipe == null ? ImmutableList.of() : recipe.pedestalItems;
//        } else if ("imbuement_chamber".equals(recipeType)) {
//            ImbuementRecipe recipe = world.getRecipeManager().getAllRecipesFor(RecipeRegistry.IMBUEMENT_TYPE.get()).stream().filter(f -> f.id.toString().equals(recipeName)).findFirst().orElse(null);
//            return recipe == null ? ImmutableList.of() : recipe.pedestalItems;
//        } else if ("glyph_recipe".equals(recipeType)) {
//            GlyphRecipe recipe = (GlyphRecipe) world.getRecipeManager().byKey(new ResourceLocation(recipeName)).orElse(null);
//            return recipe == null ? ImmutableList.of() : recipe.inputs;
//        } else {
//            throw new IllegalArgumentException("Type must be 'enchanting_apparatus', 'glyph_recipe', or 'imbuement_chamber'!");
//        }
//    }
//
//    @Override
//    public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
//        recipeName = lookup.apply(IVariable.wrap(recipeName)).asString();
//        recipeType = lookup.apply(IVariable.wrap(recipeType)).asString();
//    }
//}
