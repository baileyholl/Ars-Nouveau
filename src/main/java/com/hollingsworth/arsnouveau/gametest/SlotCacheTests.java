package com.hollingsworth.arsnouveau.gametest;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.inv.SlotCache;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

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
        cache.replaceSlotWithItem(Items.AIR, Items.STICK, 3);
        cache.replaceSlotWithItem(Items.AIR, Items.STICK, 9);
        var collection = cache.getIfPresent(Items.STICK);
        helper.assertTrue(collection != null, "Expected non-null stick slot collection");
        helper.assertTrue(collection.size() == 2, "Expected slot collection size 2," + " got: " + collection.size());
        helper.assertTrue(collection.contains(3) && collection.contains(9), "Expected slot collection to contain 3 and 9, got: " + collection);
        helper.succeed();
    }

    @GameTest(template = "empty10")
    public static void dynamicallyGrowsCache(GameTestHelper helper) {
        SlotCache cache = new SlotCache();
        cache.replaceSlotWithItem(Items.AIR, Items.STICK, 3);
        cache.replaceSlotWithItem(Items.AIR, Items.STICK, 9);
        var collection = cache.getIfPresent(Items.STICK);
        helper.assertTrue(collection != null, "Expected non-null stick slot collection");
        helper.assertTrue(collection.size() == 2, "Expected slot collection size 2," + " got: " + collection.size());
        helper.assertTrue(collection.contains(3) && collection.contains(9), "Expected slot collection to contain 3 and 9, got: " + collection);
        helper.succeed();
    }

}
