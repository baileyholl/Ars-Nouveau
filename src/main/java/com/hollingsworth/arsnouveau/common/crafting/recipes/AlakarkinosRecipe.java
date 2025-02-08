package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.NeoForgeEventHandler;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.text.DecimalFormat;
import java.util.*;

public record AlakarkinosRecipe(Block input, ResourceKey<LootTable> table, int weight, Optional<List<LootDrop>> drops) implements SpecialSingleInputRecipe {
    public AlakarkinosRecipe(Block input, ResourceKey<LootTable> table, int weight) {
        this(input, table, weight, Optional.empty());
    }

    public boolean matches(BlockState block) {
        return block.is(input);
    }

    @Override
    public boolean matches(SingleRecipeInput p_346065_, Level p_345375_) {
        return false;
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.ALAKARKINOS_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.ALAKARKINOS_RECIPE_TYPE.get();
    }


    public static class Serializer implements RecipeSerializer<AlakarkinosRecipe> {
        public static final MapCodec<AlakarkinosRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("input").forGetter(AlakarkinosRecipe::input),
                ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("table").forGetter(AlakarkinosRecipe::table),
                Codec.INT.fieldOf("weight").forGetter(AlakarkinosRecipe::weight),
                LootDrop.CODEC.listOf().optionalFieldOf("drops").forGetter(AlakarkinosRecipe::drops)
        ).apply(instance, AlakarkinosRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, AlakarkinosRecipe> STREAM = StreamCodec.of(
                AlakarkinosRecipe.Serializer::toNetwork, AlakarkinosRecipe.Serializer::fromNetwork
        );

        private static AlakarkinosRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
            return CheatSerializer.fromNetwork(CODEC, buf);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buf, AlakarkinosRecipe karky) {
            List<LootDrop> drops = LootDrop.getLootDrops(karky.table);
            AlakarkinosRecipe recipe = new AlakarkinosRecipe(karky.input, karky.table, karky.weight, Optional.of(drops));
            CheatSerializer.toNetwork(CODEC, buf, recipe);
        }

        @Override
        public MapCodec<AlakarkinosRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AlakarkinosRecipe> streamCodec() {
            return STREAM;
        }
    }

    public record LootDrop(ItemStack item, float chance) {
        private static HashMap<ResourceKey<LootTable>, List<LootDrop>> DROPS = new HashMap<>();

        public static Codec<LootDrop> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ItemStack.CODEC.fieldOf("item").forGetter(LootDrop::item),
                        Codec.FLOAT.fieldOf("chance").forGetter(LootDrop::chance)
                ).apply(instance, LootDrop::new)
        );

        public static List<LootDrop> getLootDrops(ResourceKey<LootTable> resourceKey) {
            return DROPS.computeIfAbsent(resourceKey, key -> {
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                LootTable lootTable = server.reloadableRegistries().getLootTable(key);

                if (lootTable.equals(LootTable.EMPTY)) {
                    return Collections.emptyList();
                }

                ServerLevel level = server.overworld();
                ANFakePlayer player = ANFakePlayer.getPlayer(level);
                LootParams params = new LootParams.Builder(level)
                        .withParameter(LootContextParams.ORIGIN, BlockPos.ZERO.getCenter())
                        .withLuck(1.0f)
                        .withParameter(LootContextParams.THIS_ENTITY, player)
                        .create(LootContextParamSets.CHEST);

                HashMap<Integer, Pair<ItemStack, Integer>> drops = new HashMap<>();
                for (int i = 0; i < 600; i++) {
                    for (ItemStack random : lootTable.getRandomItems(params)) {
                        drops.compute(ItemStack.hashItemAndComponents(random), (k, v) -> {
                           if (v == null) return Pair.of(random, 1);
                           return v.mapSecond(count -> count + 1);
                        });
                    }
                }

                int totalDrops = drops.values().stream().mapToInt(Pair::getSecond).sum();

                List<LootDrop> lootDrops = new ArrayList<>();
                for (Pair<ItemStack, Integer> entry : drops.values()) {
                    LootDrop drop = new LootDrop(entry.getFirst(), (float) entry.getSecond() / totalDrops);
                    lootDrops.add(drop);
                }

                lootDrops.sort(Comparator.comparing(AlakarkinosRecipe.LootDrop::chance).reversed());

                return lootDrops;
            });
        }
    }
}
