package com.hollingsworth.arsnouveau.api.entity;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface IDispellable {
    /**
     * When dispel hits a target
     *
     * @param caster The entity that cast this spell. This can be NULL in the case of runes and machines that cast spells.
     * @return Returns true if dispel was successful.
     */
    boolean onDispel(@NotNull LivingEntity caster);

}
