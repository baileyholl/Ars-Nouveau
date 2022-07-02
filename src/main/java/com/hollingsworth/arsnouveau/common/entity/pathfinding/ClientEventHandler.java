package com.hollingsworth.arsnouveau.common.entity.pathfinding;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.hollingsworth.arsnouveau.common.entity.pathfinding.pathjobs.AbstractPathJob.DEBUG_DRAW;

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
    public static void renderWorldLastEvent(final RenderLevelLastEvent event) {
        ClientInfo.partialTicks = event.getPartialTick();
        if (DEBUG_DRAW) {
            Pathfinding.debugDraw(event.getPartialTick(), event.getPoseStack());
        }
        LightManager.updateAll(event.getLevelRenderer());
    }
}
