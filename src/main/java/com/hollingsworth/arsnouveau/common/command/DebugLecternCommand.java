package com.hollingsworth.arsnouveau.common.command;

import com.hollingsworth.arsnouveau.common.block.tile.CraftingLecternTile;
import com.hollingsworth.arsnouveau.common.block.tile.RepositoryTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

import java.util.ArrayList;
import java.util.List;

public class DebugLecternCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ars-debug-lectern")
                .requires(sender -> sender.hasPermission(2) && sender.getPlayer() != null && sender.getPlayer().isCreative())
                .executes(context -> placeLectern(context.getSource().getPlayerOrException())));
    }

    private static int placeLectern(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        Direction facing = player.getDirection();
        Direction right = facing.getClockWise();
        BlockPos lecternPos = player.blockPosition().relative(facing, 2);

        List<RepositoryTile> repositories = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            BlockPos repoPos = lecternPos.relative(right, i);
            level.setBlockAndUpdate(repoPos, BlockRegistry.REPOSITORY.get().defaultBlockState());
            if (level.getBlockEntity(repoPos) instanceof RepositoryTile repositoryTile) {
                repositoryTile.name = Component.literal("test " + i);
                repositories.add(repositoryTile);
            }
        }
        level.setBlockAndUpdate(lecternPos, BlockRegistry.CRAFTING_LECTERN.get().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing.getOpposite()));

        int[] index = {0};
        BuiltInRegistries.POTION.holders().forEach(potion -> {
            RepositoryTile repository = repositories.get(index[0] % repositories.size());
            repository.setItem(index[0] / repositories.size(), PotionContents.createItemStack(Items.POTION, potion));
            index[0]++;
        });

        if (!(level.getBlockEntity(lecternPos) instanceof CraftingLecternTile lectern)) {
            return 0;
        }
        for (RepositoryTile repository : repositories) {
            lectern.addHandlerPos(repository.getBlockPos());
        }
        lectern.updateItems = true;
        return 1;
    }
}
