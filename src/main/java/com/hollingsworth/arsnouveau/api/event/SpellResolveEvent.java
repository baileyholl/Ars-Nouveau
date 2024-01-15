package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

public class SpellResolveEvent extends Event {
    public Level world;
    public @NotNull LivingEntity shooter;
    public HitResult rayTraceResult;
    public Spell spell;
    public SpellContext context;
    public SpellResolver resolver;

    private SpellResolveEvent(Level world, @NotNull LivingEntity shooter, HitResult result, Spell spell, SpellContext spellContext, SpellResolver resolver) {
        this.world = world;
        this.shooter = shooter;
        this.rayTraceResult = result;
        this.spell = spell;
        this.context = spellContext;
        this.resolver = resolver;
    }

    /**
     * Fired before a spell is resolved. Can be cancelled to stop resolving.
     */
    public static class Pre extends SpellResolveEvent {
        public Pre(Level world, @NotNull LivingEntity shooter, HitResult result, Spell spell, SpellContext spellContext, SpellResolver resolver) {
            super(world, shooter, result, spell, spellContext, resolver);
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    /**
     * Fired after a spell has resolved its effects. Cannot be canceled.
     */
    public static class Post extends SpellResolveEvent {

        public Post(Level world, @NotNull LivingEntity shooter, HitResult result, Spell spell, SpellContext spellContext, SpellResolver resolver) {
            super(world, shooter, result, spell, spellContext, resolver);
        }

        @Override
        public boolean isCancelable() {
            return false;
        }
    }
}
