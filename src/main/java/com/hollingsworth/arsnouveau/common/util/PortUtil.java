package com.hollingsworth.arsnouveau.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class PortUtil {
    public static void sendMessage(Entity playerEntity, ITextComponent component){
        playerEntity.sendMessage(component, Util.NIL_UUID);
    }

    public static void sendMessage(Entity playerEntity, String message){
        sendMessage(playerEntity, new StringTextComponent(message));
    }
}
