package com.hollingsworth.arsnouveau.common.mixin.shoulder;

import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class ShoulderPlayerMixin {

    @Inject(method = "removeEntitiesOnShoulder", at = @At("TAIL"))
    private void an_removeEntityOnShoulder(CallbackInfo ci) {
        FamiliarEntity.FAMILIAR_SHOULDER_SET.removeIf(e -> e.getOwner() == (Object) this);
    }

}
