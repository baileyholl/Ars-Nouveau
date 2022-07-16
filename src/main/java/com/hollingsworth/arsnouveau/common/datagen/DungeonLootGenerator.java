package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.api.loot.DungeonLootTables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;

import java.util.function.Supplier;

//TODO: Restore dungeon loot
public class DungeonLootGenerator extends GlobalLootModifierProvider {
    public DungeonLootGenerator(DataGenerator gen, String modid) {
        super(gen, modid);
    }

//    public static final DeferredRegister<LootModifier> GLM = DeferredRegister.create(new ResourceLocation("forge:loot_modifier_serializers"), ArsNouveau.MODID);
//   public static final RegistryObject<DungeonLootEnhancerModifier> DUNGEON_LOOT = GLM.register("dungeon_loot", DungeonLootEnhancerModifier::new);

    @Override
    protected void start() {
//        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy());
//        add("dungeon_loot", new DungeonLootEnhancerModifier(new LootItemCondition[]{
//                getList(new String[]{
//                        "chests/simple_dungeon", "chests/jungle_temple", "chests/abandoned_mineshaft", "chests/bastion_treasure", "chests/desert_pyramid", "chests/end_city_treasure",
//                        "chests/ruined_portal", "chests/pillager_outpost", "chests/nether_bridge", "chests/stronghold_corridor", "chests/stronghold_crossing", "chests/stronghold_library"
//                        , "chests/woodland_mansion", "chests/underwater_ruin_big", "chests/underwater_ruin_small"
//                })
//        }));
    }

    public LootItemCondition getList(String[] chests) {
        LootItemCondition.Builder condition = null;

        for (String s : chests) {
            if (condition == null) {
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
            this.rareChance = 0.1;

            this.commonRolls = 3;
            this.uncommonRolls = 2;
            this.rareRolls = 1;
        }

        @Override
        protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
            generatedLoot.addAll(DungeonLootTables.getRandomRoll(this));
            return generatedLoot;
        }
        public static final Supplier<Codec<DungeonLootEnhancerModifier>> CODEC = () -> RecordCodecBuilder.create(instance -> instance.group(
                        LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(lm -> lm.conditions),
                        Codec.DOUBLE.fieldOf("common_chance").forGetter(d -> d.commonChance),
                        Codec.DOUBLE.fieldOf("uncommon_chance").forGetter(d -> d.uncommonChance),
                        Codec.DOUBLE.fieldOf("rare_chance").forGetter(d -> d.rareChance),
                        Codec.INT.fieldOf("common_rolls").forGetter(d -> d.commonRolls),
                        Codec.INT.fieldOf("uncommon_rolls").forGetter(d -> d.uncommonRolls),
                        Codec.INT.fieldOf("rare_rolls").forGetter(d -> d.rareRolls)
                ).apply(instance, DungeonLootEnhancerModifier::new));
        @Override
        public Codec<? extends IGlobalLootModifier> codec() {
            return CODEC.get();
        }
    }
}

