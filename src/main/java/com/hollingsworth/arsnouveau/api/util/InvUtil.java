package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class InvUtil {
    public static List<FilterableItemHandler> adjacentInventories(Level level, BlockPos pos){
        List<FilterableItemHandler> inventories = new ArrayList<>();
        for (Direction d : Direction.values()) {
            BlockEntity adjacentInvTile = level.getBlockEntity(pos.relative(d));
            if (adjacentInvTile == null || adjacentInvTile.isRemoved())
                continue;

            IItemHandler handler = adjacentInvTile.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
            if(handler == null)
                continue;
            inventories.add(new FilterableItemHandler(handler, filtersOnTile(adjacentInvTile)));
        }
        return inventories;
    }

    public static FilterableItemHandler getFilteredHandler(@NotNull BlockEntity tile){
        return new FilterableItemHandler(tile.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null), filtersOnTile(tile));
    }

    public static List<Function<ItemStack, ItemScroll.SortPref>> filtersOnTile(@Nullable BlockEntity thisTile){
        if(thisTile == null || thisTile.isRemoved()){
            return new ArrayList<>();
        }
        Level level = thisTile.getLevel();
        BlockPos pos = thisTile.getBlockPos();
        List<Function<ItemStack, ItemScroll.SortPref>> filters = new ArrayList<>();
        IItemHandler inv = thisTile.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        if(inv == null)
            return filters;

        // Get all item frames attached to the tile
        for (ItemFrame i : level.getEntitiesOfClass(ItemFrame.class, new AABB(pos).inflate(1))) {
            // Check if these frames are attached to the tile
            BlockEntity tileOnFrame = level.getBlockEntity(i.blockPosition().relative(i.getDirection().getOpposite()));
            ItemStack stackInFrame = i.getItem();
            if (tileOnFrame == null || !tileOnFrame.equals(thisTile) || i.getItem().isEmpty() || stackInFrame.isEmpty()) {
                continue;
            }

            if (stackInFrame.getItem() instanceof ItemScroll scrollItem) {
                filters.add(stackToStore -> scrollItem.getSortPref(stackToStore, stackInFrame, inv));
            } else {
                filters.add(stackToStore -> stackToStore.getItem() == stackInFrame.getItem() ? ItemScroll.SortPref.HIGHEST : ItemScroll.SortPref.INVALID);
            }
        }

        return filters;
    }

    public static List<FilterableItemHandler> fromPlayer(Player player){
        List<FilterableItemHandler> list = new ArrayList<>();
        list.add(new FilterableItemHandler(new PlayerMainInvWrapper(player.inventory), new ArrayList<>()));
        return list;
    }
}
