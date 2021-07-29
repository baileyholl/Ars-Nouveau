package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

public class SummonEvent extends Event {
    public RayTraceResult rayTraceResult;
    public World world;
    public LivingEntity shooter;

    public SpellContext context;
    public ISummon summon;
    public SpellStats stats;

    public SummonEvent(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats stats, SpellContext spellContext, ISummon summon){
        this.rayTraceResult = rayTraceResult;
        this.world = world;
        this.shooter = shooter;
        this.stats = stats;
        this.context = spellContext;
        this.summon = summon;
    }

    public static class Death extends Event{
        public ISummon summon;
        public @Nullable DamageSource source;
        public World world;
        public boolean wasExpiration; //If the summon expired via time
        public Death(World world, ISummon summon, @Nullable DamageSource source, boolean wasExpiration){
            this.summon = summon;
            this.source = source;
            this.world = world;
            this.wasExpiration = wasExpiration;
        }
    }
}