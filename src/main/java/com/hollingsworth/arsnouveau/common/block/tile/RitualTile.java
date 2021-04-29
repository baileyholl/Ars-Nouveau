package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.entity.EntityRitualProjectile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

public class RitualTile extends TileEntity implements ITickableTileEntity, ITooltipProvider {
    public AbstractRitual ritual;
    public RitualTile() {
        super(BlockRegistry.RITUAL_TILE);
    }

    @Override
    public void tick() {
        if(ritual != null){
            if(ritual.getContext().isDone){
                ritual.onEnd();
                ritual = null;
                return;
            }
            ritual.tryTick();
        }
    }

    public boolean isRitualRunning(){
        return ritual != null && !ritual.getContext().isDone;
    }

    public boolean canAffordCost(int currentExp){
        return ritual.getCost() <= currentExp;
    }

    public boolean canRitualStart(){
        return ritual.canStart();
    }


    public void startRitual(){
        if(ritual == null)
            return;
        ritual.onStart();
        EntityRitualProjectile ritualProjectile = new EntityRitualProjectile(level, worldPosition.getX(), worldPosition.getY() + 1.0, worldPosition.getZ());
        ritualProjectile.setPos(ritualProjectile.getX() +0.5, ritualProjectile.getY(), ritualProjectile.getZ() +0.5);
        ritualProjectile.tilePos = this.getBlockPos();
        level.addFreshEntity(ritualProjectile);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        String ritualID = tag.getString("ritualID");
        if(!ritualID.isEmpty()){
            ritual = ArsNouveauAPI.getInstance().getRitual(ritualID);
            if(ritual != null)
                ritual.read(tag);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        if(ritual != null){
            tag.putString("ritualID", ritual.getID());
            ritual.write(tag);
        }
        return super.save(tag);
    }

    public void setRitual(String selectedRitual) {
        this.ritual = ArsNouveauAPI.getInstance().getRitual(selectedRitual);
    }


    @Override
    public List<String> getTooltip() {
        List<String> tooltips = new ArrayList<>();
        if(ritual != null){
            tooltips.add(ritual.getName());
        }
        return tooltips;
    }

    public int getRitualCost() {
        return ritual.getCost();
    }
}
