package com.hollingsworth.arsnouveau.common.camera;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.camera.ICameraMountable;
import com.hollingsworth.arsnouveau.common.entity.ICameraCallback;
import com.hollingsworth.arsnouveau.common.util.CameraUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;

// https://github.com/Geforce132/SecurityCraft/blob/fbef0c9d3f5959f09f2f0a1a351a9a86604566ed/src/main/java/net/geforcemods/securitycraft/SCEventHandler.java
@EventBusSubscriber(modid = ArsNouveau.MODID)
public class CameraEvents {

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();

        if (player.getCamera() instanceof ICameraCallback cam) {
            if (player.level.getBlockEntity(cam.entity().blockPosition()) instanceof ICameraMountable camBe)
                camBe.stopViewing();

            cam.stopViewing(player);
        }
    }

    @SubscribeEvent
    public static void onDamageTaken(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level;

        if (!level.isClientSide && entity instanceof ServerPlayer player && player.camera instanceof ICameraCallback camera)
            camera.stopViewing(player);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (CameraUtil.isPlayerMountedOnCamera(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(LeftClickBlock event) {
        if (CameraUtil.isPlayerMountedOnCamera(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (CameraUtil.isPlayerMountedOnCamera(event.getEntity()))
            event.setCanceled(true);
    }

}
