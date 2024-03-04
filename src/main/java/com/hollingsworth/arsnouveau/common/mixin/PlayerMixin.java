package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

    @Shadow
    public abstract CompoundTag getShoulderEntityLeft();

    @Shadow
    public abstract CompoundTag getShoulderEntityRight();

    @Inject(at = @At("HEAD"), method = "removeEntitiesOnShoulder", cancellable = true)
    void arsNouveau$removeEntitiesOnShoulder(CallbackInfo ci) {
        if ((Object) this instanceof Player player) {
            CompoundTag leftShoulder = getShoulderEntityLeft(), rightShoulder = getShoulderEntityRight();
            // the alternative to using a list of strings is to recreate the entity from tag and make an instanceof check on MagicalBuddyMob
            if (leftShoulder != null) {
                if (ArsNouveauAPI.shoulderRiders.contains(leftShoulder.getString("id")) && !player.isShiftKeyDown()) {
                    ci.cancel();
                }
            }
            if (rightShoulder != null) {
                if (ArsNouveauAPI.shoulderRiders.contains(rightShoulder.getString("id")) && !player.isShiftKeyDown()) {
                    ci.cancel();
                }
            }
        }
    }

}
