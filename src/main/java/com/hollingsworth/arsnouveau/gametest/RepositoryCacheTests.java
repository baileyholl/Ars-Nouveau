package com.hollingsworth.arsnouveau.gametest;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import com.hollingsworth.arsnouveau.common.block.tile.CraftingLecternTile;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryTile;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.mojang.authlib.GameProfile;
import net.minecraft.ReportedException;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.UUID;

import static com.hollingsworth.arsnouveau.gametest.StorageLecternTests.*;

@GameTestHolder(ArsNouveau.MODID)
@PrefixGameTestTemplate(false)
public class RepositoryCacheTests {

    // recreates the case where a user shift clicks items from the repository into their own full inventory, causing minecraft
    // to shrink and grow the stacks in place.

    /**
     * fill repository with full dirt stacks
     * fill your own inventory entirely except for one partial stack of dirt
     * shift click the repository dirt into your inventory so that it grows your own stack and shrinks the repository one in place
     * try to shift click the dirt into the lectern, it will no longer insert
     */
    @GameTest(template = "empty10")
    public static void repositoryAllowsInsertAfterPartialStackRemoval(GameTestHelper helper) {
        StorageNetwork network = createDirectRepositoryNetwork(helper);
        RepositoryTile repository = helper.getBlockEntity(storagePos);
        ServerPlayer filler = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "filler"));
        for (int i = 0; i < 53; i++) {
            depositThroughChestMenu(helper, repository, filler, new ItemStack(Items.DIRT, 64));
        }
        depositThroughChestMenu(helper, repository, filler, new ItemStack(Items.DIRT, 60));
        helper.assertTrue(repository.countItem(Items.DIRT) == 53 * 64 + 60, "Expected 53 full stacks and one of 60, got " + repository.countItem(Items.DIRT) + " dirt");
        startLinked(helper, network)
                .thenExecute(() -> {
                    IItemHandler handler = helper.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, helper.absolutePos(lecternPos), null);
                    ItemStack dryRun = handler.insertItem(0, new ItemStack(Items.DIRT, 64), true);
                    helper.assertTrue(dryRun.getCount() == 60, "Expected the dry run to accept the 4 dirt that fit, got " + dryRun.getCount() + " back");
                    ItemStack remainder = handler.insertItem(0, new ItemStack(Items.DIRT, 64), false);
                    helper.assertTrue(remainder.getCount() == 60, "Expected the insert to accept the 4 dirt the dry run accepted, got " + remainder.getCount() + " back");
                    helper.assertTrue(repository.countItem(Items.DIRT) == 54 * 64, "Expected the partial stack topped up, got " + repository.countItem(Items.DIRT) + " dirt");
                })
                .thenSucceed();
    }

    /**
     * Insert 10 healing arrows
     * Insert 10 poison arrows
     * Insert 10 healing arrows
     * Healing arrows are now across two stacks instead of 1
     */
    @GameTest(template = "empty10")
    public static void insertMergesIntoMatchingVariant(GameTestHelper helper) {
        StorageNetwork network = createDirectRepositoryNetwork(helper);
        RepositoryTile repository = helper.getBlockEntity(storagePos);
        ServerPlayer archer = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "archer"));
        ItemStack poison = PotionContents.createItemStack(Items.TIPPED_ARROW, Potions.POISON);
        ItemStack healing = PotionContents.createItemStack(Items.TIPPED_ARROW, Potions.HEALING);
        depositThroughChestMenu(helper, repository, archer, poison.copyWithCount(30));
        startLinked(helper, network)
                .thenExecute(() -> {
                    helper.assertTrue(pushThroughTerminal(network.lectern(), archer, pickUp(helper, archer, healing.copyWithCount(10))).isEmpty(), "Expected the healing arrows to be inserted");
                    helper.assertTrue(pushThroughTerminal(network.lectern(), archer, pickUp(helper, archer, poison.copyWithCount(10))).isEmpty(), "Expected the poison arrows to be inserted");
                    helper.assertTrue(repository.getItem(0).getCount() == 40, "Expected the poison arrows to merge into slot 0, got slot 0 = " + repository.getItem(0) + ", slot 1 = " + repository.getItem(1) + ", slot 2 = " + repository.getItem(2));
                })
                .thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void partialShiftClickWithdrawalFreesCapacity(GameTestHelper helper) {
        StorageNetwork network = createDirectRepositoryNetwork(helper);
        RepositoryTile repository = helper.getBlockEntity(storagePos);
        ServerPlayer filler = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "filler"));
        for (int i = 0; i < 54; i++) {
            depositThroughChestMenu(helper, repository, filler, new ItemStack(Items.DIRT, 64));
        }
        startLinked(helper, network)
                .thenExecute(() -> {
                    int dirtSlot = pickUp(helper, filler, new ItemStack(Items.DIRT, 4));
                    helper.assertTrue(pushThroughTerminal(network.lectern(), filler, dirtSlot).getCount() == 4, "Expected the full repository to refuse the dirt");
                    ServerPlayer taker = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "taker"));
                    pickUp(helper, taker, new ItemStack(Items.DIRT, 60));
                    for (int i = 0; i < 35; i++) {
                        pickUp(helper, taker, new ItemStack(Items.STONE, 64));
                    }
                    AbstractContainerMenu chest = repository.createMenu(1, taker.getInventory(), taker);
                    taker.containerMenu = chest;
                    chest.clicked(0, 0, ClickType.QUICK_MOVE, taker);
                    helper.assertTrue(repository.getItem(0).getCount() == 60, "Expected the shift-click to take the 4 dirt the taker had room for, got slot 0 = " + repository.getItem(0));
                    ItemStack remainder = pushThroughTerminal(network.lectern(), filler, dirtSlot);
                    helper.assertTrue(remainder.isEmpty(), "Expected the 4 dirt to fit the capacity the shift-click freed, got " + remainder.getCount() + " back");
                    helper.assertTrue(repository.countItem(Items.DIRT) == 54 * 64, "Expected the repository full again, got " + repository.countItem(Items.DIRT) + " dirt");
                })
                .thenSucceed();
    }

    // Place repository
    // Connect lectern to repository, make sure cache is initialized with repository slot count
    // Replace repository with chest
    // Insert via lectern, error is thrown accessint out of bounds slots
    @GameTest(template = "empty10")
    public static void chestReplacingLinkedRepositoryAcceptsItems(GameTestHelper helper) {
        StorageNetwork network = createDirectRepositoryNetwork(helper);
        ServerPlayer filler = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "filler"));
        startLinked(helper, network)
                .thenExecute(() -> helper.setBlock(storagePos, Blocks.CHEST))
                .thenIdle(3)
                .thenExecute(() -> {
                    for (int i = 0; i < 27; i++) {
                        ItemStack remainder = pushThroughTerminal(network.lectern(), filler, pickUp(helper, filler, new ItemStack(Items.DIRT, 64)));
                        helper.assertTrue(remainder.isEmpty(), "Expected the chest to accept stack " + i + ", got " + remainder.getCount() + " back");
                    }
                    int lastSlot = pickUp(helper, filler, new ItemStack(Items.DIRT, 64));
                    ItemStack remainder;
                    try {
                        remainder = pushThroughTerminal(network.lectern(), filler, lastSlot);
                    } catch (ReportedException e) {
                        helper.fail("Expected the full chest to refuse the stack, got " + e.getCause());
                        return;
                    }
                    helper.assertTrue(remainder.getCount() == 64, "Expected the full chest to refuse the stack, got " + remainder.getCount() + " back");
                })
                .thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void extractByItemAfterSlotFilled(GameTestHelper helper) {
        StorageNetwork network = createDirectRepositoryNetwork(helper);
        RepositoryTile repository = helper.getBlockEntity(storagePos);
        ServerPlayer filler = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "filler"));
        depositThroughChestMenu(helper, repository, filler, new ItemStack(Items.DIRT, 64));
        startLinked(helper, network)
                .thenExecute(() -> {
                    helper.assertTrue(pushThroughTerminal(network.lectern(), filler, pickUp(helper, filler, new ItemStack(Items.DIRT, 1))).isEmpty(), "Expected the single dirt to be inserted");
                    ItemStack extracted;
                    try {
                        extracted = helper.getLevel().getCapability(CapabilityRegistry.MAP_INV_CAP, helper.absolutePos(storagePos), null).extractByItem(Items.DIRT, 1, false);
                    } catch (IndexOutOfBoundsException e) {
                        helper.fail("Expected one dirt extracted, got " + e);
                        return;
                    }
                    helper.assertTrue(extracted.is(Items.DIRT) && extracted.getCount() == 1, "Expected one dirt extracted, got " + extracted);
                    helper.assertTrue(repository.countItem(Items.DIRT) == 64, "Expected 64 dirt left, got " + repository.countItem(Items.DIRT));
                })
                .thenSucceed();
    }

    private static int pickUp(GameTestHelper helper, ServerPlayer player, ItemStack stack) {
        helper.assertTrue(player.getInventory().add(stack.copy()), "Expected the player to pick up " + stack);
        return player.getInventory().findSlotMatchingItem(stack);
    }

    private static void depositThroughChestMenu(GameTestHelper helper, RepositoryTile repository, ServerPlayer player, ItemStack stack) {
        int inventorySlot = pickUp(helper, player, stack);
        AbstractContainerMenu menu = repository.createMenu(1, player.getInventory(), player);
        player.containerMenu = menu;
        menu.clicked(menu.findSlot(player.getInventory(), inventorySlot).getAsInt(), 0, ClickType.QUICK_MOVE, player);
    }

    private static ItemStack pushThroughTerminal(CraftingLecternTile lectern, ServerPlayer player, int inventorySlot) {
        CraftingTerminalMenu menu = (CraftingTerminalMenu) lectern.createMenu(1, player.getInventory(), player);
        player.containerMenu = menu;
        menu.clicked(menu.findSlot(player.getInventory(), inventorySlot).getAsInt(), 0, ClickType.QUICK_MOVE, player);
        return player.getInventory().getItem(inventorySlot);
    }
}
