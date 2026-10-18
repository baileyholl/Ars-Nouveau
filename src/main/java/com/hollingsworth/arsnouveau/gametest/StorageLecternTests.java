package com.hollingsworth.arsnouveau.gametest;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.container.StoredItemStack;
import com.hollingsworth.arsnouveau.common.block.tile.CraftingLecternTile;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryTile;
import com.hollingsworth.arsnouveau.common.block.tile.StorageLecternTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestSequence;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.UUID;

@GameTestHolder(ArsNouveau.MODID)
@PrefixGameTestTemplate(false)
public class StorageLecternTests {
    private static BlockPos storagePos = new BlockPos(1, 1, 1);
    private static final BlockPos catalogPos = new BlockPos(2, 1, 1);
    private static final BlockPos lecternPos = new BlockPos(3, 1, 1);

    @GameTest(template = "empty10")
    public static void extractsNonStackableItems(GameTestHelper helper) {
        depositAndWithdrawSword(helper, createRepositoryNetwork(helper));
    }

    @GameTest(template = "empty10")
    public static void chestExtractsNonStackableItems(GameTestHelper helper) {
        depositAndWithdrawSword(helper, createChestNetwork(helper));
    }

    @GameTest(template = "empty10")
    public static void extractsBasedOnDataComponents(GameTestHelper helper) {
        withdrawMatchingVariant(helper, createRepositoryNetwork(helper));
    }

    @GameTest(template = "empty10")
    public static void chestExtractsBasedOnDataComponents(GameTestHelper helper) {
        withdrawMatchingVariant(helper, createChestNetwork(helper));
    }

    @GameTest(template = "empty10")
    public static void extractsMergedStacks(GameTestHelper helper) {
        mergeAndWithdrawStack(helper, createRepositoryNetwork(helper));
    }

    @GameTest(template = "empty10")
    public static void chestExtractsMergedStacks(GameTestHelper helper) {
        mergeAndWithdrawStack(helper, createChestNetwork(helper));
    }

    @GameTest(template = "empty10")
    public static void directExtractsNonStackableItems(GameTestHelper helper) {
        depositAndWithdrawSword(helper, createDirectRepositoryNetwork(helper));
    }

    @GameTest(template = "empty10")
    public static void directExtractsBasedOnDataComponents(GameTestHelper helper) {
        withdrawMatchingVariant(helper, createDirectRepositoryNetwork(helper));
    }

    @GameTest(template = "empty10")
    public static void directExtractsMergedStacks(GameTestHelper helper) {
        mergeAndWithdrawStack(helper, createDirectRepositoryNetwork(helper));
    }

    @GameTest(template = "empty10")
    public static void extractsFromStaleEmptySlot(GameTestHelper helper) {
        withdrawFromStaleEmptySlot(helper, createRepositoryNetwork(helper));
    }

    @GameTest(template = "empty10")
    public static void directExtractsFromStaleEmptySlot(GameTestHelper helper) {
        withdrawFromStaleEmptySlot(helper, createDirectRepositoryNetwork(helper));
    }

