package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;

public class RitualWarp extends AbstractRitual {
    @Override
    protected void tick() {
        World world = getWorld();
        if(world.isClientSide){
            BlockPos pos = getPos();

            for(int i =0; i< 100; i++){
                Vector3d particlePos = new Vector3d(pos.getX(), pos.getY(), pos.getZ()).add(0.5, 0, 0.5);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(5,5,5));
                world.addParticle(ParticleLineData.createData(getCenterColor()),
                        particlePos.x(), particlePos.y(), particlePos.z(),
                        pos.getX()  +0.5, pos.getY() + 1  , pos.getZ() +0.5);
            }
        }
        if(!world.isClientSide && world.getGameTime() % 20 == 0){
            incrementProgress();
            if(getProgress() >= 3){
                List<Entity> entities = getWorld().getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(getPos()).inflate(5));

                ItemStack i = getConsumedItems().get(0);
                BlockPos b = WarpScroll.getPos(i);
                for(Entity a : entities){
                    if(b != null)
                        a.teleportTo(b.getX(), b.getY(), b.getZ());
                }
                if(b != null)
                    world.playSound(null, b, SoundEvents.PORTAL_TRAVEL, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                setFinished();
            }
        }
    }

    @Override
    public String getLangName() {
        return "Warping";
    }

    @Override
    public String getLangDescription() {
        return "Warps all nearby entities to the location on a warp scroll. Before starting the ritual, you must first augment the ritual with an inscribed Warp Scroll.";
    }

    @Override
    public ParticleColor getCenterColor() {
        return super.getCenterColor();
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return getConsumedItems().isEmpty() && stack.getItem() instanceof WarpScroll && WarpScroll.getPos(stack) != null && !(WarpScroll.getPos(stack).equals(new BlockPos(0,0,0)));
    }

    @Override
    public boolean canStart() {
        return getConsumedItems().size() > 0;
    }

    @Override
    public String getID() {
        return RitualLib.WARP;
    }
}
