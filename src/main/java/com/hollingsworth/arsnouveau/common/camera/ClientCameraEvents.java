package com.hollingsworth.arsnouveau.common.camera;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.util.ClientCameraUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID, value = Dist.CLIENT)
public class ClientCameraEvents {

    @SubscribeEvent
    public static void renderHandEvent(RenderHandEvent event) {
        if (ClientCameraUtil.isPlayerMountedOnCamera())
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onClickInput(InputEvent.ClickInputEvent event) {
        if (ClientCameraUtil.isPlayerMountedOnCamera()) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }
}
