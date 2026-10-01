package com.hollingsworth.arsnouveau.gametest;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.item.inv.SlotCache;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.ItemStackHandler;

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
        helper.assertTrue(collection.size() == 2, "Expected slot collection size 1," + " got: " + collection.size());
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
}
