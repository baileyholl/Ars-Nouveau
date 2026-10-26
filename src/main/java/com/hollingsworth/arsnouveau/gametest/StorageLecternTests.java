package com.hollingsworth.arsnouveau.gametest;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import com.hollingsworth.arsnouveau.client.container.StoredItemStack;
import com.hollingsworth.arsnouveau.common.block.tile.CraftingLecternTile;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryTile;
import com.hollingsworth.arsnouveau.common.network.ClientTransferHandlerPacket;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
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
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@GameTestHolder(ArsNouveau.MODID)
@PrefixGameTestTemplate(false)
public class StorageLecternTests {
    static BlockPos storagePos = new BlockPos(1, 1, 1);
    static final BlockPos secondStoragePos = new BlockPos(0, 1, 1);
    static final BlockPos catalogPos = new BlockPos(2, 1, 1);
    static final BlockPos lecternPos = new BlockPos(3, 1, 1);
    static final BlockPos chainedLecternPos = new BlockPos(5, 1, 1);
    private static final TicketType<ChunkPos> UNSAVED_CHUNK = TicketType.create("ars_nouveau_unsaved_chunk_test", Comparator.comparingLong(ChunkPos::toLong));

    @GameTest(template = "empty10")
    public static void extractsNonStackableItems(GameTestHelper helper) {
        depositAndWithdrawSword(helper, createCatalogNetwork(helper));
    }

    @GameTest(template = "empty10")
    public static void chestExtractsNonStackableItems(GameTestHelper helper) {
        depositAndWithdrawSword(helper, createChestNetwork(helper));
    }

    @GameTest(template = "empty10")
    public static void extractsBasedOnDataComponents(GameTestHelper helper) {
        withdrawMatchingVariant(helper, createCatalogNetwork(helper));
    }

    @GameTest(template = "empty10")
    public static void chestExtractsBasedOnDataComponents(GameTestHelper helper) {
        withdrawMatchingVariant(helper, createChestNetwork(helper));
    }

