package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.spell.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

public class EffectResolveEvent extends Event {
    public Level world;
    public @NotNull LivingEntity shooter;
    public HitResult rayTraceResult;
    public Spell spell;
    public SpellContext context;
    public AbstractEffect resolveEffect;
    public SpellStats spellStats;
    public SpellResolver resolver;

    private EffectResolveEvent(Level world, @NotNull LivingEntity shooter, HitResult result, Spell spell, SpellContext spellContext, AbstractEffect resolveEffect, SpellStats spellStats, SpellResolver spellResolver) {
        this.world = world;
        this.shooter = shooter;
        this.rayTraceResult = result;
        this.spell = spell;
        this.context = spellContext;
        this.resolveEffect = resolveEffect;
        this.spellStats = spellStats;
        this.resolver = spellResolver;
    }

    /**
     * Fired before a glyph is resolved and after SpellStats are calculated.
     * Use this to modify the spell or stats before resolving. Can be cancelled to stop resolving.
     */
    public static class Pre extends EffectResolveEvent implements ICancellableEvent {
        public Pre(Level world, @NotNull LivingEntity shooter, HitResult result, Spell spell, SpellContext spellContext, AbstractEffect resolveEffect, SpellStats spellStats, SpellResolver spellResolver) {
            super(world, shooter, result, spell, spellContext, resolveEffect, spellStats, spellResolver);
        }

    }

    /**
     * Fired after a glyph has resolved. Cannot be canceled.
     */
    public static class Post extends EffectResolveEvent {

        public Post(Level world, @NotNull LivingEntity shooter, HitResult result, Spell spell, SpellContext spellContext, AbstractEffect resolveEffect, SpellStats spellStats, SpellResolver spellResolver) {
            super(world, shooter, result, spell, spellContext, resolveEffect, spellStats, spellResolver);
        }

    }
}
