package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.ritual.RitualContext;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.RitualTile;
import com.hollingsworth.arsnouveau.common.entity.EntityRitualProjectile;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RitualDig extends AbstractRitual {

    public RitualDig(){
        super();
    }

    public RitualDig(RitualTile tile, RitualContext context) {
        super(tile, context);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(tile == null)
            return;
        EntityRitualProjectile ritualProjectile = new EntityRitualProjectile(getWorld(), getPos().above());
        ritualProjectile.setPos(ritualProjectile.getX() + 0.5, ritualProjectile.getY(), ritualProjectile.getZ() +0.5);
        ritualProjectile.tilePos = getPos();
        getWorld().addFreshEntity(ritualProjectile);
    }

    @Override
    public void tick() {
        World world = tile.getLevel();
        if(world.getGameTime() % 20 == 0 && !world.isClientSide){
            BlockPos pos = tile.getBlockPos().north().below(getContext().progress);
            if(pos.getY() <= 1){
                onEnd();
                return;
            }
            world.destroyBlock(pos, true);
            world.destroyBlock(pos.south().south(), true);
            world.destroyBlock(pos.south().east(), true);
            world.destroyBlock(pos.south().west(), true);
            getContext().progress++;
        }
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(
                rand.nextInt(50),
                rand.nextInt(255),
                rand.nextInt(20));
    }

    @Override
    public String getID() {
        return RitualLib.DIG;
    }
}
