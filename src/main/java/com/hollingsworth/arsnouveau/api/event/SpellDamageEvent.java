package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

public class SpellDamageEvent extends Event {

    public DamageSource damageSource;
    public SpellContext context;
    public @NotNull LivingEntity caster;
    public Entity target;
    public float damage;

    private SpellDamageEvent(DamageSource source, @NotNull LivingEntity shooter, Entity entity, float totalDamage, SpellContext context) {
        this.damageSource = source;
        this.caster = shooter;
        this.target = entity;
        this.damage = totalDamage;
        this.context = context;
    }

    /**
     * Apply effects to the target before the spell damage resolves.
     */
    public static class Pre extends SpellDamageEvent implements ICancellableEvent {

        public Pre(DamageSource source, LivingEntity shooter, Entity entity, float totalDamage, SpellContext context) {
            super(source, shooter, entity, totalDamage, context);
        }

    }

    public static class Post extends SpellDamageEvent{

        public Post(DamageSource source, LivingEntity shooter, Entity entity, float totalDamage, SpellContext context) {
            super(source, shooter, entity, totalDamage, context);
        }
    }

}
