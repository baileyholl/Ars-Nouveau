package com.hollingsworth.arsnouveau.api.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import java.util.function.Supplier;

public class DungeonLootEnhancerModifier extends LootModifier{


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
