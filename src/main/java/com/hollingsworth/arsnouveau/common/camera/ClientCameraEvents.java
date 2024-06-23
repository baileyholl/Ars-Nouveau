package com.hollingsworth.arsnouveau.common.camera;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.util.ClientCameraUtil;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
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
}
