package com.hollingsworth.arsnouveau.api.spell.wrapped_caster;

import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.InvUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TileCaster implements IWrappedCaster{
    protected SpellContext.CasterType casterType;
    protected BlockEntity tile;

    public TileCaster(BlockEntity tile, SpellContext.CasterType casterType){
        this.tile = tile;
        this.casterType = casterType;
    }

    @Override
    public @NotNull List<FilterableItemHandler> getInventory() {
        return new ArrayList<>(InvUtil.adjacentInventories(tile.getLevel(), tile.getBlockPos()));
    }

    @Override
    public SpellContext.CasterType getCasterType() {
        return casterType;
    }

    public BlockEntity getTile(){
        return tile;
    }

    @Override
    public Direction getFacingDirection() {
        if(tile.getBlockState().hasProperty(BlockStateProperties.FACING)){
            return tile.getBlockState().getValue(BlockStateProperties.FACING);
        }
        return Direction.NORTH;
    }

    @Override
    public BlockEntity getNearbyBlockEntity(Predicate<BlockEntity> predicate) {
        for(Direction dir : Direction.values()){
            BlockEntity tile = this.tile.getLevel().getBlockEntity(this.tile.getBlockPos().relative(dir));
            if(tile != null && predicate.test(tile)){
                return tile;
            }
        }
        return null;
    }
}
