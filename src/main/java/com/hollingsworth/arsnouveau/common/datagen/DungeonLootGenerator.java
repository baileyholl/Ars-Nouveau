package com.hollingsworth.arsnouveau.common.datagen;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.loot.LootTables;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
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
        add("dungeon_loot", DUNGEON_LOOT.get(), new DungeonLootEnhancerModifier(  new ILootCondition[] {
                getList(new String[]{
                        "chests/simple_dungeon","chests/jungle_temple", "chests/abandoned_mineshaft","chests/bastion_treasure","chests/desert_pyramid","chests/end_city_treasure",
                        "chests/ruined_portal","chests/pillager_outpost", "chests/nether_bridge","chests/stronghold_corridor",  "chests/stronghold_crossing", "chests/stronghold_library"
                        ,"chests/woodland_mansion", "chests/underwater_ruin_big", "chests/underwater_ruin_small"
                })
        }));
    }

    public ILootCondition getList(String[] chests){
        ILootCondition.IBuilder condition = null;

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
        public DungeonLootEnhancerModifier(final ILootCondition[] conditionsIn, double commonChance, double uncommonChance, double rareChance) {
            super(conditionsIn);
            this.commonChance = commonChance;
            this.uncommonChance = uncommonChance;
            this.rareChance = rareChance;
        }

        public DungeonLootEnhancerModifier(final ILootCondition[] conditionsIn) {
            super(conditionsIn);
            this.commonChance = 0.40;
            this.uncommonChance = 0.30;
            this.rareChance =  0.20;
        }

        @Override
        protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
            generatedLoot.addAll(LootTables.getRandomRoll(this));
            return generatedLoot;
        }

        public static class Serializer extends GlobalLootModifierSerializer<DungeonLootEnhancerModifier> {
            @Override
            public DungeonLootEnhancerModifier read(ResourceLocation location, JsonObject object, ILootCondition[] conditions) {
                return new DungeonLootEnhancerModifier(conditions,
                        object.get("common_chance").getAsDouble(),
                        object.get("uncommon_chance").getAsDouble(),
                        object.get("rare_chance").getAsDouble());
            }

            @Override
            public JsonObject write(DungeonLootEnhancerModifier instance) {
                final JsonObject obj = this.makeConditions(instance.conditions);
                obj.addProperty("common_chance", instance.commonChance);
                obj.addProperty("uncommon_chance", instance.uncommonChance);
                obj.addProperty("rare_chance", instance.rareChance);
                return obj;
            }
        }
    }
}
