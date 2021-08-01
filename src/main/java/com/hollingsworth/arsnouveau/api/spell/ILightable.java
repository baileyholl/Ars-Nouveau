package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public interface ILightable {

    /**
     * Called when a light spell is cast on this block or entity.
     */
    void onLight(RayTraceResult rayTraceResult, World world, LivingEntity shooter, SpellStats augments, SpellContext spellContext);
}
