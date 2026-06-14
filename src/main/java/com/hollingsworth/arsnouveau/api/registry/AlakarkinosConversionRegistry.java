package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.common.crafting.recipes.AlakarkinosRecipe;
import com.hollingsworth.arsnouveau.common.mixin.ExplorationMapFunctionAccessor;
import com.hollingsworth.arsnouveau.common.mixin.LootItemConditionalFunctionAccessor;
import com.hollingsworth.arsnouveau.common.mixin.LootPoolAccessor;
import com.hollingsworth.arsnouveau.common.mixin.LootTableAccessor;
import com.hollingsworth.arsnouveau.common.util.Log;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

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
        public static Codec<LootDrops> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        LootDrop.CODEC.listOf().fieldOf("list").forGetter(LootDrops::list),
                        Codec.INT.fieldOf("weight").forGetter(LootDrops::weight)
                ).apply(instance, LootDrops::new)
        );
    }

    ;

    public record LootDrop(ItemStack item, float chance) {
        private static HashMap<ResourceKey<LootTable>, LootDrops> DROPS = new HashMap<>();

        public static Codec<LootDrop> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ItemStack.CODEC.fieldOf("item").forGetter(LootDrop::item),
                        Codec.FLOAT.fieldOf("chance").forGetter(LootDrop::chance)
                ).apply(instance, LootDrop::new)
        );

        public static LootDrops getLootDrops(ResourceKey<LootTable> resourceKey) {
            return DROPS.get(resourceKey);
        }

        @Deprecated
        public static LootDrops computeLootDrops(AlakarkinosRecipe recipe) {
            return computeLootDrops(ServerLifecycleHooks.getCurrentServer(), recipe);
        }

        public static LootDrops computeLootDrops(MinecraftServer server, AlakarkinosRecipe recipe) {
            return DROPS.computeIfAbsent(recipe.table(), key -> {

                LootTable lootTable = server.reloadableRegistries().getLootTable(key);

                if (lootTable.equals(LootTable.EMPTY)) {
                    return null;
                }

                Object2IntMap<ItemStack> drops = new Object2IntOpenCustomHashMap<>(new Hash.Strategy<>() {
                    @Override
                    public int hashCode(ItemStack o) {
                        return ItemStack.hashItemAndComponents(o);
                    }

                    @Override
                    public boolean equals(ItemStack a, ItemStack b) {
                        return (a == null && b == null) || (a != null && b != null && ItemStack.isSameItemSameComponents(a, b));
                    }
                });

                ANFakePlayer player = ANFakePlayer.getPlayer(server.overworld());
                LootParams lootParams = new LootParams.Builder(server.overworld())
                        .withParameter(LootContextParams.ORIGIN, BlockPos.ZERO.getCenter())
                        .withLuck(1.0f)
                        .withParameter(LootContextParams.THIS_ENTITY, player)
                        .create(LootContextParamSets.CHEST);

                var lta = (LootTableAccessor) lootTable;
                var functions = new ArrayList<LootItemFunction>();
                for (var function : lta.getFunctions()) {
                    if (function instanceof ExplorationMapFunction map) {
                        functions.add(DummyExplorationMapFunction.wrap(map));
                    } else {
                        functions.add(function);
                    }
                }

                var compositeFunction = LootItemFunctions.compose(functions);
                var context = new LootContext.Builder(lootParams).create(lta.getRandomSequence());

                var out = new ObjectArrayList<ItemStack>();

                for (int i = 0; i < 600; i++) {
                    LootContext.VisitedEntry<?> visitedentry = LootContext.createVisitedEntry(lootTable);
                    if (context.pushVisitedElement(visitedentry)) {
                        Consumer<ItemStack> consumer = LootItemFunction.decorate(compositeFunction, out::add, context);

                        for (LootPool lootpool : lootTable.pools) {
                            var lpa = (LootPoolAccessor) lootpool;
                            if (lpa.getCompositeCondition().test(context)) {
                                Consumer<ItemStack> poolConsumer = LootItemFunction.decorate(compositeFunction, consumer, context);
                                int items = lootpool.getRolls().getInt(context) + Mth.floor(lootpool.getBonusRolls().getFloat(context) * context.getLuck());

                                for(int j = 0; j < items; ++j) {
                                    lpa.callAddRandomItem(poolConsumer, context);
                                }
                            }
                        }

                        out = CommonHooks.modifyLoot(lootTable.getLootTableId(), out, context);

                        context.popVisitedElement(visitedentry);
                    } else {
                        Log.getLogger().warn("Detected infinite loop in loot tables");
                    }

                    for (var stack : out) {
                        drops.computeInt(stack, (o, n) -> stack.getCount() + (n == null ? 0 : n.intValue()));
                    }

                    out.clear();
                }

                int totalDrops = 0;

                var iter = Object2IntMaps.fastIterator(drops);
                while (iter.hasNext()) {
                    var entry = iter.next();
                    totalDrops += entry.getIntValue();
                }

                List<LootDrop> lootDrops = new ArrayList<>(drops.size());
                iter = Object2IntMaps.fastIterator(drops);
                while (iter.hasNext()) {
                    var entry = iter.next();
                    LootDrop drop = new LootDrop(entry.getKey(), (float) entry.getIntValue() / totalDrops);
                    lootDrops.add(drop);
                }

                lootDrops.sort(Comparator.comparing(LootDrop::chance).reversed());

                return new LootDrops(lootDrops, CONVERTABLE_BLOCKS_MAP.get(recipe.input()).totalWeight);
            });
        }
    }

    static final class DummyExplorationMapFunction extends ExplorationMapFunction {
        public DummyExplorationMapFunction(List<LootItemCondition> conditons, TagKey<Structure> destination, Holder<MapDecorationType> mapDecoration, byte zoom, int searchRadius, boolean skipKnownStructures) {
            super(conditons, destination, mapDecoration, zoom, searchRadius, skipKnownStructures);
        }

        public static DummyExplorationMapFunction wrap(ExplorationMapFunction map) {
            var acc = (ExplorationMapFunctionAccessor) map;
            return new DummyExplorationMapFunction(((LootItemConditionalFunctionAccessor) map).getPredicates(), acc.getDestination(), acc.getMapDecoration(), acc.getZoom(), acc.getSearchRadius(), acc.getSkipKnownStructures());
        }

        @Override
        public @NotNull ItemStack run(@NotNull ItemStack stack, @NotNull LootContext context) {
            return stack;
        }
    }
}
