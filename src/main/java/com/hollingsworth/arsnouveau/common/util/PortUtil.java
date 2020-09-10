package com.hollingsworth.arsnouveau.common.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class PortUtil {
    public static void sendMessage(PlayerEntity playerEntity, ITextComponent component){
        playerEntity.sendMessage(component, Util.DUMMY_UUID);
    }

    public static void sendMessage(PlayerEntity playerEntity, String message){
        sendMessage(playerEntity, new StringTextComponent(message));
    }
}
