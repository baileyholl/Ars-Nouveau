package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.loot.LootTables;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class DungeonLootGenerator extends GlobalLootModifierProvider {

    public DungeonLootGenerator(DataGenerator gen, String modid) {
        super(gen, modid);
    }
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> GLM = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, ArsNouveau.MODID);
    public static final RegistryObject<DungeonLootEnhancerModifier.Serializer> DUNGEON_LOOT = GLM.register("dungeon_loot", DungeonLootGenerator.DungeonLootEnhancerModifier.Serializer::new);

    @Override
    protected void start() {
        add("dungeon_loot", DUNGEON_LOOT.get(), new DungeonLootEnhancerModifier(  new LootItemCondition[] {
                getList(new String[]{
                        "chests/simple_dungeon","chests/jungle_temple", "chests/abandoned_mineshaft","chests/bastion_treasure","chests/desert_pyramid","chests/end_city_treasure",
                        "chests/ruined_portal","chests/pillager_outpost", "chests/nether_bridge","chests/stronghold_corridor",  "chests/stronghold_crossing", "chests/stronghold_library"
                        ,"chests/woodland_mansion", "chests/underwater_ruin_big", "chests/underwater_ruin_small"
                })
        }));
    }

    public LootItemCondition getList(String[] chests){
        LootItemCondition.Builder condition = null;

        for(String s : chests){
            if(condition == null) {
                condition = LootTableIdCondition.builder(new ResourceLocation(s));
                continue;
            }
            condition = condition.or(LootTableIdCondition.builder(new ResourceLocation(s)));
        }
        return condition.build();
    }

    public static class DungeonLootEnhancerModifier extends LootModifier {

        public double commonChance;
        public double uncommonChance;
        public double rareChance;

        public int commonRolls;
        public int uncommonRolls;
        public int rareRolls;


        public DungeonLootEnhancerModifier(final LootItemCondition[] conditionsIn, double commonChance, double uncommonChance, double rareChance, int commonRolls, int uncommonRolls, int rareRolls) {
            super(conditionsIn);
            this.commonChance = commonChance;
            this.uncommonChance = uncommonChance;
            this.rareChance = rareChance;

            this.commonRolls = commonRolls;
            this.uncommonRolls = uncommonRolls;
            this.rareRolls = rareRolls;
        }

        public DungeonLootEnhancerModifier(final LootItemCondition[] conditionsIn) {
            super(conditionsIn);
            this.commonChance = 0.30;
            this.uncommonChance = 0.2;
            this.rareChance =  0.1;

            this.commonRolls = 3;
            this.uncommonRolls = 2;
            this.rareRolls = 1;
        }

        @Override
        protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
            generatedLoot.addAll(LootTables.getRandomRoll(this));
            return generatedLoot;
        }

        public static class Serializer extends GlobalLootModifierSerializer<DungeonLootEnhancerModifier> {
            @Override
            public DungeonLootEnhancerModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
                return new DungeonLootEnhancerModifier(conditions,
                        object.get("common_chance").getAsDouble(),
                        object.get("uncommon_chance").getAsDouble(),
                        object.get("rare_chance").getAsDouble(),
                        object.get("common_rolls").getAsInt(),
                        object.get("uncommon_rolls").getAsInt(),
                        object.get("rare_rolls").getAsInt()

                );
            }

            @Override
            public JsonObject write(DungeonLootEnhancerModifier instance) {
                final JsonObject obj = this.makeConditions(instance.conditions);
                obj.addProperty("common_chance", instance.commonChance);
                obj.addProperty("uncommon_chance", instance.uncommonChance);
                obj.addProperty("rare_chance", instance.rareChance);

                obj.addProperty("common_rolls", instance.commonRolls);
                obj.addProperty("uncommon_rolls", instance.uncommonRolls);
                obj.addProperty("rare_rolls", instance.rareRolls);
                return obj;
            }
        }
    }
}