    @GameTest(template = "empty10")
    public static void extractsMergedStacks(GameTestHelper helper) {
        mergeAndWithdrawStack(helper, createCatalogNetwork(helper));
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
        withdrawFromStaleEmptySlot(helper, createCatalogNetwork(helper));
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
    public static void lecternUnloadDisablesMenus(GameTestHelper helper) {
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
    public static void filtersDoNotVoidItems(GameTestHelper helper) {
        StorageNetwork network = createChestNetwork(helper);
        helper.setBlock(lecternPos.east(), Blocks.REDSTONE_BLOCK);
        hangFrame(helper, storagePos, Direction.NORTH, new ItemStack(Items.COBBLESTONE));
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

    // Crafters behave differently than hoppers as they insert everything at once so this is a nice coverage
    @GameTest(template = "empty10")
    public static void multiInsertsDoNotVoidItems(GameTestHelper helper) {
        StorageNetwork network = createChestNetwork(helper);
        hangFrame(helper, storagePos, Direction.NORTH, new ItemStack(Items.COBBLESTONE));
        BlockPos plankChestPos = new BlockPos(1, 1, 3);
        helper.setBlock(plankChestPos, Blocks.CHEST);
        GameTestSequence linked = startLinked(helper, network)
                .thenExecute(() -> {
                    network.lectern().addHandlerPos(helper.absolutePos(plankChestPos));
                    network.lectern().updateItems = true;
                })
                .thenIdle(1);
        assertCrafterItemsNotVoided(helper, linked, helper.getBlockEntity(plankChestPos));
    }

    @GameTest(template = "empty10")
    public static void insertsByFilterPriority(GameTestHelper helper) {
        StorageNetwork network = createCatalogNetwork(helper);
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
    public static void insertsIntoFrameBeforeExistingStack(GameTestHelper helper) {
        StorageNetwork network = createCatalogNetwork(helper);
        helper.setBlock(secondStoragePos, BlockRegistry.REPOSITORY.get());
        preferFrameOverExistingStack(helper, network.lectern(), startLinked(helper, network));
    }

    @GameTest(template = "empty10")
    public static void directInsertsIntoFrameBeforeExistingStack(GameTestHelper helper) {
        StorageNetwork network = createDirectRepositoryNetwork(helper);
        preferFrameOverExistingStack(helper, network.lectern(), startLinkedWithSecondRepository(helper, network));
    }

    @GameTest(template = "empty10")
    public static void insertsRespectDenyScrolledRepository(GameTestHelper helper) {
        StorageNetwork network = createCatalogNetwork(helper);
        helper.setBlock(secondStoragePos, BlockRegistry.REPOSITORY.get());
        assertDenyScrollIsRespected(helper, network.lectern(), startLinked(helper, network));
    }

    @GameTest(template = "empty10")
    public static void directInsertsRespectDenyScrolledRepository(GameTestHelper helper) {
        StorageNetwork network = createDirectRepositoryNetwork(helper);
        assertDenyScrollIsRespected(helper, network.lectern(), startLinkedWithSecondRepository(helper, network));
    }

    @GameTest(template = "empty10")
    public static void repositoryCacheSetsNewItem(GameTestHelper helper) {
        StorageNetwork network = createCatalogNetwork(helper);
        helper.setBlock(secondStoragePos, BlockRegistry.REPOSITORY.get());
        Container first = helper.getBlockEntity(storagePos);
        RepositoryTile emptied = helper.getBlockEntity(secondStoragePos);
        emptied.setItem(0, new ItemStack(Items.REDSTONE, 16));
        ServerPlayer player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "taker"));
        startLinked(helper, network)
                .thenExecute(() -> {
                    AbstractContainerMenu menu = emptied.createMenu(1, player.getInventory(), player);
                    player.containerMenu = menu;
                    menu.clicked(0, 0, ClickType.QUICK_MOVE, player);
                    network.lectern().pushStack(new StoredItemStack(new ItemStack(Items.REDSTONE, 16)), null, false);
                    int inFirst = first.countItem(Items.REDSTONE);
                    int inEmptied = emptied.countItem(Items.REDSTONE);
                    helper.assertTrue(inFirst == 16 && inEmptied == 0, "Expected the redstone in the first repository, not the one it was shift-clicked out of, first: " + inFirst + ", emptied: " + inEmptied);
                })
                .thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void hopperInsertsAfterLecternExtract(GameTestHelper helper) {
        StorageNetwork network = createCatalogNetwork(helper);
        Container storage = fillWithDirt(helper, storagePos);
        BlockPos abovePos = lecternPos.above();
        BlockPos belowPos = lecternPos.below();
        helper.setBlock(abovePos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN));
        HopperBlockEntity above = helper.getBlockEntity(abovePos);
        int expectedDirt = storage.getContainerSize() * 64 + 1;
        startLinked(helper, network)
                .thenExecute(() -> above.setItem(0, new ItemStack(Items.DIRT)))
                .thenIdle(10)
                .thenExecute(() -> {
                    helper.assertTrue(above.countItem(Items.DIRT) == 1, "Expected the dirt to stay stuck in the hopper while the storage is full");
                    helper.setBlock(belowPos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN));
                })
                .thenWaitUntil(() -> helper.assertTrue(above.isEmpty(), "Expected the stuck hopper to insert once the hopper below took dirt out through the lectern"))
                .thenExecute(() -> {
                    HopperBlockEntity below = helper.getBlockEntity(belowPos);
                    int dirt = storage.countItem(Items.DIRT) + below.countItem(Items.DIRT);
                    helper.assertTrue(dirt == expectedDirt, "Expected " + expectedDirt + " dirt between the storage and the hopper below, got " + dirt);
                })
                .thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void chainedHopperInsertsAfterMainLecternExtract(GameTestHelper helper) {
        StorageNetwork network = createChestNetwork(helper);
        helper.setBlock(chainedLecternPos, BlockRegistry.CRAFTING_LECTERN.get());
        Container storage = fillWithDirt(helper, storagePos);
        BlockPos abovePos = chainedLecternPos.above();
        BlockPos belowPos = lecternPos.below();
        helper.setBlock(abovePos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN));
        HopperBlockEntity above = helper.getBlockEntity(abovePos);
        int expectedDirt = storage.getContainerSize() * 64 + 1;
        startLinked(helper, network)
                .thenExecute(() -> {
                    helper.<CraftingLecternTile>getBlockEntity(chainedLecternPos).onFinishedConnectionFirst(helper.absolutePos(lecternPos), null, null, FakePlayerFactory.getMinecraft(helper.getLevel()));
                    above.setItem(0, new ItemStack(Items.DIRT));
                })
                .thenIdle(10)
                .thenExecute(() -> {
                    helper.assertTrue(above.countItem(Items.DIRT) == 1, "Expected the dirt to stay stuck in the hopper above the chained lectern while the storage is full");
                    helper.setBlock(belowPos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN));
                })
                .thenWaitUntil(() -> helper.assertTrue(above.isEmpty(), "Expected the stuck hopper above the chained lectern to insert once the hopper below the main lectern took dirt out"))
                .thenExecute(() -> {
                    HopperBlockEntity below = helper.getBlockEntity(belowPos);
                    int dirt = storage.countItem(Items.DIRT) + below.countItem(Items.DIRT);
                    helper.assertTrue(dirt == expectedDirt, "Expected " + expectedDirt + " dirt between the storage and the hopper below, got " + dirt);
                })
                .thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void simulatedInsertReturnsCorrectRemainder(GameTestHelper helper) {
        StorageNetwork network = createChestNetwork(helper);
        Container chest = fillWithDirt(helper, storagePos);
        chest.setItem(0, new ItemStack(Items.DIRT, 44));
        startLinked(helper, network)
                .thenExecute(() -> {
                    IItemHandler handler = helper.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, helper.absolutePos(lecternPos), null);
                    ItemStack dirt = new ItemStack(Items.DIRT, 64);
                    int refused = handler.insertItem(0, dirt.copy(), true).getCount();
                    helper.assertTrue(refused == 44, "Expected the dry run to fit 20 of the 64 dirt and return 44, got " + refused);
                    chest.setItem(0, new ItemStack(Items.DIRT, 64));
                    int refusedAfterFill = handler.insertItem(0, dirt.copy(), true).getCount();
                    helper.assertTrue(refusedAfterFill == 64, "Expected the repeated dry run to see the slot filled since and return all 64 dirt, got " + refusedAfterFill);
                })
                .thenSucceed();
    }

