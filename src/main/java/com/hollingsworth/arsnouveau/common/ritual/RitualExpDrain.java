package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.common.mixin.ExpInvokerMixin;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class RitualExpDrain extends AbstractRitual {


    @Override
    public void onStart() {
        super.onStart();
        if(tile == null)
            return;

    }

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

        if(!world.isClientSide && world.getGameTime() % 60 == 0){
            boolean didWorkOnce = false;
            List<LivingEntity> entityList = world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(getPos()).inflate(5.0),
                    (m) -> m.getClassification(false).equals(EntityClassification.MONSTER) && !(m instanceof PlayerEntity));
            for(LivingEntity m : entityList) {

                m.remove();
                if (m.removed) {
                    ExpInvokerMixin invoker = ((ExpInvokerMixin) m);
                    ParticleUtil.spawnPoof((ServerWorld) world, m.blockPosition());
                    if (invoker.an_shouldDropExperience()) {
                        int exp = invoker.an_getExperienceReward(new ANFakePlayer((ServerWorld) getWorld())) * 2;
                        if (exp > 0) {
                            int numGreater = (int) (exp / 12);
                            exp -= numGreater * 12;
                            int numLesser = (int) (exp / 3);
                            if ((exp - numLesser * 3) > 0)
                                numLesser++;
                            world.addFreshEntity(new ItemEntity(world, m.blockPosition().getX(), m.blockPosition().getY(), m.blockPosition().getZ(), new ItemStack(ItemsRegistry.GREATER_EXPERIENCE_GEM, numGreater)));
                            world.addFreshEntity(new ItemEntity(world, m.blockPosition().getX(), m.blockPosition().getY(), m.blockPosition().getZ(), new ItemStack(ItemsRegistry.EXPERIENCE_GEM, numLesser)));
                            didWorkOnce = true;
                        }

                    }
                }
            }
            if(didWorkOnce)
                setNeedsMana(true);
        }
    }

    @Override
    public int getManaCost() {
        return 300;
    }

    @Override
    public String getLangName() {
        return "Disintegration";
    }

    @Override
    public String getLangDescription() {
        return "Destroys nearby monsters and converts them into Experience Gems worth twice as much experience. Monsters destroyed this way will not drop items. This ritual consumes mana each time a monster is destroyed.";
    }

    @Override
    public ParticleColor getCenterColor() {
        return ParticleColor.makeRandomColor(220, 20, 20, rand);
    }

    @Override
    public String getID() {
        return RitualLib.DISINTEGRATION;
    }
}
