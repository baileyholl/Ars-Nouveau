package com.hollingsworth.arsnouveau.api.ritual;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.RitualTile;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public abstract class AbstractRitual {

    public RitualTile tile;
    private RitualContext context;

    public Random rand = new Random();

    public AbstractRitual() { }

    public AbstractRitual(RitualTile tile, RitualContext context){
        this.tile = tile;
        this.setContext(context);
    }


    public void tryTick(){
        if(tile == null || !getContext().isStarted || getContext().isDone) {
            return;
        }

        tick();
    }

    public @Nullable BlockPos getPos(){
        return tile != null ? tile.getBlockPos() : null;
    }

    public @Nullable World getWorld(){return tile != null ? tile.getLevel() : null;}

    public boolean canStart(){
        return true;
    }

    public List<ItemStack> getConsumedItems(){
        return getContext().consumedItems;
    }

    public boolean canConsumeItem(ItemStack stack){
        return false;
    }

    public void onItemConsumed(ItemStack stack){
        this.getConsumedItems().add(stack.copy());
        stack.shrink(1);
        BlockUtil.safelyUpdateState(getWorld(), tile.getBlockPos());
    }

    public boolean didConsumeItem(Item item){
        for(ItemStack i : getConsumedItems()){
            if(i.getItem() == item)
                return true;
        }
        return false;
    }

    public void incrementProgress(){
        getContext().progress++;
    }

    public int getProgress(){
        return getContext().progress;
    }

    public void onStart(){
        getContext().isStarted = true;
    }

    public boolean isRunning(){
        return getContext().isStarted && !getContext().isDone;
    }

    public boolean isDone(){
        return getContext().isDone;
    }

    public void setFinished(){
        getContext().isDone = true;
    }

    protected abstract void tick();

    public void onEnd(){
        this.getContext().isDone = true;
    }
    /*Must follow rules for the lang file.*/
    public abstract String getID();

    public String getName(){
        return new TranslationTextComponent("item.ars_nouveau.ritual_" + getID()).getString();
    }

    public String getDescription(){
        return new TranslationTextComponent("ars_nouveau.ritual_desc." + getID()).getString();
    }

    public int getManaCost(){
        return 0;
    }

    public boolean consumesMana(){
        return getManaCost() > 0;
    }

    public void setNeedsMana(boolean needMana){
        getContext().needsManaToRun = needMana;
        BlockUtil.safelyUpdateState(getWorld(), tile.getBlockPos());
    }

    public boolean needsManaNow(){
        return getContext().needsManaToRun;
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


    public ParticleColor getCenterColor(){
        return new ParticleColor(
                rand.nextInt(255),
                rand.nextInt(22),
                rand.nextInt(255)
        );
    }

    public ParticleColor getOuterColor(){
        return getCenterColor();
    }

    public int getParticleIntensity(){
        return 50;
    }

    public String getLangName(){
        return "";
    }
    public String getLangDescription(){
        return "";
    }
}
