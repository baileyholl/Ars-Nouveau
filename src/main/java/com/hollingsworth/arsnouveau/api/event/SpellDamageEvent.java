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

}
