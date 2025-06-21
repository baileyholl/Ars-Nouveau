package com.hollingsworth.arsnouveau.common.mixin;

import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Pufferfish;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Pufferfish.class)
public interface PufferfishAccessor {
    @Accessor
    int getInflateCounter();

    @Accessor
    void setInflateCounter(int inflateCounter);

    @Accessor
    int getDeflateTimer();

    @Accessor
    void setDeflateTimer(int deflateTimer);

    @Accessor("targetingConditions")
    static TargetingConditions targetConditions() {
        throw new AssertionError();
    }

}
