package com.hollingsworth.arsnouveau.gametest;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.item.inv.SlotCache;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.function.ToIntFunction;

@GameTestHolder(ArsNouveau.MODID)
@PrefixGameTestTemplate(false)
public class SlotCacheTests {
    @GameTest(template = "empty10")
    public static void cachesEmptySlots(GameTestHelper helper) {
        SlotCache cache = new SlotCache();
        cache.initEmpty(0);
        cache.initEmpty(2);
        var collection = cache.getIfPresent(Items.AIR);
        helper.assertTrue(collection != null, "Expected non-null empty slot collection");
        helper.assertValueEqual(collection.size(), 2, "slot collection size");
        helper.succeed();
    }

    @GameTest(template = "empty10")
    public static void cachesItems(GameTestHelper helper) {
        SlotCache cache = new SlotCache(10);
        // Edge case with empty -> item -> empty -> item
        cache.replaceSlotWithItem(Items.AIR, 3);
        cache.replaceSlotWithItem(Items.STICK, 3);
        cache.replaceSlotWithItem(Items.AIR, 3);
        cache.replaceSlotWithItem(Items.STICK, 3);
        cache.replaceSlotWithItem(Items.STICK, 9);
        var collection = cache.getIfPresent(Items.STICK);
        helper.assertTrue(collection != null, "Expected non-null stick slot collection");
        helper.assertTrue(collection.size() == 2, "Expected slot collection size 2," + " got: " + collection.size());
        helper.assertTrue(collection.contains(3) && collection.contains(9), "Expected slot collection to contain 3 and 9, got: " + collection);
        helper.succeed();
    }

    @GameTest(template = "empty10")
    public static void dynamicallyGrowsCache(GameTestHelper helper) {
        SlotCache cache = new SlotCache();
        cache.replaceSlotWithItem(Items.STICK, 3);
        cache.replaceSlotWithItem(Items.STICK, 9);
        var collection = cache.getIfPresent(Items.STICK);
        helper.assertTrue(collection != null, "Expected non-null stick slot collection");
        helper.assertTrue(collection.size() == 2, "Expected slot collection size 2," + " got: " + collection.size());
        helper.assertTrue(collection.contains(3) && collection.contains(9), "Expected slot collection to contain 3 and 9, got: " + collection);
        helper.succeed();
    }

    @GameTest(template = "empty10")
    public static void mergesStacks(GameTestHelper helper) {
        ItemStackHandler handler = new ItemStackHandler(3);
        handler.setStackInSlot(0, new ItemStack(Items.COBBLESTONE, 16));
        SlotCache cache = new SlotCache(1);
        FilterableItemHandler filterable = new FilterableItemHandler(handler).withSlotCache(cache);
        helper.assertTrue(filterable.insertItemStacked(new ItemStack(Items.COBBLESTONE, 16), false).isEmpty(), "Expected cobblestone to merge");
        helper.assertTrue(!cache.isEmpty(0), "Expected merged slot to stay occupied");
        helper.assertTrue(cache.size() == 1, "Expected cache to stay size of 1");
        helper.assertTrue(cache.getIfPresent(Items.COBBLESTONE).equals(IntArrayList.of(0)), "Expected cobblestone to be merged into slot 0");
        helper.assertTrue(filterable.getHandler().getStackInSlot(0).getCount() == 32, "Expected cobblestone stack to be 32, got: " + filterable.getHandler().getStackInSlot(0).getCount());
        helper.succeed();
    }

    @GameTest(template = "empty10")
    public static void repoPerfectCaching(GameTestHelper helper) {
        var pos = helper.relativePos(BlockPos.ZERO);
        helper.setBlock(pos, BlockRegistry.REPOSITORY.get());
        var repo = (RepositoryTile) helper.getBlockEntity(pos);
        repo.attachFilters();
        repo.initCache();
        var cache = repo.slotCache;

        helper.assertTrue(repo.insertStack(new ItemStack(Items.COBBLESTONE, 16), false).isEmpty(), "Expected cobblestone to be fully inserted");
        repo.setItem(20, new ItemStack(Items.STONE, 32));
        repo.setItem(40, new ItemStack(Items.STONE, 16));
        helper.assertTrue(repo.getStackInSlot(20).is(Items.STONE), "Expected stone to be fully inserted");
        helper.assertTrue(repo.insertItem(3, new ItemStack(Items.SMOOTH_STONE, 64), false).isEmpty(), "Expected smooth stone to be fully inserted");

        ToIntFunction<Item> occupancy = (item) -> {
            var slots = cache.getIfPresent(item);
            return slots == null ? 0 : slots.size();
        };

        helper.assertTrue(cache.size() - cache.emptyCount() == 4, "cache should see 4 non-empty slots");
        helper.assertTrue(occupancy.applyAsInt(Items.COBBLESTONE) == 1, "cache should see 1 cobblestone slot");
        helper.assertTrue(occupancy.applyAsInt(Items.STONE) == 2, "cache should see 2 stone slots");
        helper.assertTrue(occupancy.applyAsInt(Items.SMOOTH_STONE) == 1, "cache should see 1 smooth stone filled");

        helper.assertTrue(repo.extractItem(0, 64, false).getCount() == 16, "expected to extract all cobblestone");
        helper.assertValueEqual(repo.extractByItem(Items.STONE, 64, false).getCount(), 48, "extracted stone count");
        helper.assertTrue(repo.getItem(20).isEmpty(), "expected to extract all stone");
        helper.assertTrue(repo.getItem(40).isEmpty(), "expected to extract all stone");
        repo.setItem(3, ItemStack.EMPTY);
        helper.assertTrue(repo.getItem(3).isEmpty(), "expected to delete all smooth stone");

        helper.assertTrue(cache.size() - cache.emptyCount() == 0, "cache should see all empty slots");

        helper.assertTrue(repo.insertStack(new ItemStack(Items.SMOOTH_STONE, 16), false).isEmpty(), "Expected smooth stone to be fully inserted");
        helper.assertTrue(repo.insertStack(new ItemStack(Items.COBBLESTONE, 32), false).isEmpty(), "Expected cobblestone to be fully inserted");
        helper.assertTrue(repo.insertStack(new ItemStack(Items.STONE, 64), false).isEmpty(), "Expected stone to be fully inserted");

        helper.assertValueEqual(repo.getItem(0).getItem(), Items.SMOOTH_STONE, "slot 0 item");
        helper.assertValueEqual(repo.getItem(0).getCount(), 16, "slot 0 count");
        helper.assertValueEqual(repo.getItem(1).getItem(), Items.COBBLESTONE, "slot 1 item");
        helper.assertValueEqual(repo.getItem(1).getCount(), 32, "slot 1 count");
        helper.assertValueEqual(repo.getItem(2).getItem(), Items.STONE, "slot 2 item");
        helper.assertValueEqual(repo.getItem(2).getCount(), 64, "slot 2 count");

        helper.assertTrue(cache.size() - cache.emptyCount() == 3, "cache should see 3 non-empty slots");
        helper.assertTrue(occupancy.applyAsInt(Items.COBBLESTONE) == 1, "cache should see 1 cobblestone slot");
        helper.assertTrue(occupancy.applyAsInt(Items.STONE) == 1, "cache should see 1 stone slot");
        helper.assertTrue(occupancy.applyAsInt(Items.SMOOTH_STONE) == 1, "cache should see 1 smooth stone filled");

        helper.succeed();
    }
}
