package com.hollingsworth.arsnouveau.api.ritual;

import com.hollingsworth.arsnouveau.common.block.tile.RitualTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRitual {

    public RitualTile tile;
    private RitualContext context;
    private World world;
    private BlockPos pos;

    public AbstractRitual() { }

    public AbstractRitual(RitualTile tile, RitualContext context){
        this.tile = tile;
        this.world = tile.getLevel();
        this.pos = tile.getBlockPos();
        this.setContext(context);
    }


    public void tryTick(){
        if(tile == null)
            return;

        if(getContext().isDone)
            return;

        tick();
    }

    public @Nullable BlockPos getPos(){
        return tile != null ? tile.getBlockPos() : null;
    }

    public boolean canStart(){
        return true;
    }

    public List<ItemStack> getConsumableItems(){
        return new ArrayList<>();
    }

    public boolean canConsumeItem(ItemStack stack){
        return false;
    }

    public void onItemConsumed(ItemStack stack){
        stack.shrink(1);
    }


    public void onStart(){}

    protected abstract void tick();

    public void onEnd(){
        this.getContext().isDone = true;
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
        CompoundNBT contextTag = new CompoundNBT();
        getContext().write(contextTag);
        tag.put("context", contextTag);
    }
    // Called once the ritual tile has created a new instance of this ritual
    public void read(CompoundNBT tag){
        this.setContext(RitualContext.read(tag.getCompound("context")));
    }

    public @Nonnull RitualContext getContext() {
        if(context == null)
            context = new RitualContext();
        return context;
    }

    public void setContext(RitualContext context) {
        this.context = context;
    }

    public int getCost(){
        return 0;
    }
}
