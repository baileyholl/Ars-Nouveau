package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

public interface ILightable {

    /**
     * Called when a light spell is cast on this block or entity.
     */
    void onLight(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats augments, SpellContext spellContext);
}
