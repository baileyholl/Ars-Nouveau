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
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class InvUtil {
    public static List<FilterableItemHandler> adjacentInventories(Level level, BlockPos pos){
        List<FilterableItemHandler> inventories = new ArrayList<>();
        for (Direction d : Direction.values()) {
            BlockEntity adjacentInvTile = level.getBlockEntity(pos.relative(d));
            if (adjacentInvTile == null)
                continue;
            adjacentInvTile.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv ->{
                List<Function<ItemStack, ItemScroll.SortPref>> filters = new ArrayList<>();
                // Get all item frames attached to the tile
                for (ItemFrame i : adjacentInvTile.getLevel().getEntitiesOfClass(ItemFrame.class, new AABB(adjacentInvTile.getBlockPos()).inflate(1))) {
                    // Check if these frames are attached to the tile
                    BlockEntity tileOnFrame = adjacentInvTile.getLevel().getBlockEntity(i.blockPosition().relative(i.getDirection().getOpposite()));
                    ItemStack stackInFrame = i.getItem();
                    if (tileOnFrame == null || !tileOnFrame.equals(adjacentInvTile) || i.getItem().isEmpty() || stackInFrame.isEmpty()) {
                        continue;
                    }

                    if (stackInFrame.getItem() instanceof ItemScroll scrollItem) {
                        filters.add(stackToStore -> scrollItem.getSortPref(stackToStore, stackInFrame, inv));
                    }else{
                        filters.add(stackToStore -> stackToStore.getItem() == stackInFrame.getItem() ? ItemScroll.SortPref.HIGHEST : ItemScroll.SortPref.INVALID);
                    }
                }
                inventories.add(new FilterableItemHandler(inv, filters));
            });
        }
        return inventories;
    }

    public static List<FilterableItemHandler> fromPlayer(Player player){
        List<FilterableItemHandler> list = new ArrayList<>();
        list.add(new FilterableItemHandler(new PlayerMainInvWrapper(player.inventory), new ArrayList<>()));
        return list;
    }
}