    @GameTest(template = "empty10")
    public static void craftingRecipesArePerPlayer(GameTestHelper helper) {
        StorageNetwork network = createChestNetwork(helper);
        CraftingLecternTile lectern = helper.getBlockEntity(lecternPos);
        ServerPlayer crafter = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "crafter"));
        ItemStack log = new ItemStack(Items.OAK_LOG);
        startLinked(helper, network)
                .thenExecute(() -> lectern.pushStack(new StoredItemStack(log.copyWithCount(32)), null))
                .thenWaitUntil(() -> helper.assertTrue(listedCount(lectern, log) == 32, "Expected the lectern to list 32 logs"))
                .thenExecute(() -> {
                    lectern.getCraftingInv(crafter).setItem(0, log.copy());
                    lectern.getCraftingInv(UUID.randomUUID()).setItem(0, new ItemStack(Items.DIRT));
                    helper.assertTrue(lectern.getCraftResult(crafter).getItem(0).is(Items.OAK_PLANKS), "Expected another player's grid to leave the crafter's result alone");
                    lectern.craftShift(crafter, null);
                    helper.assertTrue(crafter.getInventory().countItem(Items.OAK_PLANKS) == 64, "Expected a full stack of planks, got: " + crafter.getInventory().countItem(Items.OAK_PLANKS));
                    helper.assertTrue(lectern.getCraftingInv(crafter).getItem(0).is(Items.OAK_LOG), "Expected the grid to be refilled from storage");
                })
                .thenWaitUntil(() -> helper.assertTrue(listedCount(lectern, log) == 16, "Expected crafting to use 16 logs from storage"))
                .thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void breakingLecternDropsEveryCraftingGrid(GameTestHelper helper) {
        helper.setBlock(lecternPos, BlockRegistry.CRAFTING_LECTERN.get());
        CraftingLecternTile lectern = helper.getBlockEntity(lecternPos);
        lectern.getCraftingInv(UUID.randomUUID()).setItem(0, new ItemStack(Items.OAK_LOG, 5));
        lectern.getCraftingInv(UUID.randomUUID()).setItem(0, new ItemStack(Items.COBBLESTONE, 7));
        helper.destroyBlock(lecternPos);
        helper.assertItemEntityCountIs(Items.OAK_LOG, lecternPos, 2, 5);
        helper.assertItemEntityCountIs(Items.COBBLESTONE, lecternPos, 2, 7);
        helper.assertItemEntityCountIs(Items.OAK_PLANKS, lecternPos, 2, 0);
        helper.succeed();
    }

    private static void withdrawFromStaleEmptySlot(GameTestHelper helper, StorageNetwork network) {
        StorageLecternTile lectern = network.lectern();
        RepositoryTile repository = helper.getBlockEntity(storagePos);
        ItemStack sword = new ItemStack(Items.IRON_SWORD);
        startLinked(helper, network)
                .thenExecute(() -> {
                    repository.setItem(0, sword.copy());
                    repository.slotCache.initEmpty(0);
                })
                .thenWaitUntil(() -> helper.assertTrue(listedCount(lectern, sword) == 1, "Expected the lectern to list the sword"))
                .thenExecute(() -> {
                    StoredItemStack pulled = lectern.pullStack(new StoredItemStack(sword), 1, null);
                    helper.assertTrue(pulled != null && ItemStack.isSameItemSameComponents(pulled.getStack(), sword), "Expected to withdraw from a slot wrongly cached as empty");
                })
                .thenWaitUntil(() -> helper.assertTrue(listedCount(lectern, sword) == 0, "Expected the lectern to list no swords"))
                .thenSucceed();
    }

    private static void depositAndWithdrawSword(GameTestHelper helper, StorageNetwork network) {
        StorageLecternTile lectern = network.lectern();
        ItemStack sword = new ItemStack(Items.IRON_SWORD);
        startLinked(helper, network)
                .thenExecute(() -> helper.assertTrue(lectern.pushStack(new StoredItemStack(sword.copy()), null) == null, "Expected sword to be inserted"))
                .thenWaitUntil(() -> helper.assertTrue(listedCount(lectern, sword) == 1, "Expected the lectern to list the sword"))
                .thenExecute(() -> {
                    StoredItemStack pulled = lectern.pullStack(new StoredItemStack(sword), 1, null);
                    helper.assertTrue(pulled != null && ItemStack.isSameItemSameComponents(pulled.getStack(), sword), "Expected to withdraw the sword");
                })
                .thenWaitUntil(() -> helper.assertTrue(listedCount(lectern, sword) == 0, "Expected the lectern to list no swords"))
                .thenSucceed();
    }

    private static void withdrawMatchingVariant(GameTestHelper helper, StorageNetwork network) {
        StorageLecternTile lectern = network.lectern();
        ItemStack damaged = new ItemStack(Items.IRON_SWORD);
        damaged.setDamageValue(10);
        ItemStack fresh = new ItemStack(Items.IRON_SWORD);
        startLinked(helper, network)
                .thenExecute(() -> {
                    lectern.pushStack(new StoredItemStack(damaged.copy()), null);
                    lectern.pushStack(new StoredItemStack(fresh.copy()), null);
                })
                .thenWaitUntil(() -> helper.assertTrue(listedCount(lectern, fresh) == 1 && listedCount(lectern, damaged) == 1, "Expected the lectern to list both swords"))
                .thenExecute(() -> {
                    StoredItemStack pulled = lectern.pullStack(new StoredItemStack(fresh), 1, null);
                    helper.assertTrue(pulled != null && ItemStack.isSameItemSameComponents(pulled.getStack(), fresh), "Expected to withdraw the undamaged sword");
                })
                .thenWaitUntil(() -> helper.assertTrue(listedCount(lectern, fresh) == 0 && listedCount(lectern, damaged) == 1, "Expected only the damaged sword to remain listed"))
                .thenExecute(() -> {
                    StoredItemStack pulled = lectern.pullStack(new StoredItemStack(damaged), 1, null);
                    helper.assertTrue(pulled != null && ItemStack.isSameItemSameComponents(pulled.getStack(), damaged), "Expected to withdraw the damaged sword");
                })
                .thenWaitUntil(() -> helper.assertTrue(listedCount(lectern, damaged) == 0, "Expected the lectern to list no swords"))
                .thenSucceed();
    }

    private static void mergeAndWithdrawStack(GameTestHelper helper, StorageNetwork network) {
        StorageLecternTile lectern = network.lectern();
        ItemStack cobblestone = new ItemStack(Items.COBBLESTONE, 16);
        startLinked(helper, network)
                .thenExecute(() -> {
                    helper.assertTrue(lectern.pushStack(new StoredItemStack(cobblestone.copy()), null) == null, "Expected the first stack to be inserted");
                    helper.assertTrue(lectern.pushStack(new StoredItemStack(cobblestone.copy()), null) == null, "Expected the second stack to be inserted");
                })
                .thenWaitUntil(() -> helper.assertTrue(listedCount(lectern, cobblestone) == 32, "Expected the lectern to list 32 cobblestone"))
                .thenExecute(() -> {
                    StoredItemStack pulled = lectern.pullStack(new StoredItemStack(cobblestone), cobblestone.getMaxStackSize(), null);
                    helper.assertTrue(pulled != null && pulled.getQuantity() == 32, "Expected to withdraw 32 cobblestone, got: " + (pulled == null ? 0 : pulled.getQuantity()));
                })
                .thenWaitUntil(() -> helper.assertTrue(listedCount(lectern, cobblestone) == 0, "Expected the lectern to list no cobblestone"))
                .thenSucceed();
    }

    private record StorageNetwork(StorageLecternTile lectern, BlockPos linkedPos) {
    }

    private static StorageNetwork createRepositoryNetwork(GameTestHelper helper) {
        helper.setBlock(storagePos, BlockRegistry.REPOSITORY.get());
        helper.setBlock(catalogPos, BlockRegistry.REPOSITORY_CONTROLLER.get());
        helper.setBlock(lecternPos, BlockRegistry.CRAFTING_LECTERN.get());
        return new StorageNetwork(helper.getBlockEntity(lecternPos), catalogPos);
    }

    private static StorageNetwork createDirectRepositoryNetwork(GameTestHelper helper) {
        helper.setBlock(storagePos, BlockRegistry.REPOSITORY.get());
        helper.setBlock(lecternPos, BlockRegistry.CRAFTING_LECTERN.get());
        return new StorageNetwork(helper.getBlockEntity(lecternPos), storagePos);
    }

    private static StorageNetwork createChestNetwork(GameTestHelper helper) {
        helper.setBlock(storagePos, Blocks.CHEST);
        helper.setBlock(lecternPos, BlockRegistry.CRAFTING_LECTERN.get());
        return new StorageNetwork(helper.getBlockEntity(lecternPos), storagePos);
    }

    private static GameTestSequence startLinked(GameTestHelper helper, StorageNetwork network) {
        return helper.startSequence()
                .thenIdle(1)
                .thenExecute(() -> {
                    network.lectern().addHandlerPos(network.lectern(), helper.absolutePos(network.linkedPos()));
                    network.lectern().updateItems = true;
                })
                .thenIdle(1);
    }

    private static long listedCount(StorageLecternTile lectern, ItemStack stack) {
        return lectern.getStacks(null).getOrDefault(new StoredItemStack(stack), 0L);
    }
}
