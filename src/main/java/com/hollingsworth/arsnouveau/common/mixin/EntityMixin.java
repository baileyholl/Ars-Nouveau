package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.api.event.EntityPreRemovalEvent;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.Color;
import com.hollingsworth.arsnouveau.common.entity.BubbleEntity;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectLight;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public Level level;

    @Shadow
    public abstract boolean isCurrentlyGlowing();

    @ModifyReturnValue(method = "isInWaterOrRain", at = @At("RETURN"))
    private boolean ars_nouveau$isInWaterOrRain(boolean original) {
        if (((Entity) (Object) this) instanceof LivingEntity livingEntity && isWet(livingEntity)) {
            return true;
        }
        return original;
    }

    @Inject(method = "setRemainingFireTicks", at = @At("HEAD"), cancellable = true)
    private void ars_nouveau$setSecondsOnFire(int pRemainingFireTicks, CallbackInfo ci) {
        if (((Entity) (Object) this) instanceof LivingEntity livingEntity && isWet(livingEntity) && pRemainingFireTicks > 0) {
            livingEntity.clearFire();
            ci.cancel();
        }
    }

    @Inject(method = "setRemoved", at = @At("HEAD"))
    private void onRemoval(Entity.RemovalReason removalReason, CallbackInfo ci) {
        NeoForge.EVENT_BUS.post(new EntityPreRemovalEvent(this.level, (Entity) (Object) this));
    }

    private static boolean isWet(LivingEntity livingEntity){
        return livingEntity.hasEffect(ModPotions.SOAKED_EFFECT) || livingEntity.getVehicle() instanceof BubbleEntity;
    }

    /**
     * Used to make the glowing effect on mobs use the tag applied by {@link EffectLight}.
     */
    @Inject(method = "getTeamColor", at = @At("RETURN"), cancellable = true)
    public void ars_nouveau$getTeamColor(CallbackInfoReturnable<Integer> cir) {
        int color = cir.getReturnValue();
        if (color == 16777215 && isCurrentlyGlowing()) {
            if (((Object) this) instanceof LivingEntity livingEntity) {
                var perData = livingEntity.getPersistentData();
                if (perData.contains("ars_nouveau:glow_color")) {
                    color = perData.getInt("ars_nouveau:glow_color");
                    if (color < 0) {
                        color = Color.rainbowColor(ClientInfo.ticksInGame).getRGB();
                    }
                    cir.setReturnValue(color);
                }
            }

        }
    }
}
