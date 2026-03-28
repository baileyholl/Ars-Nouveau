package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Custom explosion that caps damage to baseDamage + amps * ampDamageScalar.
 * Migrated from extending Explosion (class, pre-1.21.11) to wrapping ServerExplosion
 * because Explosion became an interface in 1.21.11.
 *
 * The custom damage is applied manually; block destruction delegates to Level.explode().
 */
public class ANExplosion {
    public double amps;
    public double baseDamage;
    public double ampDamageScalar;

    private final Level level;
    @Nullable
    private final Entity source;
    @Nullable
    private final DamageSource damageSource;
    @Nullable
    private final ExplosionDamageCalculator damageCalculator;
    private final double x, y, z;
    private final float radius;
    private final boolean fire;
    private final Explosion.BlockInteraction blockInteraction;

    public ANExplosion(Level level, @Nullable Entity source, @Nullable DamageSource damageSource,
                       @Nullable ExplosionDamageCalculator damageCalculator,
                       double x, double y, double z, float radius, boolean fire,
                       Explosion.BlockInteraction blockInteraction, double numAmps) {
        this.level = level;
        this.source = source;
        this.damageSource = damageSource;
        this.damageCalculator = damageCalculator;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.fire = fire;
        this.blockInteraction = blockInteraction;
        this.amps = numAmps;
    }

    /**
     * Performs custom entity damage with damage cap, then delegates block destruction to Level.explode.
     * Returns a representative Explosion from the level for callers that need one.
     */
    public void explodeWithCustomDamage() {
        float f2 = this.radius * 2.0F;
        int k1 = Mth.floor(this.x - (double) f2 - 1.0D);
        int l1 = Mth.floor(this.x + (double) f2 + 1.0D);
        int i2 = Mth.floor(this.y - (double) f2 - 1.0D);
        int i1 = Mth.floor(this.y + (double) f2 + 1.0D);
        int j2 = Mth.floor(this.z - (double) f2 - 1.0D);
        int j1 = Mth.floor(this.z + (double) f2 + 1.0D);

        List<Entity> list = this.level.getEntities(this.source, new AABB(k1, i2, j2, l1, i1, j1));
        Vec3 vector3d = new Vec3(this.x, this.y, this.z);

        DamageSource ds = this.damageSource != null ? this.damageSource
                // 1.21.11: explosion(LivingEntity) removed; use explosion(Entity, Entity) - pass source as both direct and indirect
                : this.level.damageSources().explosion(this.source, this.source);

        // 1.21.11: Explosion is an interface; entity.ignoreExplosion(null) NPEs for ItemEntity/ItemFrame etc.
        // Create a ServerExplosion solely for ignoreExplosion checks — we do NOT call explode() on it.
        Explosion explosionCheck = (this.level instanceof net.minecraft.server.level.ServerLevel sl)
                ? new ServerExplosion(sl, this.source, ds, this.damageCalculator, vector3d, this.radius, this.fire, this.blockInteraction)
                : null;

        for (Entity entity : list) {
            if (explosionCheck != null && entity.ignoreExplosion(explosionCheck)) continue;
            double d12 = Mth.sqrt((float) entity.distanceToSqr(vector3d)) / f2;
            if (d12 <= 1.0D) {
                double d5 = entity.getX() - this.x;
                double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y;
                double d9 = entity.getZ() - this.z;
                double d13 = Mth.sqrt((float) (d5 * d5 + d7 * d7 + d9 * d9));
                if (d13 != 0.0D) {
                    d5 = d5 / d13;
                    d7 = d7 / d13;
                    d9 = d9 / d13;
                    double d14 = ServerExplosion.getSeenPercent(vector3d, entity);
                    double d10 = (1.0D - d12) * d14;
                    float damage = (float) Math.min(
                            Math.max(0.0f, (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f2 + 1.0D))),
                            baseDamage + this.amps * ampDamageScalar);
                    entity.hurt(ds, damage);
                    double d11 = d10;
                    if (entity instanceof LivingEntity livingEntity) {
                        d11 = d10 * (1.0 - livingEntity.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE));
                    }
                    entity.setDeltaMovement(entity.getDeltaMovement().add(d5 * d11, d7 * d11, d9 * d11));
                }
            }
        }

        // Delegate block destruction to the standard level explosion (does not re-hurt entities)
        Level.ExplosionInteraction interaction = switch (blockInteraction) {
            case DESTROY -> Level.ExplosionInteraction.BLOCK;
            case DESTROY_WITH_DECAY -> Level.ExplosionInteraction.MOB;
            case KEEP -> Level.ExplosionInteraction.NONE;
            case TRIGGER_BLOCK -> Level.ExplosionInteraction.TNT;
        };
        this.level.explode(this.source, x, y, z, radius, fire, interaction);
    }

    public List<BlockPos> getToBlow() {
        return List.of();
    }
}
