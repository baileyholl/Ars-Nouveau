package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

public class SummonUtil {
    public static boolean canSummonTakeDamage(DamageSource source){
        return source.isBypassInvul() || ((source.getEntity() != null) && (source.getEntity() instanceof Player));
    }
}
