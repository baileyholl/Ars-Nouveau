package com.hollingsworth.arsnouveau.common.mixin.shoulder;

import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class ShoulderPlayerMixin {

    @Shadow
    public abstract CompoundTag getShoulderEntityLeft();

    @Shadow
    public abstract CompoundTag getShoulderEntityRight();

    @Inject(method = "removeEntitiesOnShoulder", at = @At("TAIL"), cancellable = true)
    private void an_removeEntityOnShoulder(CallbackInfo ci) {

        if ((Object) this instanceof Player player) {
            CompoundTag leftShoulder = getShoulderEntityLeft(), rightShoulder = getShoulderEntityRight();
            if (leftShoulder != null) {
                if (leftShoulder.getBoolean("familiar") && !player.isShiftKeyDown()) {
                    ci.cancel();
                }
            }
            if (rightShoulder != null) {
                if (rightShoulder.getBoolean("familiar") && !player.isShiftKeyDown()) {
                    ci.cancel();
                }
            }
        }

        FamiliarEntity.FAMILIAR_SHOULDER_SET.removeIf(e -> e.getOwner() == (Object) this);
    }

}
