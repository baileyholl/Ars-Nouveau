package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectIntangible;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class RitualIntangible extends AbstractRitual {
    List<BlockPos> startingPos = new ArrayList<>();
    public int y;
    public boolean setPositions = false;
    @Override
    protected void tick() {
        if(getWorld().isClientSide){
            return;
        }
        // Get blocks under the tile
        BlockPos pos = getPos();
        if(!setPositions) {
            for (BlockPos pos1 : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, -1, 1))) {
                startingPos.add(pos1.immutable());
            }
            setPositions = true;
        }
        List<BlockPos> toRemove = new ArrayList<>();
        for(BlockPos pos1 : startingPos){
            BlockPos adjusted = pos1.below(y);
            if(getWorld().isOutsideBuildHeight(adjusted.getY()) || getWorld().getBlockState(adjusted).isAir()){
                toRemove.add(pos1);
            }else{
                EffectIntangible.makeIntangible(getWorld(), adjusted, 100);
            }
        }
        startingPos.removeAll(toRemove);
        y++;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(ArsNouveau.MODID, "intangible");
    }
}
