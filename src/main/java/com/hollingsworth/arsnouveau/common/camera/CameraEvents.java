package com.hollingsworth.arsnouveau.common.camera;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.camera.ICameraMountable;
import com.hollingsworth.arsnouveau.client.ClientHandler;
import com.hollingsworth.arsnouveau.common.entity.ScryerCamera;
import com.hollingsworth.arsnouveau.common.util.CameraUtil;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
// https://github.com/Geforce132/SecurityCraft/blob/fbef0c9d3f5959f09f2f0a1a351a9a86604566ed/src/main/java/net/geforcemods/securitycraft/SCEventHandler.java
@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class CameraEvents {

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        ServerPlayer player = (ServerPlayer) event.getPlayer();

        if (player.getCamera() instanceof ScryerCamera cam) {
            if (player.level.getBlockEntity(cam.blockPosition()) instanceof ICameraMountable camBe)
                camBe.stopViewing();

            cam.discard();
        }
    }

    @SubscribeEvent
    public static void onDamageTaken(LivingHurtEvent event) {
        LivingEntity entity = event.getEntityLiving();
        Level level = entity.level;

        if (!level.isClientSide && entity instanceof ServerPlayer player && CameraUtil.isPlayerMountedOnCamera(entity))
            ((ScryerCamera) player.getCamera()).stopViewing(player);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (CameraUtil.isPlayerMountedOnCamera(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(LeftClickBlock event) {
        if (CameraUtil.isPlayerMountedOnCamera(event.getPlayer())) {
            event.setCanceled(true);
            return;
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (CameraUtil.isPlayerMountedOnCamera(event.getPlayer()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onGuiOpen(ScreenOpenEvent event) {
        Screen screen = event.getScreen();
        if (screen instanceof TitleScreen || screen instanceof JoinMultiplayerScreen) {
            OverlayRegistry.enableOverlay(ClientHandler.cameraOverlay, false);
            OverlayRegistry.enableOverlay(ForgeIngameGui.JUMP_BAR_ELEMENT, true);
            OverlayRegistry.enableOverlay(ForgeIngameGui.EXPERIENCE_BAR_ELEMENT, true);
        }

    }

}
