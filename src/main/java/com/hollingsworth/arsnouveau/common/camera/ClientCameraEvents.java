package com.hollingsworth.arsnouveau.common.camera;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ScryBot;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketMoveScryBot;
import com.hollingsworth.arsnouveau.common.util.CameraUtil;
import com.hollingsworth.arsnouveau.common.util.ClientCameraUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;


@EventBusSubscriber(modid = ArsNouveau.MODID, value = Dist.CLIENT)
public class ClientCameraEvents {

    @SubscribeEvent
    public static void renderHandEvent(RenderHandEvent event) {
        if (ClientCameraUtil.isPlayerMountedOnCamera())
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onClickInput(InputEvent.InteractionKeyMappingTriggered event) {
        if (ClientCameraUtil.isPlayerMountedOnCamera()) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }

    public static final  ResourceLocation[] overlays = new ResourceLocation[]{VanillaGuiLayers.JUMP_METER, VanillaGuiLayers.EXPERIENCE_BAR, VanillaGuiLayers.EFFECTS};

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onGuiOpen(RenderGuiLayerEvent.Pre event) {
        if(ClientCameraUtil.isPlayerMountedOnCamera()){
            for(ResourceLocation overlay : overlays){
                if(event.getName().equals(overlay)){
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void inputsUpdated(MovementInputUpdateEvent event) {
        if(CameraUtil.getCamera(event.getEntity()) instanceof ScryBot scryBot){
            Input input = event.getInput();
            PacketMoveScryBot moveScryBot = new PacketMoveScryBot(
                    scryBot.getId(),
                    input.leftImpulse,
                    input.forwardImpulse,
                    input.up,
                    input.down,
                    input.left,
                    input.right,
                    input.jumping,
                    input.shiftKeyDown,
                    Minecraft.getInstance().player.xRot,
                    Minecraft.getInstance().player.yRot
            );
            scryBot.onMove(moveScryBot);
            Networking.sendToServer(moveScryBot);
        }
    }
}
