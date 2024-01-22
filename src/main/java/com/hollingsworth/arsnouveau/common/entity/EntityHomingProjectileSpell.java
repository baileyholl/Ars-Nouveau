package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class EntityHomingProjectileSpell extends EntityProjectileSpell {

    public EntityHomingProjectileSpell(EntityType<? extends EntityProjectileSpell> entityType, Level world) {
        super(entityType, world);
    }

    public EntityHomingProjectileSpell(Level world, SpellResolver resolver) {
        super(ModEntities.SPELL_PROJ_HOM.get(), world, resolver);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.SPELL_PROJ_HOM.get();
    }

    List<Predicate<LivingEntity>> ignore;
    LivingEntity target;

    public void setIgnored(List<Predicate<LivingEntity>> ignore) {
        this.ignore = ignore;
    }

    public List<Predicate<LivingEntity>> getIgnored() {
        return this.ignore;
    }

    @Override
    public void tickNextPosition() {
        if (!this.isRemoved()) {

            if ((target != null) && (!target.isAlive() || (target.distanceToSqr(this) > 50))) target = null;

            if (target == null && tickCount % 3 == 0) {


                List<LivingEntity> entities;
                if (getOwner() instanceof Player) {
                    entities = level.getEntitiesOfClass(LivingEntity.class,
                            this.getBoundingBox().inflate(4), this::shouldTarget);
                } else {
                    entities = level.getEntitiesOfClass(LivingEntity.class,
                            this.getBoundingBox().inflate(4), this::shouldTarget);
                }
                //update target or keep going
                if (entities.isEmpty() && target == null) {
                    super.tickNextPosition();
                } else if (!entities.isEmpty()) {
                    target = entities.stream().filter(e -> e.distanceToSqr(this) < 75).min(Comparator.comparingDouble(e -> e.distanceToSqr(this))).orElse(target);
                }
            }

            if (target != null) {
                homeTo(target.blockPosition());
            } else {
                super.tickNextPosition();
            }

        }
    }

    private void homeTo(BlockPos dest) {

        double posX = getX();
        double posY = getY();
        double posZ = getZ();
        double motionX = this.getDeltaMovement().x;
        double motionY = this.getDeltaMovement().y;
        double motionZ = this.getDeltaMovement().z;

        if (dest.getX() != 0 || dest.getY() != 0 || dest.getZ() != 0) {
            double targetX = dest.getX() + 0.5;
            double targetY = dest.getY() + 0.75;
            double targetZ = dest.getZ() + 0.5;
            Vec3 targetVector = new Vec3(targetX - posX, targetY - posY, targetZ - posZ);
            double length = targetVector.length();
            targetVector = targetVector.scale(0.3 / length);
            double weight = 0;
            if (length <= 3) {
                weight = (3.0 - length) * 0.3;
            }

            motionX = (0.9 - weight) * motionX + (0.1 + weight) * targetVector.x;
            motionY = (0.9 - weight) * motionY + (0.1 + weight) * targetVector.y;
            motionZ = (0.9 - weight) * motionZ + (0.1 + weight) * targetVector.z;
        }

        posX += motionX;
        posY += motionY;
        posZ += motionZ;
        this.setPos(posX, posY, posZ);

        this.setDeltaMovement(motionX, motionY, motionZ);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        boolean b = super.canHitEntity(entity);
        if (entity instanceof LivingEntity) b &= shouldTarget((LivingEntity) entity);
        return b;
    }

    private boolean shouldTarget(LivingEntity e) {
        if (ignore == null) return false;
        for (Predicate<LivingEntity> p : getIgnored()) {
            if (p.test(e)) {
                return false;
            }
        }
        return true;
    }

}
