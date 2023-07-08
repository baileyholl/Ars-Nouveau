package com.hollingsworth.arsnouveau.common.entity.pathfinding;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
//        WorldEventContext.INSTANCE.renderWorldLastEvent(event);
//        if(event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS){
//            PathfindingDebugRenderer.render(WorldEventContext.INSTANCE);
//            WorldEventContext.bufferSource.endBatch();
//        }
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
            ClientInfo.partialTicks = event.getPartialTick();
//            if (DEBUG_DRAW) {
//                Pathfinding.debugDraw(event.getPartialTick(), event.getPoseStack());
//            }
            LightManager.updateAll(event.getLevelRenderer());
        }
    }
}
