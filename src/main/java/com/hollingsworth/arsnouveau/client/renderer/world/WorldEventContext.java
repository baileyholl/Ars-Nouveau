package com.hollingsworth.arsnouveau.client.renderer.world;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/**
 * Main class for handling world rendering.
 * Also holds all possible values which may be needed during rendering.
 */
public class WorldEventContext
{
    public static final WorldEventContext INSTANCE = new WorldEventContext();

//    public static MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(new BufferBuilder(256));
    private WorldEventContext()
    {
        // singleton
    }

    PoseStack poseStack;
    float partialTicks;
    ClientLevel clientLevel;
    LocalPlayer clientPlayer;
    ItemStack mainHandItem;


    /**
     * In chunks
     */
    int clientRenderDist;

    public void renderWorldLastEvent(final RenderLevelStageEvent event)
    {
        poseStack = event.getPoseStack();
//        partialTicks = event.getPartialTick();
//        clientLevel = Minecraft.getInstance().level;
//        clientPlayer = Minecraft.getInstance().player;
//        mainHandItem = clientPlayer.getMainHandItem();
//        clientRenderDist = Minecraft.getInstance().options.renderDistance().get();
//
//        final Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
//        poseStack.pushPose();
//        poseStack.translate(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());
//
//        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS)
//        {
//            PathfindingDebugRenderer.render(this);
//
//            bufferSource.endBatch();
//        }
//        else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS)
//        {
//            bufferSource.endBatch();
//        }
//
//        poseStack.popPose();
    }
}
