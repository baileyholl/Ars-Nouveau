package com.hollingsworth.arsnouveau.api.loot;

import com.google.common.collect.ImmutableSet;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.setup.registry.LootRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;

public class DrygmyLootCondition implements LootItemCondition {
    public static final MapCodec<DrygmyLootCondition> CODEC = MapCodec.unit(DrygmyLootCondition::new);

    @Override
    public boolean test(LootContext context) {
            Entity entity = context.getParamOrNull(LootContextParams.ATTACKING_ENTITY);
        if (!(entity instanceof ANFakePlayer fakePlayer)) return false;
        if (!fakePlayer.hasCustomName()) return false;

        Component name = fakePlayer.getCustomName();
        if (name == null) return false;

        return name.getString().equals("Drygmy");
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.ATTACKING_ENTITY);
    }

    @Override
    public LootItemConditionType getType() {
        return LootRegistry.IS_DRYGMY.get();
    }

    public static DrygmyLootCondition.Builder drygmy() {
        return new Builder();
    }

    public static class Builder implements LootItemCondition.Builder {
        public DrygmyLootCondition build() {
            return new DrygmyLootCondition();
        }
    }
}
