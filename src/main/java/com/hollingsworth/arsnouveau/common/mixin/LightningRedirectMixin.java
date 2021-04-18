package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.common.entity.LightningEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Entity.class)
public class LightningRedirectMixin {

    @ModifyConstant(method = "thunderHit", constant = @Constant(floatValue = 5.0F), remap = true)
    public float hookLightningDamage(float original, ServerWorld level, LightningBoltEntity bolt) {
        if(bolt instanceof LightningEntity) {
            return ((LightningEntity)bolt).getDamage((Entity)((Object)this));
        }
        return original;
    }
}
