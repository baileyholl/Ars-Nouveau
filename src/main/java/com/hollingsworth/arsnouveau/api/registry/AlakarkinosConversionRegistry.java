package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.loot.LootTableUtil;
import com.hollingsworth.arsnouveau.common.crafting.recipes.AlakarkinosRecipe;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import javax.annotation.Nullable;
import java.util.*;

public class AlakarkinosConversionRegistry {

    private static List<AlakarkinosRecipe> RECIPES = new ArrayList<>();
    private static Set<Block> CONVERTABLE_BLOCKS = Set.of();
    private static Map<Block, WeightedRandomList<WeightedEntry.Wrapper<AlakarkinosRecipe>>> CONVERTABLE_BLOCKS_MAP = new HashMap<>();

    public static List<AlakarkinosRecipe> getRecipes() {
        return Collections.unmodifiableList(RECIPES);
    }

    public static void reloadAlakarkinosRecipes(RecipeManager recipeManager, MinecraftServer server) {

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
            LootDrop.computeLootDrops(server, recipe);
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
    }

    public record LootDrop(ItemStack item, float chance) {
        private static final Map<DropKey, LootDrops> DROPS = new HashMap<>();

        public static final Codec<LootDrop> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ItemStack.CODEC.fieldOf("item").forGetter(LootDrop::item),
                        Codec.FLOAT.fieldOf("chance").forGetter(LootDrop::chance)
                ).apply(instance, LootDrop::new)
        );

        public static @Nullable LootDrops getLootDrops(ResourceKey<LootTable> table, Block input) {
            return DROPS.get(new DropKey(table, input));
        }

        public static void computeLootDrops(MinecraftServer server, AlakarkinosRecipe recipe) {
            DROPS.computeIfAbsent(new DropKey(recipe.table(), recipe.input()), key -> createLootDrops(server, recipe));
        }

        private static @Nullable LootDrops createLootDrops(MinecraftServer server, AlakarkinosRecipe recipe) {
            LootTable lootTable = server.reloadableRegistries().getLootTable(recipe.table());
            if (lootTable.equals(LootTable.EMPTY)) {
                return null;
            }

            Object2DoubleMap<Item> expected = LootTableUtil.expectedDrops(server, lootTable);

            double total = 0;
            for (var entry : Object2DoubleMaps.fastIterable(expected)) {
                total += entry.getDoubleValue();
            }
            if (total <= 0) {
                return null;
            }

            List<LootDrop> lootDrops = new ArrayList<>(expected.size());
            for (var entry : Object2DoubleMaps.fastIterable(expected)) {
                lootDrops.add(new LootDrop(new ItemStack(entry.getKey()), (float) (entry.getDoubleValue() / total)));
            }
            lootDrops.sort(Comparator.comparing(LootDrop::chance).reversed());

            return new LootDrops(lootDrops, CONVERTABLE_BLOCKS_MAP.get(recipe.input()).totalWeight);
        }

        private record DropKey(ResourceKey<LootTable> table, Block input) {
        }
    }
}
