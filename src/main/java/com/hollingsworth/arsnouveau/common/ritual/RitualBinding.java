package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class RitualBinding extends AbstractRitual {


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
                List<Entity> entities = getWorld().getEntitiesOfClass(Entity.class, new AxisAlignedBB(getPos()).inflate(5));

                for(Entity entity : entities){
                    for(AbstractFamiliarHolder familiarHolder : ArsNouveauAPI.getInstance().getFamiliarHolderMap().values()){
                        if(familiarHolder.isEntity.test(entity)){
                            entity.remove();
                            ParticleUtil.spawnPoof((ServerWorld) world, entity.blockPosition());
                            world.addFreshEntity(new ItemEntity(world, entity.blockPosition().getX(), entity.blockPosition().getY(), entity.blockPosition().getZ(), familiarHolder.getOutputItem()));
                            world.playSound(null, entity.blockPosition(), SoundEvents.BOOK_PUT, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                        }
                    }
                }
                setFinished();
            }
        }
    }

    @Override
    public String getLangName() {
        return "Binding";
    }

    @Override
    public String getLangDescription() {
        return "The Ritual of Binding converts nearby eligible entities into Bound Scripts, used for summoning a Familiar. For more information, see the section on Familiars.";
    }

    @Override
    public String getID() {
        return RitualLib.BINDING;
    }
}
