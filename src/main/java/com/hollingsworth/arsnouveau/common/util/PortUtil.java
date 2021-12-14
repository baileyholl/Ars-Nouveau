package com.hollingsworth.arsnouveau.common.util;

import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketNoSpamChatMessage;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class PortUtil {
    public static void sendMessage(Entity playerEntity, Component component){
        if(playerEntity == null)
            return;
        playerEntity.sendMessage(component, Util.NIL_UUID);
    }

    public static void sendMessageNoSpam(Entity playerEntity, Component component){
        if (playerEntity instanceof Player) {
            Networking.sendToPlayer(new PacketNoSpamChatMessage(component, 0, false), (Player) playerEntity);
        }
    }

    public static void sendMessageCenterScreen(Entity playerEntity, Component component){
        if (playerEntity instanceof Player) {
            Networking.sendToPlayer(new PacketNoSpamChatMessage(component, 0, true), (Player) playerEntity);
        }
    }

    @Deprecated
    public static void sendMessage(Entity playerEntity, String message){
        sendMessage(playerEntity, new TextComponent(message));
    }
}
