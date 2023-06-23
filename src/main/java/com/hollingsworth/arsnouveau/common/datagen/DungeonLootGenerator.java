package com.hollingsworth.arsnouveau.common.datagen;

//TODO: Restore dungeon loot
public class DungeonLootGenerator {
//    private final DataGenerator gen;
//    private final String modid;
//    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
////    public DungeonLootGenerator(DataGenerator gen, String modid) {
////        super(gen, modid);
////        this.gen = gen;
////        this.modid = modid;
////    }
//
//
//    @Override
//    protected void start() {
////        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy());
////        add("dungeon_loot", new DungeonLootEnhancerModifier(new LootItemCondition[]{
////                getList(new String[]{
////                        "chests/simple_dungeon", "chests/jungle_temple", "chests/abandoned_mineshaft", "chests/bastion_treasure", "chests/desert_pyramid", "chests/end_city_treasure",
////                        "chests/ruined_portal", "chests/pillager_outpost", "chests/nether_bridge", "chests/stronghold_corridor", "chests/stronghold_crossing", "chests/stronghold_library"
////                        , "chests/woodland_mansion", "chests/underwater_ruin_big", "chests/underwater_ruin_small"
////                })
////        }));
//    }
//
//    @Override
//    public void run(CachedOutput cache) throws IOException {
//        super.run(cache);
//
////        start();
////
////        Path forgePath = gen.getOutputFolder().resolve("data/forge/loot_modifiers/global_loot_modifiers.json");
////        String modPath = "data/" + modid + "/loot_modifiers/";
////        List<ResourceLocation> entries = new ArrayList<>();
////
////        entries.add(new ResourceLocation(modid, "dungeon_loot"));
////        for(ResourceLocation  resourceLocation : entries) {
////            Path modifierPath = gen.getOutputFolder().resolve(modPath + resourceLocation.getPath() + ".json");
//////            DataProvider.saveStable(cache, json, modifierPath);
////        }
////        JsonObject forgeJson = new JsonObject();
////        forgeJson.addProperty("replace", false);
////        forgeJson.add("entries", GSON.toJsonTree(entries.stream().map(ResourceLocation::toString).collect(Collectors.toList())));
////
////        DataProvider.saveStable(cache, forgeJson, forgePath);
//    }
//
//    public LootItemCondition getList(String[] chests) {
//        LootItemCondition.Builder condition = null;
//
//        for (String s : chests) {
//            if (condition == null) {
//                condition = LootTableIdCondition.builder(new ResourceLocation(s));
//                continue;
//            }
//            condition = condition.or(LootTableIdCondition.builder(new ResourceLocation(s)));
//        }
//        return condition.build();
//    }

}

