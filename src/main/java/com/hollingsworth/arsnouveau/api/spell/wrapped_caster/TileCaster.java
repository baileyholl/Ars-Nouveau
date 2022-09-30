package com.hollingsworth.arsnouveau.api.spell.wrapped_caster;

import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.InvUtil;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
        handlers = new ArrayList<>(InvUtil.adjacentInventories(tile.getLevel(), tile.getBlockPos()));
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
