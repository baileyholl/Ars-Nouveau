package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class SummonUtil {
    public static boolean canSummonTakeDamage(DamageSource source) {
        return source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || ((source.getEntity() != null) && (source.getEntity() instanceof Player));
    }

    public static void healOverTime(LivingEntity entity) {
        if (!entity.level.isClientSide && entity.level.getGameTime() % 20 == 0 && !entity.isDeadOrDying()) {
            entity.heal(1.0f);
        }
    }
}
