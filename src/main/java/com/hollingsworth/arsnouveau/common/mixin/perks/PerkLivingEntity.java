package com.hollingsworth.arsnouveau.common.mixin.perks;

import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class PerkLivingEntity {

    @Inject(method = "decreaseAirSupply", at = @At("HEAD"), cancellable = true)
    protected void decreaseAirSupply(int pCurrentAir, CallbackInfoReturnable<Integer> cir) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;
        AttributeInstance instance = thisEntity.getAttribute(PerkAttributes.DEPTHS.get());
        if(instance != null && thisEntity.getRandom().nextDouble() <= instance.getValue()) {
            cir.setReturnValue(thisEntity.getAirSupply());
        }
    }

    @Inject(method = "getJumpBoostPower", at = @At("RETURN"), cancellable = true)
    protected void getJumpPower(CallbackInfoReturnable<Double> cir) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;
        double power = PerkUtil.perkValue(thisEntity, PerkAttributes.JUMP_HEIGHT.get());
        cir.setReturnValue(cir.getReturnValueD() + power);
    }
}
