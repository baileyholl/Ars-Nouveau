package com.hollingsworth.arsnouveau.common.mixin.rewind;

import com.hollingsworth.arsnouveau.common.entity.debug.FixedStack;
import com.hollingsworth.arsnouveau.common.event.timed.IRewindable;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectRewind;
import com.hollingsworth.arsnouveau.common.spell.rewind.RewindEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

    @Shadow public Level level;
    @Unique
    public Stack<RewindEntityData> ars_Nouveau$motions = null;

    @Unique
    public boolean an_isRewinding = false;

    @Inject(method = "baseTick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        // Prevent other mods from early loading Entity and causing the config to throw
        if(ars_Nouveau$motions == null){
            ars_Nouveau$motions = new FixedStack<>(EffectRewind.INSTANCE.getEntityMaxTrackingTicks());
        }
        Entity entity = (Entity) (Object) this;
        if(!EffectRewind.shouldRecordData(entity, this) || level == null) {
            return;
        }
        float health = 0;
        if(entity instanceof LivingEntity living){
            health = living.getHealth();
        }
        RewindEntityData data = new RewindEntityData(level.getGameTime(), getDeltaMovement(), this.position(), health);
        ars_Nouveau$motions.push(data);
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
    public Stack<RewindEntityData> getMotions() {
        return ars_Nouveau$motions == null ? new FixedStack<>(0) : ars_Nouveau$motions;
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
