package com.hollingsworth.arsnouveau.gametest;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.container.StoredItemStack;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryTile;
import com.hollingsworth.arsnouveau.gametest.StorageLecternTests.StorageNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Collections;

import static com.hollingsworth.arsnouveau.gametest.StorageLecternTests.createChestNetwork;
import static com.hollingsworth.arsnouveau.gametest.StorageLecternTests.createDirectRepositoryNetwork;
import static com.hollingsworth.arsnouveau.gametest.StorageLecternTests.createRepositoryNetwork;
import static com.hollingsworth.arsnouveau.gametest.StorageLecternTests.fillWithDirt;
import static com.hollingsworth.arsnouveau.gametest.StorageLecternTests.lecternPos;
import static com.hollingsworth.arsnouveau.gametest.StorageLecternTests.startLinked;
import static com.hollingsworth.arsnouveau.gametest.StorageLecternTests.storagePos;

@GameTestHolder(ArsNouveau.MODID)
@PrefixGameTestTemplate(false)
public class StorageLecternPerformanceTests {
    @GameTest(template = "empty10")
    public static void stuckHopperDryRunCostStaysLinear(GameTestHelper helper) {
        keepStuckHopperDryRunsLinear(helper, createChestNetwork(helper), ChestBlockEntity.class);
    }

    @GameTest(template = "empty10")
    public static void catalogStuckHopperDryRunCostStaysLinear(GameTestHelper helper) {
        keepStuckHopperDryRunsLinear(helper, createRepositoryNetwork(helper), RepositoryTile.class);
    }

    @GameTest(template = "empty10")
    public static void directStuckHopperDryRunCostStaysLinear(GameTestHelper helper) {
        keepStuckHopperDryRunsLinear(helper, createDirectRepositoryNetwork(helper), RepositoryTile.class);
    }

    @GameTest(template = "empty10")
    public static void catalogInsertIntoFullStorageCostStaysLinear(GameTestHelper helper) {
        StorageNetwork network = createRepositoryNetwork(helper);
        CountingItems items = countReads(helper, RepositoryTile.class);
        fillWithDirt(helper);
        startLinked(helper, network)
                .thenExecute(() -> {
                    items.reads = 0;
                    StoredItemStack remainder = network.lectern().pushStack(new StoredItemStack(new ItemStack(Items.DIRT, 64)), null, false);
                    int slots = items.size();
                    helper.assertTrue(remainder != null && remainder.getQuantity() == 64, "Expected the full storage to refuse all 64 dirt, got " + (remainder == null ? 0 : remainder.getQuantity()) + " back");
                    helper.assertTrue(items.reads <= 8 * slots, "Expected an insert into a full catalog to read each storage slot at most 8 times, got " + items.reads + " reads for " + slots + " slots");
                })
                .thenSucceed();
    }

    @GameTest(template = "empty10")
    public static void sameHandlerDryRunForgetsRefusalAfterExtract(GameTestHelper helper) {
        StorageNetwork network = createChestNetwork(helper);
        CountingItems items = countReads(helper, ChestBlockEntity.class);
        fillWithDirt(helper);
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

    private static <T extends BlockEntity & Container> void keepStuckHopperDryRunsLinear(GameTestHelper helper, StorageNetwork network, Class<T> storageType) {
        CountingItems items = countReads(helper, storageType);
        fillWithDirt(helper);
        BlockPos hopperPos = lecternPos.above();
        helper.setBlock(hopperPos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN));
        HopperBlockEntity hopper = helper.getBlockEntity(hopperPos);
        int ticks = 20;
        startLinked(helper, network)
                .thenExecute(() -> hopper.setItem(0, new ItemStack(Items.DIRT)))
                .thenIdle(1)
                .thenExecute(() -> items.reads = 0)
                .thenIdle(ticks)
                .thenExecute(() -> {
                    int readsPerTick = items.reads / ticks;
                    int slots = items.size();
                    helper.assertTrue(hopper.countItem(Items.DIRT) == 1, "Expected the dirt to stay stuck in the hopper");
                    helper.assertTrue(readsPerTick <= 8 * slots, "Expected a stuck hopper to read each storage slot at most 8 times per tick, got " + readsPerTick + " reads for " + slots + " slots");
                })
                .thenSucceed();
    }

    private static <T extends BlockEntity & Container> CountingItems countReads(GameTestHelper helper, Class<T> storageType) {
        T storage = helper.getBlockEntity(storagePos);
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
            reads++;
            return super.get(index);
        }
    }
}
