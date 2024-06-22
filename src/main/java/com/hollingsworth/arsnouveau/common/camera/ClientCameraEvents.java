package com.hollingsworth.arsnouveau.common.camera;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.util.ClientCameraUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;


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

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onGuiOpen(RenderGuiOverlayEvent.Pre event) {
        VanillaGuiOverlay[] overlays = new VanillaGuiOverlay[]{VanillaGuiOverlay.JUMP_BAR,VanillaGuiOverlay.EXPERIENCE_BAR, VanillaGuiOverlay.POTION_ICONS};
        if(ClientCameraUtil.isPlayerMountedOnCamera()){
            for(VanillaGuiOverlay overlay : overlays){
                if(event.getOverlay() == overlay.type()){
                    event.setCanceled(true);
                }
            }
        }
    }
}
