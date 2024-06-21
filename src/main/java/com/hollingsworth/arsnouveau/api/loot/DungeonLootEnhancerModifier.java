package com.hollingsworth.arsnouveau.api.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;

public class DungeonLootEnhancerModifier extends LootModifier {

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

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        generatedLoot.addAll(DungeonLootTables.getRandomRoll(this));
        return generatedLoot;
    }

    public static final MapCodec<DungeonLootEnhancerModifier> CODEC = RecordCodecBuilder.mapCodec(instance ->
        codecStart(instance)
            .and(
                instance.group(
                    Codec.DOUBLE.optionalFieldOf("common_chance", 0.30).forGetter(d -> d.commonChance),
                    Codec.DOUBLE.optionalFieldOf("uncommon_chance", 0.2).forGetter(d -> d.uncommonChance),
                    Codec.DOUBLE.optionalFieldOf("rare_chance", 0.1).forGetter(d -> d.rareChance),
                    Codec.INT.optionalFieldOf("common_rolls", 3).forGetter(d -> d.commonRolls),
                    Codec.INT.optionalFieldOf("uncommon_rolls", 2).forGetter(d -> d.uncommonRolls),
                    Codec.INT.optionalFieldOf("rare_rolls", 1).forGetter(d -> d.rareRolls)
                )
            )
            .apply(instance, DungeonLootEnhancerModifier::new));

    @Override
    public MapCodec<DungeonLootEnhancerModifier> codec() {
        return CODEC;
    }

}