    private static void assertCrafterItemsNotVoided(GameTestHelper helper, GameTestSequence linked, Container planks) {
        helper.setBlock(lecternPos.east(), Blocks.REDSTONE_BLOCK);
        planks.setItem(0, new ItemStack(Items.OAK_PLANKS, 62));
        for (int i = 1; i < planks.getContainerSize(); i++) {
            planks.setItem(i, new ItemStack(Items.DIRT, 64));
        }
        BlockPos crafterPos = lecternPos.above();
        helper.setBlock(crafterPos, Blocks.CRAFTER.defaultBlockState().setValue(BlockStateProperties.ORIENTATION, FrontAndTop.DOWN_NORTH));
        CrafterBlockEntity crafter = helper.getBlockEntity(crafterPos);
        crafter.setItem(0, new ItemStack(Items.OAK_LOG));
        linked.thenExecute(() -> helper.setBlock(crafterPos.above(), Blocks.REDSTONE_BLOCK))
                .thenWaitUntil(() -> helper.assertTrue(crafter.getItem(0).isEmpty(), "Expected the crafter to craft its oak log"))
                .thenExecute(() -> {
                    int inStorage = planks.countItem(Items.OAK_PLANKS);
                    int onGround = helper.getEntities(EntityType.ITEM).stream().filter(item -> item.getItem().is(Items.OAK_PLANKS)).mapToInt(item -> item.getItem().getCount()).sum();
                    helper.assertTrue(inStorage + onGround == 66, "Expected 66 planks between the storage and the ground, storage: " + inStorage + ", ground: " + onGround);
                })
                .thenSucceed();
    }

