package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;

/**
 * Event fired when a spell projectile hits something.
 * Provided for addons, not used in the base mod
 */
public class SpellProjectileHitEvent extends EntityEvent implements ICancellableEvent {
    public HitResult hit;
    public EntityProjectileSpell projectile;

    public SpellProjectileHitEvent(EntityProjectileSpell entity, HitResult result) {
        super(entity);
        projectile = entity;
        hit = result;
    }

    public EntityProjectileSpell getProjectile() {
        return projectile;
    }

    public HitResult getHitResult() {
        return hit;
    }

}
