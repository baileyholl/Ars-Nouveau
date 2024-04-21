package com.hollingsworth.arsnouveau.common.mixin.rewind;

import com.hollingsworth.arsnouveau.common.entity.debug.FixedStack;
import com.hollingsworth.arsnouveau.common.event.timed.IRewindable;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectRewind;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Stack;

@Mixin(Entity.class)
public abstract class RewindEntityMixin implements IRewindable {

    @Shadow
    public abstract Vec3 getDeltaMovement();


    @Shadow
    public abstract Level level();


    @Shadow public abstract void remove(Entity.RemovalReason pReason);

    @Shadow public abstract boolean removeTag(String pTag);

    @Shadow public abstract Vec3 position();

    @Unique
    public Stack<EffectRewind.Data> motions = new FixedStack<>(100);

    @Unique
    public boolean an_isRewinding = false;



    @Inject(method = "baseTick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        if(this.level().isClientSide)
            return;
        if (isRewinding()) {
            return;
        }
        EffectRewind.Data data = new EffectRewind.Data(getDeltaMovement(), this.position());
        motions.push(data);
    }


    @Inject(method = "setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"), cancellable = true)
    protected void anSetDeltaMovement(Vec3 pDeltaMovement, CallbackInfo ci) {
        if(!EffectRewind.shouldAllowMovement(this)){
            ci.cancel();
        }
    }

    @Inject(method = "setDeltaMovement(DDD)V", at = @At("HEAD"), cancellable = true)
    protected void anSetDeltaMovement(double pX, double pY, double pZ, CallbackInfo ci) {
        if(!EffectRewind.shouldAllowMovement(this)){
            ci.cancel();
        }
    }

    @Override
    public Stack<EffectRewind.Data> getMotions() {
        return motions;
    }

    @Override
    public void setRewinding(boolean rewinding) {
        an_isRewinding = rewinding;
    }

    @Override
    public boolean isRewinding() {
        return an_isRewinding;
    }
}
