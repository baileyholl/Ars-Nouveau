package com.hollingsworth.arsnouveau.gametest;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.container.StoredItemStack;
import com.hollingsworth.arsnouveau.common.block.tile.CraftingLecternTile;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryCatalogTile;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestSequence;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.hollingsworth.arsnouveau.gametest.StorageLecternTests.*;

@GameTestHolder(ArsNouveau.MODID)
@PrefixGameTestTemplate(false)
public class StorageLecternPerformanceTests {
    @GameTest(template = "empty10")
    public static void chestConstantInsertions(GameTestHelper helper) {
        StorageNetwork network = createChestNetwork(helper);
        List<CountingItems> itemLists = List.of(countReads(helper, storagePos, ChestBlockEntity.class));
        fillWithDirt(helper, storagePos);
        assertHopperReadsPerTick(helper, startLinked(helper, network), lecternPos.above(), itemLists, 83, Items.DIRT);
    }

    @GameTest(template = "empty10")
    public static void catalogConstantInsertions(GameTestHelper helper) {
        StorageNetwork network = createCatalogNetwork(helper);
        List<CountingItems> itemLists = List.of(countReads(helper, storagePos, RepositoryTile.class));
        fillWithDirt(helper, storagePos);
        assertHopperReadsPerTick(helper, startLinked(helper, network), lecternPos.above(), itemLists, 164, Items.DIRT);
    }

    @GameTest(template = "empty10")
    public static void repositoriesConstantInsertions(GameTestHelper helper) {
        StorageNetwork network = createDirectRepositoryNetwork(helper);
        List<CountingItems> itemLists = List.of(countReads(helper, storagePos, RepositoryTile.class));
        fillWithDirt(helper, storagePos);
        assertHopperReadsPerTick(helper, startLinked(helper, network), lecternPos.above(), itemLists, 164, Items.DIRT);
    }

    @GameTest(template = "empty10")
    public static void catalogMixedHopperConstantInsertions(GameTestHelper helper) {
        StorageNetwork network = createCatalogNetwork(helper);
        List<CountingItems> itemLists = List.of(countReads(helper, storagePos, RepositoryTile.class));
        fillWithDirt(helper, storagePos);
        assertHopperReadsPerTick(helper, startLinked(helper, network), lecternPos.above(), itemLists, 542, Items.DIRT, Items.STONE, Items.DIRT, Items.SAND, Items.GRAVEL);
    }

    @GameTest(template = "empty10")
    public static void chestMixedHopperConstantInsertions(GameTestHelper helper) {
        StorageNetwork network = createChestNetwork(helper);
        List<CountingItems> itemLists = List.of(countReads(helper, storagePos, ChestBlockEntity.class));
        fillWithDirt(helper, storagePos);
        assertHopperReadsPerTick(helper, startLinked(helper, network), lecternPos.above(), itemLists, 272, Items.DIRT, Items.STONE, Items.DIRT, Items.SAND, Items.GRAVEL);
    }

    @GameTest(template = "empty10")
    public static void threeRepositoryConstantInsertions(GameTestHelper helper) {
        StorageNetwork network = createCatalogNetwork(helper);
        BlockPos thirdStoragePos = storagePos.above();
        helper.setBlock(secondStoragePos, BlockRegistry.REPOSITORY.get());
        helper.setBlock(thirdStoragePos, BlockRegistry.REPOSITORY.get());
        List<CountingItems> itemLists = new ArrayList<>();
        for (BlockPos pos : List.of(storagePos, secondStoragePos, thirdStoragePos)) {
            itemLists.add(countReads(helper, pos, RepositoryTile.class));
            fillWithDirt(helper, pos);
        }
        GameTestSequence linked = startLinked(helper, network)
                .thenExecute(() -> {
                    int connected = helper.<RepositoryCatalogTile>getBlockEntity(catalogPos).connectedRepositories.size();
                    helper.assertTrue(connected == 3, "Expected the catalog to connect all three repositories, got " + connected);
                });
        assertHopperReadsPerTick(helper, linked, lecternPos.above(), itemLists, 488, Items.DIRT);
    }

    @GameTest(template = "empty10")
    public static void chainedHopperConstantInsertions(GameTestHelper helper) {
        StorageNetwork network = createChestNetwork(helper);
        helper.setBlock(chainedLecternPos, BlockRegistry.CRAFTING_LECTERN.get());
        List<CountingItems> itemLists = List.of(countReads(helper, storagePos, ChestBlockEntity.class));
        fillWithDirt(helper, storagePos);
        GameTestSequence linked = startLinked(helper, network)
                .thenExecute(() -> helper.<CraftingLecternTile>getBlockEntity(chainedLecternPos).onFinishedConnectionFirst(helper.absolutePos(lecternPos), null, null, FakePlayerFactory.getMinecraft(helper.getLevel())));
        assertHopperReadsPerTick(helper, linked, chainedLecternPos.above(), itemLists, 83, Items.DIRT);
    }

