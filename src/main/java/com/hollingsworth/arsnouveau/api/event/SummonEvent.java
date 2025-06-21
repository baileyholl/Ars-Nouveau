package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.Event;

import javax.annotation.Nullable;

public class SummonEvent extends Event {
    public HitResult rayTraceResult;
    public Level world;
    public LivingEntity shooter;

    public SpellContext context;
    public ISummon summon;
    public SpellStats stats;

    public SummonEvent(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats stats, SpellContext spellContext, ISummon summon) {
        this.rayTraceResult = rayTraceResult;
        this.world = world;
        this.shooter = shooter;
        this.stats = stats;
        this.context = spellContext;
        this.summon = summon;
    }

    public static class Death extends Event {
        public ISummon summon;
        public @Nullable DamageSource source;
        public Level world;
        public boolean wasExpiration; //If the summon expired via time

        public Death(Level world, ISummon summon, @Nullable DamageSource source, boolean wasExpiration) {
            this.summon = summon;
            this.source = source;
            this.world = world;
            this.wasExpiration = wasExpiration;
        }
    }
}