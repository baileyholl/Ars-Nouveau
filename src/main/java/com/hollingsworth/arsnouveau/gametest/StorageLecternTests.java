package com.hollingsworth.arsnouveau.gametest;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import com.hollingsworth.arsnouveau.client.container.StoredItemStack;
import com.hollingsworth.arsnouveau.common.block.tile.CraftingLecternTile;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryTile;
import com.hollingsworth.arsnouveau.common.network.ClientTransferHandlerPacket;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestSequence;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@GameTestHolder(ArsNouveau.MODID)
@PrefixGameTestTemplate(false)
public class StorageLecternTests {
    private static BlockPos storagePos = new BlockPos(1, 1, 1);
    private static final BlockPos catalogPos = new BlockPos(2, 1, 1);
    private static final BlockPos lecternPos = new BlockPos(3, 1, 1);
    private static final BlockPos chainedLecternPos = new BlockPos(5, 1, 1);
    private static final TicketType<ChunkPos> UNSAVED_CHUNK = TicketType.create("ars_nouveau_unsaved_chunk_test", Comparator.comparingLong(ChunkPos::toLong));

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
        CraftingLecternTile lectern = network.lectern();
        ServerPlayer crafter = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "crafter"));
        ItemStack log = new ItemStack(Items.OAK_LOG);
        startLinked(helper, network)
                .thenExecute(() -> lectern.pushStack(new StoredItemStack(log.copyWithCount(32)), null, false))
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

    @GameTest(template = "empty10")
    public static void shiftCraftingCopiesEachBookOnce(GameTestHelper helper) {
        helper.setBlock(lecternPos, BlockRegistry.CRAFTING_LECTERN.get());
        CraftingLecternTile lectern = helper.getBlockEntity(lecternPos);
        ServerPlayer crafter = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "crafter"));
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        book.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(Filterable.passThrough("Notes"), "crafter", 0, List.of(Filterable.passThrough(Component.literal("page"))), true));
        crafter.getInventory().add(book);
        for (int i = 0; i < 9; i++) {
            crafter.getInventory().add(new ItemStack(Items.WRITABLE_BOOK));
        }
        CraftingTerminalMenu menu = openMenu(lectern, crafter);
        moveToGrid(lectern, menu, crafter, Items.WRITTEN_BOOK, 0);
        moveToGrid(lectern, menu, crafter, Items.WRITABLE_BOOK, 1, 2, 3, 4, 5, 6, 7, 8);
        menu.clicked(menu.getResultSlotIndex(), 0, ClickType.QUICK_MOVE, crafter);
        int copies = crafter.getInventory().countItem(Items.WRITTEN_BOOK);
        helper.assertTrue(copies == 9, "Expected 9 copies from 9 writable books, got: " + copies);
        helper.assertTrue(lectern.getCraftingInv(crafter).getItem(0).is(Items.WRITTEN_BOOK), "Expected the original book to stay in the grid");
        helper.succeed();
    }

    @GameTest(template = "empty10")
    public static void shiftCraftingStopsWhenResultChanges(GameTestHelper helper) {
        helper.setBlock(lecternPos, BlockRegistry.CRAFTING_LECTERN.get());
        CraftingLecternTile lectern = helper.getBlockEntity(lecternPos);
        ServerPlayer crafter = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "crafter"));
        ItemStack star = new ItemStack(Items.FIREWORK_STAR);
        star.set(DataComponents.FIREWORK_EXPLOSION, new FireworkExplosion(FireworkExplosion.Shape.SMALL_BALL, IntList.of(0xFF0000), IntList.of(), false, false));
        crafter.getInventory().add(new ItemStack(Items.PAPER, 21));
        crafter.getInventory().add(new ItemStack(Items.GUNPOWDER, 63));
        crafter.getInventory().add(star);
        CraftingTerminalMenu menu = openMenu(lectern, crafter);
        moveToGrid(lectern, menu, crafter, Items.PAPER, 0);
        moveToGrid(lectern, menu, crafter, Items.GUNPOWDER, 1, 2, 3);
        moveToGrid(lectern, menu, crafter, Items.FIREWORK_STAR, 4);
        menu.clicked(menu.getResultSlotIndex(), 0, ClickType.QUICK_MOVE, crafter);
        int starred = rocketCount(crafter, true);
        int plain = rocketCount(crafter, false);
        helper.assertTrue(starred == 3 && plain == 0, "Expected 3 rockets with the star, got " + starred + " with it and " + plain + " without");
        menu.clicked(menu.getResultSlotIndex(), 0, ClickType.QUICK_MOVE, crafter);
        starred = rocketCount(crafter, true);
        plain = rocketCount(crafter, false);
        helper.assertTrue(starred == 3 && plain == 60, "Expected the next shift-click to add 60 plain rockets, got " + starred + " with the star and " + plain + " without");
        helper.succeed();
    }

    @GameTest(template = "empty10")
    public static void breakingLecternInvalidatesOpenMenu(GameTestHelper helper) {
        helper.setBlock(lecternPos, BlockRegistry.CRAFTING_LECTERN.get());
        CraftingLecternTile lectern = helper.getBlockEntity(lecternPos);
        ServerPlayer player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "crafter"));
        player.getInventory().add(new ItemStack(Items.OAK_PLANKS, 2));
        CraftingTerminalMenu menu = openMenu(lectern, player);
        moveToGrid(lectern, menu, player, Items.OAK_PLANKS, 1, 4);
        helper.assertTrue(menu.stillValid(player) && menu.getSlot(0).getItem().is(Items.STICK), "Expected an open menu with sticks waiting in the result slot");
        helper.destroyBlock(lecternPos);
        helper.assertFalse(menu.stillValid(player), "Expected breaking the lectern to invalidate its menu before the waiting result can be taken");
        helper.succeed();
    }

    @GameTest(template = "empty10")
    public static void menuRejectsChangesWhileLecternChunkIsNotSaving(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ChunkPos chunk = new ChunkPos(helper.absolutePos(lecternPos).offset(4096, 0, 0));
        BlockPos pos = chunk.getMiddleBlockPosition(helper.absolutePos(lecternPos).getY());
        level.setBlockAndUpdate(pos, BlockRegistry.CRAFTING_LECTERN.get().defaultBlockState());
        level.getChunkSource().addRegionTicket(UNSAVED_CHUNK, chunk, -5, chunk);
        CraftingLecternTile lectern = (CraftingLecternTile) level.getBlockEntity(pos);
        ServerPlayer player = FakePlayerFactory.get(level, new GameProfile(UUID.randomUUID(), "remote"));
        player.getInventory().add(new ItemStack(Items.DIAMOND_BLOCK));
        CraftingTerminalMenu menu = openMenu(lectern, player);
        moveToGrid(lectern, menu, player, Items.DIAMOND_BLOCK, 0);
        helper.assertTrue(menu.stillValid(player), "Expected the menu to be valid while the lectern's chunk is fully loaded");
        helper.startSequence()
                .thenWaitUntil(() -> helper.assertFalse(level.isLoaded(pos), "Expected the lectern's chunk to stop being fully loaded"))
                .thenExecute(() -> {
                    level.getChunkSource().removeRegionTicket(UNSAVED_CHUNK, chunk, -5, chunk);
                    helper.assertFalse(lectern.isRemoved(), "Expected the lectern to stay in memory while its chunk is not saving");
                    helper.assertFalse(menu.stillValid(player), "Expected the menu to be invalid while the lectern's chunk is not saving");
                    new ClientTransferHandlerPacket(Collections.nCopies(9, List.<ItemStack>of())).onServerReceived(level.getServer(), player);
                    int left = lectern.getCraftingInv(player).getItem(0).getCount();
                    helper.assertTrue(left == 1, "Expected the grid to keep its diamond block, got: " + left);
                })
                .thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void lecternChainForgetsRemovedChest(GameTestHelper helper) {
        assertLecternChainFollows(helper, List.of(), () -> helper.setBlock(storagePos, Blocks.AIR), 0);
    }

    @GameTest(template = "empty10")
    public static void lecternChainForgetsRemovedChestAfterMainReloadsFirst(GameTestHelper helper) {
        assertLecternChainFollows(helper, List.of(lecternPos, chainedLecternPos), () -> helper.setBlock(storagePos, Blocks.AIR), 0);
    }

    @GameTest(template = "empty10")
    public static void lecternChainForgetsRemovedChestAfterChainedReloadsFirst(GameTestHelper helper) {
        assertLecternChainFollows(helper, List.of(chainedLecternPos, lecternPos), () -> helper.setBlock(storagePos, Blocks.AIR), 0);
    }

    @GameTest(template = "empty10")
    public static void lecternChainSeesAddedChest(GameTestHelper helper) {
        BlockPos secondChestPos = new BlockPos(1, 1, 3);
        helper.setBlock(secondChestPos, Blocks.CHEST);
        assertLecternChainFollows(helper, List.of(), () -> helper.<CraftingLecternTile>getBlockEntity(lecternPos).addHandlerPos(helper.absolutePos(secondChestPos)), 54);
    }

    @GameTest(template = "empty10")
    public static void lecternChainInsertsAndExtracts(GameTestHelper helper) {
        StorageNetwork network = createChestNetwork(helper);
        helper.setBlock(chainedLecternPos, BlockRegistry.CRAFTING_LECTERN.get());
        ServerLevel level = helper.getLevel();
        ChestBlockEntity chest = helper.getBlockEntity(storagePos);
        ItemStack cobblestone = new ItemStack(Items.COBBLESTONE, 16);
        startLinked(helper, network)
                .thenExecute(() -> {
                    helper.<CraftingLecternTile>getBlockEntity(chainedLecternPos).onFinishedConnectionFirst(helper.absolutePos(lecternPos), null, null, FakePlayerFactory.getMinecraft(level));
                    network.lectern().canCreateTasks = true;
                    IItemHandler chained = level.getCapability(Capabilities.ItemHandler.BLOCK, helper.absolutePos(chainedLecternPos), null);
                    helper.assertTrue(chained.insertItem(0, cobblestone.copy(), true).isEmpty(), "Expected the chained lectern to accept a simulated insert");
                    helper.assertTrue(chained.insertItem(0, cobblestone.copy(), false).isEmpty(), "Expected the chained lectern to accept the cobblestone");
                    helper.assertTrue(chest.countItem(Items.COBBLESTONE) == 16, "Expected the main lectern's chest to hold 16 cobblestone, got: " + chest.countItem(Items.COBBLESTONE));
                    ItemStack extracted = ItemStack.EMPTY;
                    for (int i = 0; i < chained.getSlots() && extracted.isEmpty(); i++) {
                        extracted = chained.extractItem(i, cobblestone.getMaxStackSize(), false);
                    }
                    helper.assertTrue(ItemStack.matches(extracted, cobblestone) && chest.isEmpty(), "Expected to extract all 16 cobblestone, got: " + extracted);
                    helper.assertTrue(network.lectern().transferTasks.stream().anyMatch(task -> ItemStack.matches(task.stack, cobblestone)), "Expected the bookwyrm task to keep the inserted cobblestone after it was extracted");
                })
                .thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void hopperKeepsItemNoLinkedInventoryAccepts(GameTestHelper helper) {
        StorageNetwork network = createChestNetwork(helper);
        helper.setBlock(lecternPos.east(), Blocks.REDSTONE_BLOCK);
        hangCobblestoneFrame(helper, storagePos);
        BlockPos hopperPos = lecternPos.above();
        helper.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN));
        HopperBlockEntity hopper = helper.getBlockEntity(hopperPos);
        ChestBlockEntity chest = helper.getBlockEntity(storagePos);
        startLinked(helper, network)
                .thenExecute(() -> {
                    hopper.setItem(0, new ItemStack(Items.DIAMOND));
                    hopper.setItem(1, new ItemStack(Items.COBBLESTONE));
                })
                .thenWaitUntil(() -> helper.assertTrue(chest.countItem(Items.COBBLESTONE) == 1, "Expected the hopper's cobblestone in the chest"))
                .thenIdle(10)
                .thenExecute(() -> {
                    int inHopper = hopper.countItem(Items.DIAMOND);
                    int inChest = chest.countItem(Items.DIAMOND);
                    helper.assertTrue(inHopper == 1 && inChest == 0, "Expected the hopper to keep the diamond no linked inventory accepts, hopper: " + inHopper + ", chest: " + inChest);
                })
                .thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void crafterDoesNotDestroyPlanksThatDontFit(GameTestHelper helper) {
        StorageNetwork network = createChestNetwork(helper);
        helper.setBlock(lecternPos.east(), Blocks.REDSTONE_BLOCK);
        hangCobblestoneFrame(helper, storagePos);
        BlockPos plankChestPos = new BlockPos(1, 1, 3);
        helper.setBlock(plankChestPos, Blocks.CHEST);
        ChestBlockEntity plankChest = helper.getBlockEntity(plankChestPos);
        plankChest.setItem(0, new ItemStack(Items.OAK_PLANKS, 62));
        for (int i = 1; i < plankChest.getContainerSize(); i++) {
            plankChest.setItem(i, new ItemStack(Items.DIRT, 64));
        }
        BlockPos crafterPos = lecternPos.above();
        helper.setBlock(crafterPos, Blocks.CRAFTER.defaultBlockState().setValue(BlockStateProperties.ORIENTATION, FrontAndTop.DOWN_NORTH));
        CrafterBlockEntity crafter = helper.getBlockEntity(crafterPos);
        crafter.setItem(0, new ItemStack(Items.OAK_LOG));
        startLinked(helper, network)
                .thenExecute(() -> {
                    network.lectern().addHandlerPos(helper.absolutePos(plankChestPos));
                    network.lectern().updateItems = true;
                })
                .thenIdle(1)
                .thenExecute(() -> helper.setBlock(crafterPos.above(), Blocks.REDSTONE_BLOCK))
                .thenWaitUntil(() -> helper.assertTrue(crafter.getItem(0).isEmpty(), "Expected the crafter to craft its oak log"))
                .thenExecute(() -> {
                    int inChest = plankChest.countItem(Items.OAK_PLANKS);
                    int onGround = helper.getEntities(EntityType.ITEM).stream().filter(item -> item.getItem().is(Items.OAK_PLANKS)).mapToInt(item -> item.getItem().getCount()).sum();
                    helper.assertTrue(inChest + onGround == 66, "Expected 66 planks between the chest and the ground, chest: " + inChest + ", ground: " + onGround);
                })
                .thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void insertsByFilterPriority(GameTestHelper helper) {
        StorageNetwork network = createRepositoryNetwork(helper);
        BlockPos unframedPos = storagePos.south();
        BlockPos framedPos = unframedPos.west();
        helper.setBlock(unframedPos, BlockRegistry.REPOSITORY.get());
        helper.setBlock(framedPos, BlockRegistry.REPOSITORY.get());
        overflowIntoSecondFramedInventory(helper, network.lectern(), startLinked(helper, network), unframedPos, framedPos);
    }

    @GameTest(template = "empty10")
    public static void chestInsertsByFilterPriority(GameTestHelper helper) {
        StorageNetwork network = createChestNetwork(helper);
        BlockPos unframedPos = new BlockPos(1, 1, 3);
        BlockPos framedPos = new BlockPos(1, 1, 5);
        helper.setBlock(unframedPos, Blocks.CHEST);
        helper.setBlock(framedPos, Blocks.CHEST);
        GameTestSequence linked = startLinked(helper, network)
                .thenExecute(() -> {
                    network.lectern().addHandlerPos(helper.absolutePos(unframedPos));
                    network.lectern().addHandlerPos(helper.absolutePos(framedPos));
                    network.lectern().updateItems = true;
                })
                .thenIdle(1);
        overflowIntoSecondFramedInventory(helper, network.lectern(), linked, unframedPos, framedPos);
    }

    @GameTest(template = "empty10")
    public static void stuckHopperDryRunCostStaysLinear(GameTestHelper helper) {
        StorageNetwork network = createChestNetwork(helper);
        helper.setBlock(lecternPos.east(), Blocks.REDSTONE_BLOCK);
        ChestBlockEntity chest = helper.getBlockEntity(storagePos);
        CountingItems items = new CountingItems(chest.getContainerSize());
        ObfuscationReflectionHelper.setPrivateValue(ChestBlockEntity.class, chest, items, "items");
        for (int i = 0; i < chest.getContainerSize(); i++) {
            chest.setItem(i, new ItemStack(Items.DIRT, 64));
        }
        BlockPos hopperPos = lecternPos.above();
        helper.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN));
        HopperBlockEntity hopper = helper.getBlockEntity(hopperPos);
        int ticks = 20;
        startLinked(helper, network)
                .thenExecute(() -> hopper.setItem(0, new ItemStack(Items.DIAMOND)))
                .thenIdle(1)
                .thenExecute(() -> items.reads = 0)
                .thenIdle(ticks)
                .thenExecute(() -> {
                    int readsPerTick = items.reads / ticks;
                    int slots = chest.getContainerSize();
                    helper.assertTrue(hopper.countItem(Items.DIAMOND) == 1, "Expected the diamond to stay stuck in the hopper");
                    helper.assertTrue(readsPerTick <= 8 * slots, "Expected a stuck hopper to read each chest slot at most 8 times per tick, got " + readsPerTick + " reads for " + slots + " slots");
                })
                .thenSucceed();
    }

    private static void withdrawFromStaleEmptySlot(GameTestHelper helper, StorageNetwork network) {
        CraftingLecternTile lectern = network.lectern();
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
        CraftingLecternTile lectern = network.lectern();
        ItemStack sword = new ItemStack(Items.IRON_SWORD);
        startLinked(helper, network)
                .thenExecute(() -> helper.assertTrue(lectern.pushStack(new StoredItemStack(sword.copy()), null, false) == null, "Expected sword to be inserted"))
                .thenWaitUntil(() -> helper.assertTrue(listedCount(lectern, sword) == 1, "Expected the lectern to list the sword"))
                .thenExecute(() -> {
                    StoredItemStack pulled = lectern.pullStack(new StoredItemStack(sword), 1, null);
                    helper.assertTrue(pulled != null && ItemStack.isSameItemSameComponents(pulled.getStack(), sword), "Expected to withdraw the sword");
                })
                .thenWaitUntil(() -> helper.assertTrue(listedCount(lectern, sword) == 0, "Expected the lectern to list no swords"))
                .thenSucceed();
    }

    private static void withdrawMatchingVariant(GameTestHelper helper, StorageNetwork network) {
        CraftingLecternTile lectern = network.lectern();
        ItemStack damaged = new ItemStack(Items.IRON_SWORD);
        damaged.setDamageValue(10);
        ItemStack fresh = new ItemStack(Items.IRON_SWORD);
        startLinked(helper, network)
                .thenExecute(() -> {
                    lectern.pushStack(new StoredItemStack(damaged.copy()), null, false);
                    lectern.pushStack(new StoredItemStack(fresh.copy()), null, false);
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
        CraftingLecternTile lectern = network.lectern();
        ItemStack cobblestone = new ItemStack(Items.COBBLESTONE, 16);
        startLinked(helper, network)
                .thenExecute(() -> {
                    helper.assertTrue(lectern.pushStack(new StoredItemStack(cobblestone.copy()), null, false) == null, "Expected the first stack to be inserted");
                    helper.assertTrue(lectern.pushStack(new StoredItemStack(cobblestone.copy()), null, false) == null, "Expected the second stack to be inserted");
                })
                .thenWaitUntil(() -> helper.assertTrue(listedCount(lectern, cobblestone) == 32, "Expected the lectern to list 32 cobblestone"))
                .thenExecute(() -> {
                    StoredItemStack pulled = lectern.pullStack(new StoredItemStack(cobblestone), cobblestone.getMaxStackSize(), null);
                    helper.assertTrue(pulled != null && pulled.getQuantity() == 32, "Expected to withdraw 32 cobblestone, got: " + (pulled == null ? 0 : pulled.getQuantity()));
                })
                .thenWaitUntil(() -> helper.assertTrue(listedCount(lectern, cobblestone) == 0, "Expected the lectern to list no cobblestone"))
                .thenSucceed();
    }

    private static void overflowIntoSecondFramedInventory(GameTestHelper helper, CraftingLecternTile lectern, GameTestSequence linked, BlockPos unframedPos, BlockPos framedPos) {
        hangCobblestoneFrame(helper, storagePos);
        hangCobblestoneFrame(helper, framedPos);
        Container full = helper.getBlockEntity(storagePos);
        for (int i = 0; i < full.getContainerSize(); i++) {
            full.setItem(i, new ItemStack(Items.COBBLESTONE, 64));
        }
        Container unframed = helper.getBlockEntity(unframedPos);
        Container framed = helper.getBlockEntity(framedPos);
        linked.thenExecute(() -> {
                    lectern.pushStack(new StoredItemStack(new ItemStack(Items.COBBLESTONE, 16)), null, false);
                    int inFramed = framed.countItem(Items.COBBLESTONE);
                    int inUnframed = unframed.countItem(Items.COBBLESTONE);
                    helper.assertTrue(inFramed == 16 && inUnframed == 0, "Expected the cobblestone in the framed inventory with room, not the unframed one, framed: " + inFramed + ", unframed: " + inUnframed);
                })
                .thenSucceed();
    }

    private static void assertLecternChainFollows(GameTestHelper helper, List<BlockPos> reloadOrder, Runnable change, int expectedSlots) {
        StorageNetwork network = createChestNetwork(helper);
        helper.setBlock(chainedLecternPos, BlockRegistry.CRAFTING_LECTERN.get());
        ServerLevel level = helper.getLevel();
        BlockCapabilityCache<IItemHandler, Direction> mainView = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, level, helper.absolutePos(lecternPos), null);
        BlockCapabilityCache<IItemHandler, Direction> chainedView = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, level, helper.absolutePos(chainedLecternPos), null);
        startLinked(helper, network)
                .thenExecute(() -> {
                    helper.<CraftingLecternTile>getBlockEntity(chainedLecternPos).onFinishedConnectionFirst(helper.absolutePos(lecternPos), null, null, FakePlayerFactory.getMinecraft(level));
                    for (BlockPos pos : reloadOrder) {
                        BlockEntity lectern = helper.getBlockEntity(pos);
                        level.setBlockEntity(BlockEntity.loadStatic(lectern.getBlockPos(), lectern.getBlockState(), lectern.saveWithFullMetadata(level.registryAccess()), level.registryAccess()));
                    }
                })
                .thenWaitUntil(() -> helper.assertTrue(mainView.getCapability().getSlots() == 27 && chainedView.getCapability().getSlots() == 27, "Expected both lecterns to expose the chest"))
                .thenExecute(change)
                .thenWaitUntil(() -> {
                    int mainSlots = mainView.getCapability().getSlots();
                    int chainedSlots = chainedView.getCapability().getSlots();
                    helper.assertTrue(mainSlots == expectedSlots && chainedSlots == expectedSlots, "Expected both lecterns to expose " + expectedSlots + " slots, main: " + mainSlots + ", chained: " + chainedSlots);
                })
                .thenSucceed();
    }

    private record StorageNetwork(CraftingLecternTile lectern, BlockPos linkedPos) {
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
                    network.lectern().addHandlerPos(helper.absolutePos(network.linkedPos()));
                    network.lectern().updateItems = true;
                })
                .thenIdle(1);
    }

    private static long listedCount(CraftingLecternTile lectern, ItemStack stack) {
        return lectern.getStacks(null).getOrDefault(new StoredItemStack(stack), 0L);
    }

    private static void hangCobblestoneFrame(GameTestHelper helper, BlockPos target) {
        ServerPlayer player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "builder"));
        BlockPos targetPos = helper.absolutePos(target);
        player.setShiftKeyDown(true);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.ITEM_FRAME));
        player.gameMode.useItemOn(player, helper.getLevel(), player.getMainHandItem(), InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(targetPos), Direction.NORTH, targetPos, false));
        ItemFrame frame = helper.getEntities(EntityType.ITEM_FRAME).stream().filter(entity -> entity.blockPosition().equals(targetPos.north())).findFirst().orElseThrow();
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.COBBLESTONE));
        player.interactOn(frame, InteractionHand.MAIN_HAND);
        helper.assertTrue(frame.getItem().is(Items.COBBLESTONE), "Expected the item frame on " + target + " to hold cobblestone");
    }

    private static CraftingTerminalMenu openMenu(CraftingLecternTile lectern, ServerPlayer player) {
        CraftingTerminalMenu menu = (CraftingTerminalMenu) lectern.createMenu(1, player.getInventory(), player);
        player.containerMenu = menu;
        return menu;
    }

    private static void moveToGrid(CraftingLecternTile lectern, CraftingTerminalMenu menu, ServerPlayer player, Item item, int... gridSlots) {
        for (int gridSlot : gridSlots) {
            Slot from = menu.slots.stream().filter(slot -> slot.container == player.getInventory() && slot.getItem().is(item)).findFirst().orElseThrow();
            menu.clicked(from.index, 0, ClickType.PICKUP, player);
            menu.clicked(menu.findSlot(lectern.getCraftingInv(player), gridSlot).getAsInt(), 1, ClickType.PICKUP, player);
            menu.clicked(from.index, 0, ClickType.PICKUP, player);
        }
    }

    private static int rocketCount(ServerPlayer player, boolean withExplosions) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            Fireworks fireworks = stack.get(DataComponents.FIREWORKS);
            if (fireworks != null && fireworks.explosions().isEmpty() != withExplosions) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static class CountingItems extends NonNullList<ItemStack> {
        private int reads;

        private CountingItems(int size) {
            super(new ArrayList<>(Collections.nCopies(size, ItemStack.EMPTY)), ItemStack.EMPTY);
        }

        @Override
        public ItemStack get(int index) {
            reads++;
            return super.get(index);
        }
    }
}
