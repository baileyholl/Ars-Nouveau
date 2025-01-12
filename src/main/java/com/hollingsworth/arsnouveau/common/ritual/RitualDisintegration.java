package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RitualDisintegration extends AbstractRitual {

    @Override
    protected void tick() {
        Level world = getWorld();
        if (world.isClientSide) {
            BlockPos pos = getPos();

            for (int i = 0; i < 10; i++) {
                Vec3 particlePos = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(0.5, 0, 0.5);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(5, 5, 5));
                world.addParticle(ParticleLineData.createData(getCenterColor()),
                        particlePos.x(), particlePos.y(), particlePos.z(),
                        pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            }
        }

        if (!world.isClientSide && world.getGameTime() % 60 == 0) {
            boolean didWorkOnce = false;
            List<LivingEntity> entityList = world.getEntitiesOfClass(LivingEntity.class, new AABB(getPos()).inflate(5.0),
                    (m) -> (m.getClassification(false).equals(MobCategory.MONSTER) || m.getType().is(EntityTags.DISINTEGRATION_WHITELIST)) && !(m instanceof Player));
            for (LivingEntity m : entityList) {
                if (m.getType().is(EntityTags.DISINTEGRATION_BLACKLIST)) {
                    continue;
                }
                m.remove(Entity.RemovalReason.DISCARDED);
                if (m.isRemoved()) {

                    ParticleUtil.spawnPoof((ServerLevel) world, m.blockPosition());
                    if (m.shouldDropExperience()) {
                        int exp = m.getExperienceReward((ServerLevel) world, null) * 2;
                        if (exp > 0) {
                            int numGreater = exp / 12;
                            exp -= numGreater * 12;
                            int numLesser = exp / 3;
                            if ((exp - numLesser * 3) > 0)
                                numLesser++;
                            world.addFreshEntity(new ItemEntity(world, m.blockPosition().getX(), m.blockPosition().getY(), m.blockPosition().getZ(), new ItemStack(ItemsRegistry.GREATER_EXPERIENCE_GEM, numGreater)));
                            world.addFreshEntity(new ItemEntity(world, m.blockPosition().getX(), m.blockPosition().getY(), m.blockPosition().getZ(), new ItemStack(ItemsRegistry.EXPERIENCE_GEM, numLesser)));
                            didWorkOnce = true;
                        }

                    }
                }
            }
            if (didWorkOnce)
                setNeedsSource(true);
        }
    }

    @Override
    public int getSourceCost() {
        return 300;
    }

    @Override
    public String getLangName() {
        return "Disintegration";
    }

    @Override
    public String getLangDescription() {
        return "Destroys nearby monsters and converts them into Experience Gems worth twice as much experience. Monsters destroyed this way will not drop items. This ritual consumes source each time a monster is destroyed.";
    }

    @Override
    public ParticleColor getCenterColor() {
        return ParticleColor.makeRandomColor(220, 20, 20, rand);
    }


    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix( RitualLib.DISINTEGRATION);
    }
}
