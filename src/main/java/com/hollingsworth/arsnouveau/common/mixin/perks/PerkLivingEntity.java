package com.hollingsworth.arsnouveau.common.mixin.perks;

import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.perk.DepthsPerk;
import com.hollingsworth.arsnouveau.common.perk.JumpHeightPerk;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class PerkLivingEntity extends Entity {

    @Shadow
    public abstract boolean isCurrentlyGlowing();

    @Inject(method = "decreaseAirSupply", at = @At("HEAD"), cancellable = true)
    protected void decreaseAirSupply(int pCurrentAir, CallbackInfoReturnable<Integer> cir) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;
        int numDepths = PerkUtil.countForPerk(DepthsPerk.INSTANCE, thisEntity);
        if (numDepths >= 3 || thisEntity.getRandom().nextDouble() <= numDepths * .33) {
            cir.setReturnValue(thisEntity.getAirSupply());
        }
    }

    @Inject(method = "getJumpBoostPower", at = @At("RETURN"), cancellable = true)
    protected void getJumpPower(CallbackInfoReturnable<Float> cir) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;
        cir.setReturnValue(cir.getReturnValueF() + PerkUtil.countForPerk(JumpHeightPerk.INSTANCE, thisEntity) * 0.1f);
    }

    public PerkLivingEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

}
