package com.hollingsworth.arsnouveau.api.ritual;

import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.ModConfigSpec;

import javax.annotation.Nullable;

public abstract class RangeEffectRitual extends RangeRitual {

    abstract public Holder<MobEffect> getEffect();
    abstract public int getRange();
    abstract public int getDuration();

    public @Nullable ModConfigSpec.IntValue RANGE;
    public @Nullable ModConfigSpec.IntValue DURATION;

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        RANGE = builder.comment("Range (in blocks)").defineInRange("range", getRange(), 1, 128);
        DURATION = builder.comment("Duration (in ticks)").defineInRange("duration", getDuration(), 1, 128);
    }

    public boolean shouldApply(ServerPlayer player) {
        return !player.level.isClientSide && !needsSourceNow() && BlockUtil.distanceFrom(getPos(), player.blockPosition()) <= RANGE.get();
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
        player.addEffect(new MobEffectInstance(getEffect(), DURATION.get()));
        return true;
    }
}
