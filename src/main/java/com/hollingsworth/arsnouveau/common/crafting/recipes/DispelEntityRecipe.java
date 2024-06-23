package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;

import java.util.Arrays;
import java.util.List;

public record DispelEntityRecipe(EntityType<?> entity, ResourceLocation lootTable, LootItemCondition[] conditions) implements SpecialSingleInputRecipe {

    public boolean matches(LivingEntity killer, Entity victim) {
        if (!victim.getType().equals(this.entity)) return false;

        if (conditions.length == 0) return true;

        LootParams params = getLootParams(killer, victim);
        LootContext context = new LootContext.Builder(params)
                .create(null);

        return Arrays.stream(conditions).allMatch(condition -> condition.test(context));
    }

    private LootParams getLootParams(LivingEntity killer, Entity victim) {
        LootParams.Builder params = new LootParams.Builder((ServerLevel) victim.level)
                .withParameter(LootContextParams.ORIGIN, victim.position())
                .withParameter(LootContextParams.THIS_ENTITY, victim)
                .withParameter(LootContextParams.DAMAGE_SOURCE, killer.damageSources().mobAttack(killer))
                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, killer)
                .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, killer);
        if (killer instanceof ServerPlayer serverPlayer) {
            params = params.withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, serverPlayer)
                    .withLuck(serverPlayer.getLuck());
        }
        return params.create(LootContextParamSets.ENTITY);
    }

    public List<ItemStack> result(LivingEntity killer, Entity victim) {
        if (!victim.getType().equals(this.entity)) return List.of();

        LootParams params = getLootParams(killer, victim);

        LootTable lootTable = killer.level().registryAccess().registryOrThrow(Registries.LOOT_TABLE).get(lootTable());

        return lootTable.getRandomItems(params);
    }

    @Override
    public boolean matches(SingleRecipeInput p_346065_, Level p_345375_) {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.DISPEL_ENTITY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.DISPEL_ENTITY_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<DispelEntityRecipe> {
        public static final MapCodec<DispelEntityRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(DispelEntityRecipe::entity),
                ResourceLocation.CODEC.fieldOf("loot_table").forGetter(DispelEntityRecipe::lootTable),
                IGlobalLootModifier.LOOT_CONDITIONS_CODEC.optionalFieldOf("loot_conditions", new LootItemCondition[]{}).forGetter(DispelEntityRecipe::conditions)
        ).apply(instance, DispelEntityRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, DispelEntityRecipe> STREAM = CheatSerializer.create(CODEC);

        @Override
        public MapCodec<DispelEntityRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DispelEntityRecipe> streamCodec() {
            return STREAM;
        }
    }
}
