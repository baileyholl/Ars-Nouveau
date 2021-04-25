package com.hollingsworth.arsnouveau.api.ritual;

import com.hollingsworth.arsnouveau.common.block.tile.RitualTile;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class AbstractRitual {

    public RitualTile tile;
    public RitualContext context;
    private World world;
    private BlockPos pos;

    public AbstractRitual() { }

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

    public @Nullable BlockPos getPos(){
        return tile != null ? tile.getBlockPos() : null;
    }

    public boolean canStart(){
        return true;
    }


    public void onStart(){}

    protected abstract void tick();

    public void onEnd(){
        this.context.isDone = true;
    }
    /*Must follow rules for the lang file.*/
    public abstract String getID();

    public String getName(){
        return new TranslationTextComponent("ars_nouveau.ritual_name." + getID()).getString();
    }

    public String getDescription(){
        return new TranslationTextComponent("ars_nouveau.ritual_desc." + getID()).getString();
    }

    public void write(CompoundNBT tag){
        if(context != null){
            CompoundNBT contextTag = new CompoundNBT();
            context.write(contextTag);
            tag.put("context", contextTag);
        }
    }
    // Called once the ritual tile has created a new instance of this ritual
    public void read(CompoundNBT tag){
        this.context = RitualContext.read(tag.getCompound("context"));
    }
}
