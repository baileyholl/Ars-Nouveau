package com.hollingsworth.arsnouveau.api.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public record DispelEntityRecipe(ResourceLocation id, EntityType<?> entity, ResourceLocation lootTable, LootItemCondition[] conditions) implements Recipe<Container> {
    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

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
                .withOptionalParameter(LootContextParams.KILLER_ENTITY, killer)
                .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, killer);
        if (killer instanceof ServerPlayer serverPlayer) {
            params = params.withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, serverPlayer)
                    .withLuck(serverPlayer.getLuck());
        }
        return params.create(LootContextParamSets.ENTITY);
    }

    public List<ItemStack> result(LivingEntity killer, Entity victim) {
        if (!victim.getType().equals(this.entity)) return List.of();

        LootParams params = getLootParams(killer, victim);

        LootTable lootTable = killer.level().getServer().getLootData().getLootTable(lootTable());

        return lootTable.getRandomItems(params);
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.DISPEL_ENTITY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.DISPEL_ENTITY_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public JsonElement asRecipe() {
        JsonElement recipe = Serializer.CODEC.encodeStart(JsonOps.INSTANCE, this).result().orElse(null);
        if (recipe == null) return null;
        JsonObject obj = recipe.getAsJsonObject();
        obj.addProperty("type", getType().toString());
        return obj;
    }

    public static class Serializer implements RecipeSerializer<DispelEntityRecipe> {
        public static final Codec<DispelEntityRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(DispelEntityRecipe::id),
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(DispelEntityRecipe::entity),
                ResourceLocation.CODEC.fieldOf("loot_table").forGetter(DispelEntityRecipe::lootTable),
                IGlobalLootModifier.LOOT_CONDITIONS_CODEC.optionalFieldOf("loot_conditions", new LootItemCondition[]{}).forGetter(DispelEntityRecipe::conditions)
        ).apply(instance, DispelEntityRecipe::new));

        @Override
        public DispelEntityRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            return CODEC.parse(JsonOps.INSTANCE, jsonObject).result().orElse(null);
        }

        @Override
        public @Nullable DispelEntityRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            return friendlyByteBuf.readJsonWithCodec(CODEC);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, DispelEntityRecipe buddingConversionRecipe) {
            friendlyByteBuf.writeJsonWithCodec(CODEC, buddingConversionRecipe);
        }
    }
}