    @GameTest(template = "empty10")
    public static void hopperSkipsOccupiedSlotsWithoutSimulatedInsert(GameTestHelper helper) {
        StorageNetwork network = createChestNetwork(helper);
        helper.setBlock(lecternPos.east(), Blocks.REDSTONE_BLOCK);
        CountingItems items = countReads(helper, storagePos, ChestBlockEntity.class);
        Container chest = helper.getBlockEntity(storagePos);
        for (int i = 0; i < 20; i++) {
            chest.setItem(i, new ItemStack(Items.STONE, 64));
        }
        BlockPos hopperPos = lecternPos.above();
        helper.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN));
        HopperBlockEntity hopper = helper.getBlockEntity(hopperPos);
        int expectedReads = 144;
        startLinked(helper, network)
                .thenExecute(() -> {
                    items.reads = 0;
                    hopper.setItem(0, new ItemStack(Items.DIRT));
                })
                .thenWaitUntil(() -> helper.assertTrue(hopper.isEmpty(), "Expected the hopper to insert the dirt past the occupied slots"))
                .thenExecute(() -> {
                    int reads = items.reads;
                    int dirt = chest.countItem(Items.DIRT);
                    helper.assertTrue(dirt == 1, "Expected the dirt in the chest, got " + dirt);
                    helper.assertTrue(reads == expectedReads, "Expected one transfer past 20 occupied slots to read the chest " + expectedReads + " times, got " + reads);
                })
                .thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void insertIntoFullStorageWalksEverySlotTwice(GameTestHelper helper) {
        StorageNetwork network = createCatalogNetwork(helper);
        CountingItems items = countReads(helper, storagePos, RepositoryTile.class);
        fillWithDirt(helper, storagePos);
        int expectedReads = 216;
        startLinked(helper, network)
                .thenExecute(() -> {
                    items.reads = 0;
                    StoredItemStack remainder = network.lectern().pushStack(new StoredItemStack(new ItemStack(Items.DIRT, 64)), null, false);
                    helper.assertTrue(remainder != null && remainder.getQuantity() == 64, "Expected the full storage to refuse all 64 dirt, got " + (remainder == null ? 0 : remainder.getQuantity()) + " back");
                    helper.assertTrue(items.reads == expectedReads, "Expected an insert into a full catalog to read the storage " + expectedReads + " times, got " + items.reads);
                })
                .thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void insertIntoMixedStorageUsesCache(GameTestHelper helper) {
        assertMixedStorageInsertReads(helper, createCatalogNetwork(helper), RepositoryTile.class, 3, 56);
    }

    @GameTest(template = "empty10")
    public static void chestInsertIntoMixedStorageScansOnDryRun(GameTestHelper helper) {
        assertMixedStorageInsertReads(helper, createChestNetwork(helper), ChestBlockEntity.class, 56, 2);
    }

    @GameTest(template = "empty10")
    public static void chestInsertsUseLastAcceptingSlot(GameTestHelper helper) {
        StorageNetwork network = createChestNetwork(helper);
        CountingItems items = countReads(helper, storagePos, ChestBlockEntity.class);
        Container chest = helper.getBlockEntity(storagePos);
        startLinked(helper, network)
                .thenExecute(() -> {
                    for (int i = 0; i < 13; i++) {
                        network.lectern().pushStack(new StoredItemStack(new ItemStack(Items.STONE, 64)), null, false);
                    }
                    network.lectern().pushStack(new StoredItemStack(new ItemStack(Items.STONE, 32)), null, false);
                    helper.assertTrue(chest.getItem(13).getCount() == 32, "Expected the partial stack in slot 13, got " + chest.getItem(13));
                    IItemHandler handler = helper.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, helper.absolutePos(lecternPos), null);
                    ItemStack stone = new ItemStack(Items.STONE);
                    items.reads = 0;
                    helper.assertTrue(handler.insertItem(13, stone.copy(), true).isEmpty(), "Expected the dry run to fit the stone");
                    helper.assertTrue(items.reads == 3, "Expected the warm dry run to read the chest 3 times, got " + items.reads);
                    items.reads = 0;
                    helper.assertTrue(handler.insertItem(13, stone.copy(), false).isEmpty(), "Expected the stone to be inserted");
                    helper.assertTrue(items.reads == 2, "Expected the warm insert to read the chest 2 times, got " + items.reads);
                    helper.assertTrue(chest.getItem(13).getCount() == 33, "Expected 33 stone in slot 13, got " + chest.getItem(13).getCount());
                })
                .thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void realExtractBustsCache(GameTestHelper helper) {
        StorageNetwork network = createChestNetwork(helper);
        CountingItems items = countReads(helper, storagePos, ChestBlockEntity.class);
        fillWithDirt(helper, storagePos);
        startLinked(helper, network)
                .thenExecute(() -> {
                    IItemHandler handler = helper.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, helper.absolutePos(lecternPos), null);
                    ItemStack dirt = new ItemStack(Items.DIRT);
                    helper.assertTrue(handler.insertItem(0, dirt.copy(), true).getCount() == 1, "Expected the full storage to refuse the dirt");
                    items.reads = 0;
                    helper.assertTrue(handler.insertItem(1, dirt.copy(), true).getCount() == 1, "Expected the repeated dry run to refuse the dirt");
                    helper.assertTrue(items.reads == 0, "Expected the repeated dry run to reuse the first answer without reading the storage, got " + items.reads + " reads");
                    helper.assertTrue(handler.extractItem(0, 1, false).is(Items.DIRT), "Expected to take dirt out through the lectern");
                    helper.assertTrue(handler.insertItem(0, dirt.copy(), true).isEmpty(), "Expected the dry run after the extract to see the freed room");
                })
                .thenSucceed();
    }

    private static void assertHopperReadsPerTick(GameTestHelper helper, GameTestSequence linked, BlockPos hopperPos, List<CountingItems> itemLists, int expectedReadsPerTick, Item... hopperItems) {
        helper.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN));
        HopperBlockEntity hopper = helper.getBlockEntity(hopperPos);
        int ticks = 20;
        linked.thenExecute(() -> {
                    for (int i = 0; i < hopperItems.length; i++) {
                        hopper.setItem(i, new ItemStack(hopperItems[i]));
                    }
                })
                .thenIdle(1)
                .thenExecute(() -> itemLists.forEach(items -> items.reads = 0))
                .thenIdle(ticks)
                .thenExecute(() -> {
                    int reads = itemLists.stream().mapToInt(items -> items.reads).sum();
                    for (Item item : hopperItems) {
                        int placed = Collections.frequency(List.of(hopperItems), item);
                        int held = hopper.countItem(item);
                        helper.assertTrue(held == placed, "Expected " + placed + " " + item + " to stay stuck in the hopper, got " + held);
                    }
                    helper.assertTrue(reads == expectedReadsPerTick * ticks, "Expected a stuck hopper to read the storage " + expectedReadsPerTick + " times per tick, " + expectedReadsPerTick * ticks + " over " + ticks + " ticks, got " + reads);
                })
                .thenSucceed();
    }

    private static <T extends BlockEntity & Container> void assertMixedStorageInsertReads(GameTestHelper helper, StorageNetwork network, Class<T> storageType, int expectedDryRunReads, int expectedInsertReads) {
        CountingItems items = countReads(helper, storagePos, storageType);
        Container storage = fillWithDirt(helper, storagePos);
        storage.setItem(26, new ItemStack(Items.STONE, 32));
        startLinked(helper, network)
                .thenExecute(() -> {
                    IItemHandler handler = helper.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, helper.absolutePos(lecternPos), null);
                    ItemStack stone = new ItemStack(Items.STONE);
                    items.reads = 0;
                    helper.assertTrue(handler.insertItem(26, stone.copy(), true).isEmpty(), "Expected the dry run to fit the stone");
                    helper.assertTrue(items.reads == expectedDryRunReads, "Expected the dry run to read the storage " + expectedDryRunReads + " times, got " + items.reads);
                    items.reads = 0;
                    helper.assertTrue(handler.insertItem(26, stone.copy(), false).isEmpty(), "Expected the stone to be inserted");
                    int insertReads = items.reads;
                    int held = storage.countItem(Items.STONE);
                    helper.assertTrue(held == 33, "Expected the storage to hold 33 stone, got " + held);
                    helper.assertTrue(insertReads == expectedInsertReads, "Expected the insert to read the storage " + expectedInsertReads + " times, got " + insertReads);
                })
                .thenSucceed();
    }

    private static <T extends BlockEntity & Container> CountingItems countReads(GameTestHelper helper, BlockPos pos, Class<T> storageType) {
        T storage = helper.getBlockEntity(pos);
        CountingItems items = new CountingItems(storage.getContainerSize());
        ObfuscationReflectionHelper.setPrivateValue(storageType, storage, items, "items");
        return items;
    }

    private static class CountingItems extends NonNullList<ItemStack> {
        private int reads;

        private CountingItems(int size) {
            super(new ArrayList<>(Collections.nCopies(size, ItemStack.EMPTY)), ItemStack.EMPTY);
        }

        @Override
        public ItemStack get(int index) {
            // Prevents duplicate count from chunk saving
            if (StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(frames -> frames.noneMatch(frame -> frame.getDeclaringClass() == ChunkSerializer.class))) {
                reads++;
            }
            return super.get(index);
        }
    }
}
