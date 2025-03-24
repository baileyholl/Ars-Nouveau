package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.item.inv.FilterSet;
import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class InvUtil {
    public static List<FilterableItemHandler> adjacentInventories(Level level, BlockPos pos){
        List<FilterableItemHandler> inventories = new ArrayList<>();
        for (Direction d : Direction.values()) {
            BlockPos relativePos = pos.relative(d);

            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, relativePos, level.getBlockState(relativePos), null, d.getOpposite());
            if(handler == null)
                continue;
            inventories.add(new FilterableItemHandler(handler, FilterSet.forPosition(level, pos)));
        }
        return inventories;
    }

    // Use FilterSet
    @Deprecated(forRemoval = true)
    public static List<Function<ItemStack, ItemScroll.SortPref>> filtersOnTile(@Nullable BlockEntity thisTile){
        if(thisTile == null){
            return new ArrayList<>();
        }
        FilterSet filterSet = FilterSet.forPosition(thisTile.getLevel(), thisTile.getBlockPos());
        if(filterSet instanceof FilterSet.ListSet listSet){
            return listSet.filters;
        }
        return new ArrayList<>();
    }

    public static List<FilterableItemHandler> fromPlayer(Player player){
        List<FilterableItemHandler> list = new ArrayList<>();
        list.add(new FilterableItemHandler(new PlayerMainInvWrapper(player.inventory), new ArrayList<>()));
        return list;
    }
}
