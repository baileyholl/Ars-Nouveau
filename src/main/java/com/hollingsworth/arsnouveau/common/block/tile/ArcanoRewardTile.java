package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.client.container.ArcanoRewardMenu;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ArcanoRewardTile extends ModdedTile implements MenuProvider, ITickable {

    public ArcanoRewardTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ARCANO_REWARD_TILE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("ars_nouveau.arcano_reward.title");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ArcanoRewardMenu(id, inventory, this);
    }

    public boolean canInteractWith(Player player) {
        return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
    }
}
