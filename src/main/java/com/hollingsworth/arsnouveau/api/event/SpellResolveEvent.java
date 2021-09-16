package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

public class SpellResolveEvent extends Event {
    public World world;
    public @Nullable LivingEntity shooter;
    public RayTraceResult rayTraceResult;
    public Spell spell;
    public SpellContext context;


    public SpellResolveEvent(World world, LivingEntity shooter, RayTraceResult result, Spell spell, SpellContext spellContext){
        this.world = world;
        this.shooter = shooter;
        this.rayTraceResult = result;
        this.spell = spell;
        this.context = spellContext;
    }

    /**
     * Fired before a spell is resolved. Can be cancelled to stop resolving.
     */
    public static class Pre extends SpellResolveEvent{
        public Pre(World world, LivingEntity shooter, RayTraceResult result, Spell spell, SpellContext spellContext){
            super(world, shooter, result, spell, spellContext);
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    /**
     * Fired after a spell has resolved its effects. Cannot be canceled.
     */
    public static class Post extends SpellResolveEvent{

        public Post(World world, LivingEntity shooter, RayTraceResult result, Spell spell, SpellContext spellContext) {
            super(world, shooter, result, spell, spellContext);
        }

        @Override
        public boolean isCancelable() {
            return false;
        }
    }
}
