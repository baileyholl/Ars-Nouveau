package com.hollingsworth.arsnouveau.api.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Event;

public class SpellDamageEvent extends Event {

    public DamageSource damageSource;
    public LivingEntity caster;
    public Entity target;
    public float damage;

    /**
     * This constructor is becoming private in favor of PRE and POST.
     */
    @Deprecated(forRemoval = true, since = "1.19.2")
    public SpellDamageEvent(DamageSource source, LivingEntity shooter, Entity entity, float totalDamage) {
        this.damageSource = source;
        this.caster = shooter;
        this.target = entity;
        this.damage = totalDamage;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    /**
     * Apply effects to the target before the spell damage resolves.
     */
    public static class Pre extends SpellDamageEvent {

        public Pre(DamageSource source, LivingEntity shooter, Entity entity, float totalDamage) {
            super(source, shooter, entity, totalDamage);
        }
        // TODO: Make this true then SpellDamageEvent is private.
        @Override
        public boolean isCancelable() {
            return false;
        }
    }

    public static class Post extends SpellDamageEvent{

        public Post(DamageSource source, LivingEntity shooter, Entity entity, float totalDamage) {
            super(source, shooter, entity, totalDamage);
        }

        @Override
        public boolean isCancelable() {
            return false;
        }
    }

}
