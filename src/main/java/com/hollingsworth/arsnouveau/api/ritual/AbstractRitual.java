package com.hollingsworth.arsnouveau.api.ritual;

import com.hollingsworth.arsnouveau.common.block.tile.RitualTile;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractRitual {

    public RitualTile tile;
    public RitualContext context;
    private World world;
    private BlockPos pos;
    public AbstractRitual(RitualTile tile, RitualContext context){
        this.tile = tile;
        this.world = tile.getLevel();
        this.pos = tile.getBlockPos();
        this.context = context;
    }


    public void tryTick(){
        if(tile == null || context == null)
            return;

        if(context.isDone)
            return;

        tick();
    }

    public void onStart(){}

    protected abstract void tick();

    public void onEnd(){
        this.context.isDone = true;
    }

    public abstract String getID();
}
