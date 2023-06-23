package com.hollingsworth.arsnouveau.common.datagen;

//
//public class SummonRitualProvider implements DataProvider{
//
//    public List<SummonRitualRecipe> recipes = new ArrayList<>();
//    public final DataGenerator generator;
//
//
//    public SummonRitualProvider(DataGenerator generatorIn) {
//        this.generator = generatorIn;
//    }
//    @Override
//    public void run(CachedOutput cache) throws IOException {
//        addEntries();
//        Path output = this.generator.getOutputFolder();
//        for (SummonRitualRecipe recipe : recipes) {
//                Path path = getRecipePath(output, recipe.getId().getPath());
//                DataProvider.saveStable(cache, recipe.asRecipe(), path);
//            }
//        }
//
//    protected void addEntries() {
//        // ArrayList<SummonRitualRecipe.WeightedMobType> bats = new ArrayList<>();
//        // bats.add(new SummonRitualRecipe.WeightedMobType(EntityType.getKey(EntityType.BAT)));
//        // recipes.add(new SummonRitualRecipe(new ResourceLocation("", ""), Ingredient.of(Items.AMETHYST_SHARD), SummonRitualRecipe.MobSource.MOB_LIST, 5, bats));
//    }
//
//    protected static Path getRecipePath(Path path, String id) {
//        return path.resolve("data/ars_nouveau/recipes/summon_ritual/" + id + ".json");
//    }
//
//    /**
//     * Gets a name for this provider, to use in logging.
//     */
//    @Override
//    public String getName() {
//        return "Summon Ritual Datagen";
//    }
//}
