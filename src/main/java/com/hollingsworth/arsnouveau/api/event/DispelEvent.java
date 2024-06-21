package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.Cancelable;
import net.neoforged.bus.api.Event;
import javax.annotation.Nullable;

/**
 * DispelEvent is fired when a spell is attempting to dispel an entity or block.
 * Entities and tiles can also extend IDispellable to allow for custom dispel behavior.
 */
public class DispelEvent extends Event {
    public HitResult rayTraceResult;
    public Level world;
    public LivingEntity shooter;
    public SpellStats augments;
    public SpellContext context;

    public DispelEvent(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats augments, SpellContext spellContext) {
        this.rayTraceResult = rayTraceResult;
        this.world = world;
        this.shooter = shooter;
        this.augments = augments;
        this.context = spellContext;
    }

    @Cancelable
    public static class Pre extends DispelEvent {
        public Pre(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats augments, SpellContext spellContext) {
            super(rayTraceResult, world, shooter, augments, spellContext);
        }
    }

    public static class Post extends DispelEvent {
        public Post(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats augments, SpellContext spellContext) {
            super(rayTraceResult, world, shooter, augments, spellContext);
        }
    }
}
