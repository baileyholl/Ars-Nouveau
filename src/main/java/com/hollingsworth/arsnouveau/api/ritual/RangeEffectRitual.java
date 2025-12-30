package com.hollingsworth.arsnouveau.api.ritual;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public abstract class RangeEffectRitual extends RangeRitual {

    abstract public Holder<MobEffect> getEffect();

    abstract public int getRange();

    abstract public int getDuration();

    public boolean shouldApply(ServerPlayer player) {
        return !player.level.isClientSide && !needsSourceNow() && BlockUtil.distanceFrom(getPos(), player.blockPosition()) <= getRange();
    }

    public boolean attemptRefresh(ServerPlayer player) {
        if (!shouldApply(player)) return false;

        if (applyEffect(player)) {
            setNeedsSource(true);
            return true;
        }
        return false;
    }

    public boolean applyEffect(ServerPlayer player) {
        return applyEffect(player, true);
    }

    public boolean applyEffect(ServerPlayer player, boolean visible) {
        return player.addEffect(new MobEffectInstance(getEffect(), getDuration(), 0, false, visible));
    }
}
