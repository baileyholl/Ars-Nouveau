package com.hollingsworth.arsnouveau.common.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.play.client.CEntityActionPacket;

public class GlideClientHandler {

    public static void tick(LivingEntity entity){
        ClientPlayerEntity playerEntity = (ClientPlayerEntity) entity;
        boolean flag1 = playerEntity.input.shiftKeyDown;
        boolean flag = playerEntity.input.jumping;
        boolean flag7 = false;
        boolean flag3 = false;
        if (playerEntity.autoJumpTime > 0) {
            flag3 = true;

        }
        if (playerEntity.abilities.mayfly) {
            if (Minecraft.getInstance().gameMode.isAlwaysFlying()) {
                if (!playerEntity.abilities.flying) {
                    flag7 = true;
                }
            } else if (!flag && playerEntity.input.jumping && !flag3) {
                if (!playerEntity.isSwimming()) {
                    flag7 = true;
                }
            }
        }
        if (playerEntity.input.jumping && !flag7 && !flag && !playerEntity.abilities.flying && !playerEntity.isPassenger() && !playerEntity.onClimbable()) {
            playerEntity.connection.send(new CEntityActionPacket(playerEntity, CEntityActionPacket.Action.START_FALL_FLYING));

        }
    }
}
