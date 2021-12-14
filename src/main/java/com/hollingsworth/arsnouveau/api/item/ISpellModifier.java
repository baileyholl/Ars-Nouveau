package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

public interface ISpellModifier {
    /**
     * Adjust spell stats with this interface as it pertains to a specific spellPart, usually an effect.
     */
    default SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellContext spellContext){
        return builder;
    }

}
