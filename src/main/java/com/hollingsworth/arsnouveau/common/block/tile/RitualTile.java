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
        EntityRitualProjectile ritualProjectile = new EntityRitualProjectile(level, worldPosition.getX(), worldPosition.getY() + 1.0, worldPosition.getZ());
        ritualProjectile.setPos(ritualProjectile.getX() +0.5, ritualProjectile.getY(), ritualProjectile.getZ() +0.5);
        ritualProjectile.tilePos = this.getBlockPos();
        level.addFreshEntity(ritualProjectile);
    }
}
