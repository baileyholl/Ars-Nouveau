package com.hollingsworth.arsnouveau.common.camera;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.ScryerCrystal;
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
import net.minecraft.world.level.block.state.BlockState;
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
    public static void onClientTick(ClientTickEvent event) {
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
            } else if (event instanceof ClientTickEvent.Post) {
                if (wasUpPressed) {
                    moveViewUp(cam);
                    options.keyUp.setDown(true);
                }

                if (wasDownPressed) {
                    moveViewDown(cam);
                    options.keyDown.setDown(true);
                }

                if (wasLeftPressed) {
                    moveViewHorizontally(cam, cam.getYRot(), cam.getYRot() - (float) cam.cameraSpeed * cam.zoomAmount);
                    options.keyLeft.setDown(true);
                }

                if (wasRightPressed) {
                    moveViewHorizontally(cam, cam.getYRot(), cam.getYRot() + (float) cam.cameraSpeed * cam.zoomAmount);
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

    public static void moveViewUp(ScryerCamera cam) {
        float next = cam.getXRot() - (float) cam.cameraSpeed * cam.zoomAmount;

        if (cam.isCameraDown()) {
            if (next > 40F)
                cam.setRotation(cam.getYRot(), next);
        } else if (next > -25F)
            cam.setRotation(cam.getYRot(), next);
    }

    public static void moveViewDown(ScryerCamera cam) {
        float next = cam.getXRot() + (float) cam.cameraSpeed * cam.zoomAmount;

        if (cam.isCameraDown()) {
            if (next < 90F)
                cam.setRotation(cam.getYRot(), next);
        } else if (next < 60F)
            cam.setRotation(cam.getYRot(), next);
    }

    public static void moveViewHorizontally(ScryerCamera cam, float yRot, float next) {
        BlockState state = cam.level.getBlockState(cam.blockPosition());

        if (state.hasProperty(ScryerCrystal.FACING)) {
            float checkNext = next;

            if (checkNext < 0)
                checkNext += 360;

            boolean shouldSetRotation = switch (state.getValue(ScryerCrystal.FACING)) {
                case NORTH -> checkNext > 90F && checkNext < 270F;
                case SOUTH -> checkNext > 270F || checkNext < 90F;
                case EAST -> checkNext > 180F && checkNext < 360F;
                case WEST -> checkNext > 0F && checkNext < 180F;
                case DOWN -> true;
                default -> false;
            };

            if (shouldSetRotation)
                cam.setYRot(next);
        }
    }

    public static ClientChunkCache.Storage getCameraStorage() {
        return cameraStorage;
    }

    public static void setCameraStorage(ClientChunkCache.Storage cameraStorage) {
        if (cameraStorage != null)
            CameraController.cameraStorage = cameraStorage;
    }

    public static void setRenderPosition(Entity entity) {
        if (entity instanceof ScryerCamera) {
            SectionPos cameraPos = SectionPos.of(entity);

            cameraStorage.viewCenterX = cameraPos.x();
            cameraStorage.viewCenterZ = cameraPos.z();
        }
    }
}