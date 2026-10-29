package com.hollingsworth.arsnouveau.common.command;

import com.hollingsworth.arsnouveau.common.block.tile.CraftingLecternTile;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryCatalogTile;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DebugLecternCommand {
    private static final int WALL_WIDTH = 10;
    private static final int WALL_HEIGHT = 10;
    private static final int WALL_DISTANCE = 3;
    private static final int NAMED_REPOSITORIES = 15;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("ars-lectern-scenario")
                .requires(sender -> sender.hasPermission(2) && sender.getPlayer() != null && sender.getPlayer().isCreative());
        for (Scenario scenario : Scenario.values()) {
            command.then(Commands.literal(scenario.name().toLowerCase(Locale.ROOT))
                    .then(Commands.literal("small").executes(context -> spawn(context.getSource().getPlayerOrException(), scenario, scenario.small)))
                    .then(Commands.literal("large").executes(context -> spawn(context.getSource().getPlayerOrException(), scenario, scenario.large))));
        }
        command.then(Commands.literal("named-inventories").executes(context -> spawnNamedInventories(context.getSource().getPlayerOrException())));
        dispatcher.register(command);
    }

    private static int spawn(ServerPlayer player, Scenario scenario, int count) {
        ServerLevel level = player.serverLevel();
        Direction facing = player.getDirection();
        Direction right = facing.getClockWise();
        BlockPos lecternPos = player.blockPosition().relative(facing, WALL_DISTANCE).above(2);
        if (!level.isInWorldBounds(lecternPos.above(WALL_HEIGHT - 1)) || !level.isInWorldBounds(lecternPos.below(2))) {
            return 0;
        }
        level.removeBlock(lecternPos, false);
        level.removeBlock(lecternPos.relative(right), false);

        List<Item> fillers = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item != Items.AIR && item != Items.DIRT && BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("minecraft") && item.getDefaultMaxStackSize() == 64) {
                fillers.add(item);
            }
        }

        Block storageBlock = scenario == Scenario.CHESTS ? Blocks.CHEST : BlockRegistry.REPOSITORY.get();
        int filledSlots = scenario == Scenario.CHESTS ? 20 : 48;
        List<BlockPos> storage = wall(lecternPos, facing, right, count);
        for (int i = 0; i < storage.size(); i++) {
            level.setBlockAndUpdate(storage.get(i), storageBlock.defaultBlockState());
            Container container = (Container) level.getBlockEntity(storage.get(i));
            for (int slot = 0; slot < filledSlots; slot++) {
                container.setItem(slot, new ItemStack(fillers.get((i * 64 + slot) % fillers.size()), 64));
            }
        }
        Container last = (Container) level.getBlockEntity(storage.getLast());
        last.setItem(0, new ItemStack(Items.DIRT, 64));
        last.setItem(1, new ItemStack(Items.DIRT, 64));

        level.setBlockAndUpdate(lecternPos, BlockRegistry.CRAFTING_LECTERN.get().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing.getOpposite()));
        level.setBlockAndUpdate(lecternPos.relative(right), Blocks.REDSTONE_BLOCK.defaultBlockState());
        placeFeedingHopper(level, lecternPos.above(), Direction.DOWN);
        placeFeedingHopper(level, lecternPos.relative(right.getOpposite()), right);
        placeFeedingHopper(level, lecternPos.relative(facing.getOpposite()), facing);
        placeFeedingHopper(level, lecternPos.relative(facing), facing.getOpposite());
        level.setBlockAndUpdate(lecternPos.below(), Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN));
        level.setBlockAndUpdate(lecternPos.below(2), Blocks.CHEST.defaultBlockState());

        if (!(level.getBlockEntity(lecternPos) instanceof CraftingLecternTile lectern)) {
            return 0;
        }
        if (scenario == Scenario.CATALOG) {
            BlockPos catalogPos = storage.getFirst().relative(right.getOpposite());
            level.setBlockAndUpdate(catalogPos, BlockRegistry.REPOSITORY_CONTROLLER.get().defaultBlockState());
            if (level.getBlockEntity(catalogPos) instanceof RepositoryCatalogTile catalog) {
                catalog.invalidateNetwork();
            }
            lectern.addHandlerPos(catalogPos);
        } else {
            for (BlockPos pos : storage) {
                lectern.addHandlerPos(pos);
            }
        }
        lectern.updateItems = true;
        return 1;
    }

    private static int spawnNamedInventories(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        Direction facing = player.getDirection();
        Direction right = facing.getClockWise();
        BlockPos lecternPos = player.blockPosition().relative(facing, WALL_DISTANCE);
        level.removeBlock(lecternPos, false);

        List<RepositoryTile> repositories = new ArrayList<>();
        for (int i = 1; i <= NAMED_REPOSITORIES; i++) {
            BlockPos pos = lecternPos.relative(right, i);
            level.setBlockAndUpdate(pos, BlockRegistry.REPOSITORY.get().defaultBlockState());
            if (level.getBlockEntity(pos) instanceof RepositoryTile repository) {
                repository.name = Component.literal("test " + i);
                repositories.add(repository);
            }
        }
        int[] index = {0};
        BuiltInRegistries.POTION.holders().forEach(potion -> {
            RepositoryTile repository = repositories.get(index[0] % repositories.size());
            repository.setItem(index[0] / repositories.size(), PotionContents.createItemStack(Items.POTION, potion));
            index[0]++;
        });

        level.setBlockAndUpdate(lecternPos, BlockRegistry.CRAFTING_LECTERN.get().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing.getOpposite()));
        if (!(level.getBlockEntity(lecternPos) instanceof CraftingLecternTile lectern)) {
            return 0;
        }
        for (RepositoryTile repository : repositories) {
            lectern.addHandlerPos(repository.getBlockPos());
        }
        lectern.updateItems = true;
        return 1;
    }

    private static void placeFeedingHopper(ServerLevel level, BlockPos pos, Direction facing) {
        level.setBlockAndUpdate(pos, Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, facing));
        level.setBlockAndUpdate(pos.above(), Blocks.CHEST.defaultBlockState());
        Container source = (Container) level.getBlockEntity(pos.above());
        for (int slot = 0; slot < source.getContainerSize(); slot++) {
            source.setItem(slot, new ItemStack(Items.DIRT, 64));
        }
    }

    private static List<BlockPos> wall(BlockPos lecternPos, Direction facing, Direction right, int count) {
        List<BlockPos> positions = new ArrayList<>();
        int width = Math.min(count, WALL_WIDTH);
        int start = -(width - 1) / 2;
        for (int i = 0; i < count; i++) {
            int column = i % width;
            int row = i / width % WALL_HEIGHT;
            int layer = i / (width * WALL_HEIGHT);
            positions.add(lecternPos.relative(facing, WALL_DISTANCE + layer).relative(right, start + column).above(row));
        }
        return positions;
    }

    private enum Scenario {
        CHESTS(1, 200),
        REPOSITORIES(1, 100),
        CATALOG(1, 100);

        private final int small;
        private final int large;

        Scenario(int small, int large) {
            this.small = small;
            this.large = large;
        }
    }
}
