package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.common.crafting.recipes.AlakarkinosRecipe;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.*;

public class AlakarkinosConversionRegistry {

    private static List<AlakarkinosRecipe> RECIPES = new ArrayList<>();
    private static Set<Block> CONVERTABLE_BLOCKS = Set.of();
    private static Map<Block, WeightedRandomList<WeightedEntry.Wrapper<AlakarkinosRecipe>>> CONVERTABLE_BLOCKS_MAP = new HashMap<>();

    public static List<AlakarkinosRecipe> getRecipes(){
        return Collections.unmodifiableList(RECIPES);
    }

    public static void reloadAlakarkinosRecipes(RecipeManager recipeManager){
        RECIPES = new ArrayList<>();
        List<AlakarkinosRecipe> recipes = recipeManager.getAllRecipesFor(RecipeRegistry.ALAKARKINOS_RECIPE_TYPE.get()).stream().map(RecipeHolder::value).toList();
        RECIPES.addAll(recipes);
        CONVERTABLE_BLOCKS = new HashSet<>();
        for (AlakarkinosRecipe recipe : RECIPES) {
            CONVERTABLE_BLOCKS.add(recipe.input());
        }

        CONVERTABLE_BLOCKS_MAP = new HashMap<>();
        for (AlakarkinosRecipe recipe : RECIPES) {
            var list = CONVERTABLE_BLOCKS_MAP.getOrDefault(recipe.input(), WeightedRandomList.create());
            var modifiedList = new ArrayList<>(list.unwrap());
            modifiedList.add(WeightedEntry.wrap(recipe, recipe.weight()));
            CONVERTABLE_BLOCKS_MAP.put(recipe.input(), WeightedRandomList.create(modifiedList));
        }

        LootDrop.DROPS.clear();
        for (AlakarkinosRecipe recipe : RECIPES) {
            LootDrop.computeLootDrops(recipe);
        }
    }

    public static boolean isConvertable(Block block) {
        return CONVERTABLE_BLOCKS.contains(block);
    }

    public static @Nullable AlakarkinosRecipe getConversionResult(Block block, RandomSource random) {
        if (!isConvertable(block)) {
            return null;
        }
        var list = CONVERTABLE_BLOCKS_MAP.get(block);
        var entry = list.getRandom(random);
        return entry.map(WeightedEntry.Wrapper::data).orElse(null);
    }

    public record LootDrops(List<LootDrop> list, int weight) {
        public static final Codec<LootDrops> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        LootDrop.CODEC.listOf().fieldOf("list").forGetter(LootDrops::list),
                        Codec.INT.fieldOf("weight").forGetter(LootDrops::weight)
                ).apply(instance, LootDrops::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, LootDrops> STREAM_CODEC = StreamCodec.composite(
                LootDrop.STREAM_CODEC.apply(ByteBufCodecs.list()), LootDrops::list,
                ByteBufCodecs.INT, LootDrops::weight,
                LootDrops::new
        );
    }

    public record LootDrop(ItemStack item, float chance) {
        private static HashMap<ResourceKey<LootTable>, LootDrops> DROPS = new HashMap<>();

        public static final Codec<LootDrop> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ItemStack.CODEC.fieldOf("item").forGetter(LootDrop::item),
                        Codec.FLOAT.fieldOf("chance").forGetter(LootDrop::chance)
                ).apply(instance, LootDrop::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, LootDrop> STREAM_CODEC = StreamCodec.composite(
                ItemStack.STREAM_CODEC, LootDrop::item,
                ByteBufCodecs.FLOAT, LootDrop::chance,
                LootDrop::new
        );

        public static LootDrops getLootDrops(ResourceKey<LootTable> resourceKey) {
            return DROPS.get(resourceKey);
        }

        public static LootDrops computeLootDrops(AlakarkinosRecipe recipe) {
            return DROPS.computeIfAbsent(recipe.table(), key -> {
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                LootTable lootTable = server.reloadableRegistries().getLootTable(key);

                if (lootTable.equals(LootTable.EMPTY)) {
                    return null;
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

                lootDrops.sort(Comparator.comparing(LootDrop::chance).reversed());

                return new LootDrops(lootDrops, CONVERTABLE_BLOCKS_MAP.get(recipe.input()).totalWeight);
            });
        }
    }
}
