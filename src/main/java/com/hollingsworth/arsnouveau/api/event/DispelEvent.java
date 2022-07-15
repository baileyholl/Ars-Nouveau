package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

public class DispelEvent extends Event {
    public HitResult rayTraceResult;
    public Level world;
    public LivingEntity shooter;
    public SpellStats augments;
    public SpellContext context;
    public IDispellable dispellable;

    public DispelEvent(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats augments, SpellContext spellContext, IDispellable dispellable) {
        this.rayTraceResult = rayTraceResult;
        this.world = world;
        this.shooter = shooter;
        this.augments = augments;
        this.context = spellContext;
        this.dispellable = dispellable;
    }

    @Cancelable
    public static class Pre extends DispelEvent {
        public Pre(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats augments, SpellContext spellContext, IDispellable dispellable) {
            super(rayTraceResult, world, shooter, augments, spellContext, dispellable);
        }
    }

    public static class Post extends DispelEvent {
        public Post(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats augments, SpellContext spellContext, IDispellable dispellable) {
            super(rayTraceResult, world, shooter, augments, spellContext, dispellable);
        }
    }
}