    static Container fillWithDirt(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(lecternPos.east(), Blocks.REDSTONE_BLOCK);
        Container storage = helper.getBlockEntity(pos);
        for (int i = 0; i < storage.getContainerSize(); i++) {
            storage.setItem(i, new ItemStack(Items.DIRT, 64));
        }
        return storage;
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
        hangFrame(helper, storagePos, Direction.NORTH, new ItemStack(Items.COBBLESTONE));
        hangFrame(helper, framedPos, Direction.NORTH, new ItemStack(Items.COBBLESTONE));
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

    private static void preferFrameOverExistingStack(GameTestHelper helper, CraftingLecternTile lectern, GameTestSequence linked) {
        hangFrame(helper, secondStoragePos, Direction.NORTH, new ItemStack(Items.COBBLESTONE));
        Container unframed = helper.getBlockEntity(storagePos);
        Container framed = helper.getBlockEntity(secondStoragePos);
        unframed.setItem(0, new ItemStack(Items.COBBLESTONE, 20));
        linked.thenExecute(() -> {
                    lectern.pushStack(new StoredItemStack(new ItemStack(Items.COBBLESTONE, 16)), null, false);
                    int inFramed = framed.countItem(Items.COBBLESTONE);
                    int inUnframed = unframed.countItem(Items.COBBLESTONE);
                    helper.assertTrue(inFramed == 16 && inUnframed == 20, "Expected the cobblestone in the empty framed repository, not the unframed one already holding some, framed: " + inFramed + ", unframed: " + inUnframed);
                })
                .thenSucceed();
    }

    private static void assertDenyScrollIsRespected(GameTestHelper helper, CraftingLecternTile lectern, GameTestSequence linked) {
        RepositoryTile denying = helper.getBlockEntity(storagePos);
        Container open = helper.getBlockEntity(secondStoragePos);
        denying.setItem(0, new ItemStack(Items.REDSTONE, 16));
        ServerPlayer player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "scribe"));
        hangFrame(helper, storagePos, Direction.NORTH, denyRedstoneScroll(helper, player));
        linked.thenExecute(() -> {
                    AbstractContainerMenu menu = denying.createMenu(1, player.getInventory(), player);
                    player.containerMenu = menu;
                    menu.clicked(0, 0, ClickType.QUICK_MOVE, player);
                    lectern.pushStack(new StoredItemStack(new ItemStack(Items.REDSTONE, 16)), null, false);
                    int inDenying = denying.countItem(Items.REDSTONE);
                    int inOpen = open.countItem(Items.REDSTONE);
                    helper.assertTrue(inDenying == 0 && inOpen == 16, "Expected the redstone to skip the repository denying it after its redstone was taken out, denying: " + inDenying + ", open: " + inOpen);
                    denying.setItem(0, new ItemStack(Items.REDSTONE, 16));
                    lectern.pushStack(new StoredItemStack(new ItemStack(Items.REDSTONE, 16)), null, false);
                    inDenying = denying.countItem(Items.REDSTONE);
                    inOpen = open.countItem(Items.REDSTONE);
                    helper.assertTrue(inDenying == 16 && inOpen == 32, "Expected the redstone to skip the repository denying it while it holds redstone, denying: " + inDenying + ", open: " + inOpen);
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

    record StorageNetwork(CraftingLecternTile lectern, BlockPos linkedPos) {
    }

    static StorageNetwork createCatalogNetwork(GameTestHelper helper) {
        helper.setBlock(storagePos, BlockRegistry.REPOSITORY.get());
        helper.setBlock(catalogPos, BlockRegistry.REPOSITORY_CONTROLLER.get());
        helper.setBlock(lecternPos, BlockRegistry.CRAFTING_LECTERN.get());
        // The catalog touches the lectern, which pulls from unlinked neighbours every 20 ticks unless powered.
        helper.setBlock(lecternPos.east(), Blocks.REDSTONE_BLOCK);
        return new StorageNetwork(helper.getBlockEntity(lecternPos), catalogPos);
    }

    static StorageNetwork createDirectRepositoryNetwork(GameTestHelper helper) {
        helper.setBlock(storagePos, BlockRegistry.REPOSITORY.get());
        helper.setBlock(lecternPos, BlockRegistry.CRAFTING_LECTERN.get());
        return new StorageNetwork(helper.getBlockEntity(lecternPos), storagePos);
    }

    static StorageNetwork createChestNetwork(GameTestHelper helper) {
        helper.setBlock(storagePos, Blocks.CHEST);
        helper.setBlock(lecternPos, BlockRegistry.CRAFTING_LECTERN.get());
        return new StorageNetwork(helper.getBlockEntity(lecternPos), storagePos);
    }

    static GameTestSequence startLinked(GameTestHelper helper, StorageNetwork network) {
        return helper.startSequence()
                .thenIdle(1)
                .thenExecute(() -> {
                    network.lectern().addHandlerPos(helper.absolutePos(network.linkedPos()));
                    network.lectern().updateItems = true;
                })
                .thenIdle(1);
    }

    private static GameTestSequence startLinkedWithSecondRepository(GameTestHelper helper, StorageNetwork network) {
        helper.setBlock(secondStoragePos, BlockRegistry.REPOSITORY.get());
        return startLinked(helper, network)
                .thenExecute(() -> {
                    network.lectern().addHandlerPos(helper.absolutePos(secondStoragePos));
                    network.lectern().updateItems = true;
                })
                .thenIdle(1);
    }

    private static long listedCount(CraftingLecternTile lectern, ItemStack stack) {
        return lectern.getStacks(null).getOrDefault(new StoredItemStack(stack), 0L);
    }

    private static ItemFrame hangFrame(GameTestHelper helper, BlockPos target, Direction direction, ItemStack held) {
        ServerPlayer player = FakePlayerFactory.get(helper.getLevel(), new GameProfile(UUID.randomUUID(), "builder"));
        BlockPos targetPos = helper.absolutePos(target);
        player.setShiftKeyDown(true);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.ITEM_FRAME));
        player.gameMode.useItemOn(player, helper.getLevel(), player.getMainHandItem(), InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(targetPos), direction, targetPos, false));
        ItemFrame frame = helper.getEntities(EntityType.ITEM_FRAME).stream().filter(entity -> entity.blockPosition().equals(targetPos.relative(direction))).findFirst().orElseThrow();
        player.setItemInHand(InteractionHand.MAIN_HAND, held.copy());
        player.interactOn(frame, InteractionHand.MAIN_HAND);
        helper.assertTrue(ItemStack.isSameItemSameComponents(frame.getItem(), held), "Expected the item frame on " + target + " to hold " + held);
        return frame;
    }

    private static ItemStack denyRedstoneScroll(GameTestHelper helper, ServerPlayer player) {
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ItemsRegistry.DENY_ITEM_SCROLL.get()));
        player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.REDSTONE));
        player.gameMode.useItem(player, helper.getLevel(), player.getMainHandItem(), InteractionHand.MAIN_HAND);
        return player.getMainHandItem();
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
}
