package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

/**
 * Fired before effects are resolved. Attach any items, augments, or number modifiers to the stats builder at this phase.
 */
public class SpellModifierEvent extends Event {

    public SpellStats.Builder builder;
    public @Nullable LivingEntity caster;
    public AbstractSpellPart spellPart;
    public RayTraceResult rayTraceResult;
    public World world;
    public SpellContext spellContext;

    public SpellModifierEvent(LivingEntity caster, SpellStats.Builder builder, AbstractSpellPart spellPart, RayTraceResult rayTraceResult, World world, SpellContext spellContext) {
        this.caster = caster;
        this.builder = builder;
        this.spellPart = spellPart;
        this.rayTraceResult = rayTraceResult;
        this.world = world;
        this.spellContext = spellContext;
    }
}
