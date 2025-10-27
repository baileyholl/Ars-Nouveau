package com.hollingsworth.arsnouveau.common.spell.rewind;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.common.event.timed.IRewindable;
import com.hollingsworth.arsnouveau.common.event.timed.RewindEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.phys.Vec3;

public class RewindEntityData implements IRewindCallback {
    public long gameTime;
    public Vec3 deltaMovement;
    public Vec3 position;
    public float health;

    public RewindEntityData(long gameTime, Vec3 deltaMovement, Vec3 position, float health) {
        this.gameTime = gameTime;
        this.deltaMovement = deltaMovement;
        this.position = position;
        this.health = health;
    }

    @Override
    public void onRewind(RewindEvent event) {
        var entity = event.entity;
        if (!(entity instanceof IRewindable rewindable) || entity.isRemoved() || (entity instanceof LivingEntity living && living.isDeadOrDying())) {
            return;
        }
        entity.hurtMarked = true;
        // Disable the flag that prevents the entity from moving and reenable
        rewindable.setRewinding(false);

        if (event.respectsGravity) {
            if (entity instanceof LivingEntity le) {
                var weightless = le.getAttribute(PerkAttributes.WEIGHT);
                var modifierId = ArsNouveau.prefix("rewind");
                if (weightless != null) {
                    weightless.addOrUpdateTransientModifier(new AttributeModifier(modifierId, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
                }
            } else {
                entity.setNoGravity(true);
            }
        }
        entity.setPos(position);
        entity.setDeltaMovement(deltaMovement.scale(-1));
        entity.fallDistance = 0;
        rewindable.setRewinding(true);

        if (event.serverSide) {
            if (entity instanceof LivingEntity living) {
                living.setHealth(health);
            }
        }
    }
}
