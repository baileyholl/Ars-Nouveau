package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.entity.EntityRitualProjectile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class RitualTile extends TileEntity implements ITickableTileEntity {
    public AbstractRitual ritual;
    public RitualTile() {
        super(BlockRegistry.RITUAL_TILE);
    }

    @Override
    public void tick() {
        if(ritual != null){
            if(ritual.context.isDone){
                ritual.onEnd();
                ritual = null;
                return;
            }
            ritual.tryTick();
        }
    }

    public void startRitual(AbstractRitual ritual){
        this.ritual = ritual;
        ritual.onStart();
        EntityRitualProjectile ritualProjectile = new EntityRitualProjectile(world, pos.getX(), pos.getY() + 1.0, pos.getZ());
        ritualProjectile.setPosition(ritualProjectile.getPosX() +0.5, ritualProjectile.getPosY(), ritualProjectile.getPosZ() +0.5);
        ritualProjectile.tilePos = this.getPos();
        world.addEntity(ritualProjectile);
    }
}
