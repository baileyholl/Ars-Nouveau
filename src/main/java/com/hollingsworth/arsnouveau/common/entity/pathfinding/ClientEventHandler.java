package com.hollingsworth.arsnouveau.common.entity.pathfinding;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Used to handle client events.
 */
@OnlyIn(Dist.CLIENT)
public class ClientEventHandler {
    /**
     * Used to catch the renderWorldLastEvent in order to draw the debug nodes for pathfinding.
     *
     * @param event the catched event.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderWorldLastEvent(final RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
            ClientInfo.partialTicks = event.getPartialTick();
            LightManager.updateAll(event.getLevelRenderer());
        }
    }

    @SubscribeEvent
    public static void clientPlayerLogin(ClientPlayerNetworkEvent.LoggingIn e) {
        if(e.getPlayer() != null){
            if(Config.INFORM_LIGHTS.get()){
                Player entity = e.getPlayer();
                PortUtil.sendMessage(entity, Component.translatable("ars_nouveau.light_message").withStyle(ChatFormatting.GOLD));
                Config.INFORM_LIGHTS.set(false);
                Config.INFORM_LIGHTS.save();
            }
        }
    }
}
