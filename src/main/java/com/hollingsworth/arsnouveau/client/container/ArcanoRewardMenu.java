package com.hollingsworth.arsnouveau.client.container;

import com.hollingsworth.arsnouveau.common.block.DimBoundary;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanoRewardTile;
import com.hollingsworth.arsnouveau.common.world.saved_data.ArcanoDimData;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class ArcanoRewardMenu extends AbstractContainerMenu {
    public static final int CONTINUE_BUTTON = 0;
    public static final int LEAVE_BUTTON = 1;
    public static final List<ItemStack> DUMMY_LOOT = List.of(
            new ItemStack(ItemsRegistry.SOURCE_GEM.get(), 8),
            new ItemStack(Items.AMETHYST_SHARD, 4),
            new ItemStack(Items.EXPERIENCE_BOTTLE, 3)
    );

    private final ArcanoRewardTile tile;
    private final BlockPos tilePos;

    public ArcanoRewardMenu(int id, Inventory inventory) {
        this(id, inventory, null);
    }

    public ArcanoRewardMenu(int id, Inventory inventory, ArcanoRewardTile tile) {
        super(MenuRegistry.ARCANO_REWARD.get(), id);
        this.tile = tile;
        this.tilePos = tile == null ? BlockPos.ZERO : tile.getBlockPos();
    }

    @Override
    public boolean stillValid(Player player) {
        return tile == null || tile.canInteractWith(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == CONTINUE_BUTTON && player instanceof ServerPlayer && player.level() instanceof ServerLevel serverLevel) {
            ArcanoDimData dimData = ArcanoDimData.from(serverLevel);
            if (dimData.isRewardBlockAt(serverLevel, tilePos)) {
                dimData.continueToNextStage(serverLevel);
                player.closeContainer();
            }
            return true;
        }
        if (id == LEAVE_BUTTON && player instanceof ServerPlayer) {
            player.closeContainer();
            DimBoundary.playerAttemptedBreak(player.level(), player);
            return true;
        }
        return false;
    }
}
