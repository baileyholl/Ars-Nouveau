package com.hollingsworth.arsnouveau.gametest;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.item.inv.ISlotCache;
import com.hollingsworth.arsnouveau.api.item.inv.ImperfectSlotCache;
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
        var cache = new ImperfectSlotCache();
        cache.initEmpty(0);
        cache.initEmpty(2);
        var collection = cache.getIfPresent(Items.AIR);
        helper.assertTrue(collection != null, "Expected non-null empty slot collection");
        helper.assertTrue(collection.size() == 2, "Expected slot collection size 1," + " got: " + collection.size());
        helper.succeed();
    }

    @GameTest(template = "empty10")
    public static void cachesItems(GameTestHelper helper) {
        ISlotCache cache = new ImperfectSlotCache(10);
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
        ISlotCache cache = new ImperfectSlotCache();
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
        ISlotCache cache = new ImperfectSlotCache(1);
        FilterableItemHandler filterable = new FilterableItemHandler(handler).withSlotCache(cache);
        helper.assertTrue(filterable.insertItemStacked(new ItemStack(Items.COBBLESTONE, 16), false).isEmpty(), "Expected cobblestone to merge");
        helper.assertTrue(!cache.isEmpty(0), "Expected merged slot to stay occupied");
        helper.assertTrue(cache.size() == 1, "Expected cache to stay size of 1");
        helper.assertTrue(cache.getIfPresent(Items.COBBLESTONE).equals(IntArrayList.of(0)), "Expected cobblestone to be merged into slot 0");
        helper.assertTrue(filterable.getHandler().getStackInSlot(0).getCount() == 32, "Expected cobblestone stack to be 32, got: " + filterable.getHandler().getStackInSlot(0).getCount());
        helper.succeed();
    }

    @GameTest(template = "empty10")
    public static void listsLastAcceptingSlotFirst(GameTestHelper helper) {
        ISlotCache cache = createStoneFilledInv(helper);
        var slots = cache.getIfPresent(Items.STONE);
        helper.assertTrue(slots.equals(IntArrayList.of(3, 0, 1, 2)), "Expected the last accepting slot first and every slot once, got: " + slots);
        helper.succeed();
    }

    @GameTest(template = "empty10")
    public static void ignoresStaleLastAcceptingSlot(GameTestHelper helper) {
        ISlotCache cache = createStoneFilledInv(helper);
        cache.replaceSlotWithItem(Items.AIR, 3);
        helper.assertTrue(cache.getIfPresent(Items.STONE).equals(IntArrayList.of(0, 1, 2)), "Expected an emptied hint slot to drop out, got: " + cache.getIfPresent(Items.STONE));
        cache.replaceSlotWithItem(Items.DIRT, 3);
        helper.assertTrue(cache.getIfPresent(Items.STONE).equals(IntArrayList.of(0, 1, 2)), "Expected a hint slot holding another item to drop out, got: " + cache.getIfPresent(Items.STONE));
        cache.replaceSlotWithItem(Items.STONE, 3);
        helper.assertTrue(cache.getIfPresent(Items.STONE).equals(IntArrayList.of(3, 0, 1, 2)), "Expected the hint to apply again once the slot holds stone, got: " + cache.getIfPresent(Items.STONE));
        helper.succeed();
    }

    private static ISlotCache createStoneFilledInv(GameTestHelper helper) {
        ItemStackHandler handler = new ItemStackHandler(4);
        for (int i = 0; i < 4; i++) {
            handler.setStackInSlot(i, new ItemStack(Items.STONE, 63));
        }
        ISlotCache cache = new ImperfectSlotCache(4);
        FilterableItemHandler filterable = new FilterableItemHandler(handler).withSlotCache(cache);
        for (int i = 0; i < 4; i++) {
            helper.assertTrue(filterable.insertItemStacked(new ItemStack(Items.STONE), false).isEmpty(), "Expected stone " + i + " to top up a stack");
        }
        return cache;
    }
}
