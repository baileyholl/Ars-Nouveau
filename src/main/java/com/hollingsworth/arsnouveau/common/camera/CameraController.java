package com.hollingsworth.arsnouveau.common.camera;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ICameraCallback;
import com.hollingsworth.arsnouveau.common.entity.ScryerCamera;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketDismountCamera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = ArsNouveau.MODID, value = Dist.CLIENT)
public class CameraController {
    public static CameraType previousCameraType;
    public static boolean resetOverlaysAfterDismount = false;
    private static ClientChunkCache.Storage cameraStorage;
    private static boolean wasUpPressed;
    private static boolean wasDownPressed;
    private static boolean wasLeftPressed;
    private static boolean wasRightPressed;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Entity cameraEntity = Minecraft.getInstance().cameraEntity;

        if (cameraEntity instanceof ScryerCamera cam) {
            Options options = Minecraft.getInstance().options;

            //up/down/left/right handling is split to prevent players who are viewing a camera from moving around in a boat or on a horse
            if (event instanceof ClientTickEvent.Pre) {
                if (wasUpPressed = options.keyUp.isDown())
                    options.keyUp.setDown(false);

                if (wasDownPressed = options.keyDown.isDown())
                    options.keyDown.setDown(false);

                if (wasLeftPressed = options.keyLeft.isDown())
                    options.keyLeft.setDown(false);

                if (wasRightPressed = options.keyRight.isDown())
                    options.keyRight.setDown(false);

                if (options.keyShift.isDown()) {
                    dismount();
                    options.keyShift.setDown(false);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Entity cameraEntity = Minecraft.getInstance().cameraEntity;

        if (cameraEntity instanceof ScryerCamera cam) {
            Options options = Minecraft.getInstance().options;

            //up/down/left/right handling is split to prevent players who are viewing a camera from moving around in a boat or on a horse
            if (event instanceof ClientTickEvent.Post) {
                if (wasUpPressed) {
                    cam.onUpPressed();
                    options.keyUp.setDown(true);
                }

                if (wasDownPressed) {
                    cam.onDownPressed();
                    options.keyDown.setDown(true);
                }

                if (wasLeftPressed) {
                    cam.onLeftPressed();
                    options.keyLeft.setDown(true);
                }

                if (wasRightPressed) {
                    cam.onRightPressed();
                    options.keyRight.setDown(true);
                }

                //update other players with the head rotation
                LocalPlayer player = Minecraft.getInstance().player;
                double yRotChange = player.getYRot() - player.yRotLast;
                double xRotChange = player.getXRot() - player.xRotLast;

                if (yRotChange != 0.0D || xRotChange != 0.0D)
                    player.connection.send(new ServerboundMovePlayerPacket.Rot(player.getYRot(), player.getXRot(), player.onGround()));
            }
        }
    }

    private static void dismount() {
        Networking.sendToServer(new PacketDismountCamera());
    }


    public static ClientChunkCache.Storage getCameraStorage() {
        return cameraStorage;
    }

    public static void setCameraStorage(ClientChunkCache.Storage cameraStorage) {
        if (cameraStorage != null)
            CameraController.cameraStorage = cameraStorage;
    }

    public static void setRenderPosition(Entity entity) {
        if (entity instanceof ICameraCallback) {
            SectionPos cameraPos = SectionPos.of(entity);

            cameraStorage.viewCenterX = cameraPos.x();
            cameraStorage.viewCenterZ = cameraPos.z();
        }
    }
}