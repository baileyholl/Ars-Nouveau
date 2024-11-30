package com.hollingsworth.arsnouveau.common.mixin.elytra;


import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.entity.ScryBot;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectGlide;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class ClientElytraMixin {

    @ModifyExpressionValue(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;canElytraFly(Lnet/minecraft/world/entity/LivingEntity;)Z",
                    remap = false
            )
    )
    public boolean elytraOverride(boolean original) {
        return original || EffectGlide.canGlide(((LivingEntity) ((Object)this)));
    }

    @Inject(method="tick", at=@At("HEAD"))
    public void ars$_tick(CallbackInfo info) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        if(player.level.getEntity(ClientInfo.scryBotId) instanceof ScryBot scryBot){
            player.connection.send(new ServerboundMoveVehiclePacket(scryBot));
        }
    }
}