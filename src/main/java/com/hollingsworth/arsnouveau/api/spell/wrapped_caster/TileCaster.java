package com.hollingsworth.arsnouveau.api.spell.wrapped_caster;

import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TileCaster implements IWrappedCaster{
    private SpellContext.CasterType casterType;
    private List<FilterableItemHandler> handlers;
    public BlockEntity tile;

    public TileCaster(BlockEntity tile, SpellContext.CasterType casterType){
        this.tile = tile;
        this.casterType = casterType;
        handlers = new ArrayList<>();
        initFilterables();
    }

    public void initFilterables(){
        for (Direction d : Direction.values()) {
            BlockEntity adjacentInvTile = tile.getLevel().getBlockEntity(tile.getBlockPos().relative(d));
            if (adjacentInvTile == null)
                continue;
            adjacentInvTile.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv ->{
                List<Function<ItemStack, ItemScroll.SortPref>> filters = new ArrayList<>();
                for (ItemFrame i : tile.getLevel().getEntitiesOfClass(ItemFrame.class, new AABB(tile.getBlockPos()).inflate(1))) {
                    // Check if these frames are attached to the tile
                    BlockEntity adjTile = tile.getLevel().getBlockEntity(i.blockPosition().relative(i.getDirection().getOpposite()));
                    ItemStack stackInFrame = i.getItem();
                    if (adjTile == null || !adjTile.equals(adjacentInvTile) || i.getItem().isEmpty() || stackInFrame.isEmpty()) {
                        continue;
                    }

                    if (stackInFrame.getItem() instanceof ItemScroll scrollItem) {
                        filters.add(stackToStore -> scrollItem.getSortPref(stackToStore, stackInFrame, inv));
                    }else{
                        filters.add(stackToStore -> stackToStore.getItem() == stackInFrame.getItem() ? ItemScroll.SortPref.HIGHEST : ItemScroll.SortPref.INVALID);
                    }
                }
                handlers.add(new FilterableItemHandler(inv, filters));
            });
        }
    }

    @Override
    public @NotNull List<FilterableItemHandler> getInventory() {
        return handlers;
    }

    @Override
    public SpellContext.CasterType getCasterType() {
        return casterType;
    }
}
