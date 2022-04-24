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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID, value = Dist.CLIENT)
public class CameraController {
	public static boolean jumpBarElementEnabledPreviously;
	public static boolean experienceBarElementEnabledPreviously;
	public static boolean potionIconsElementEnabledPreviously;
	public static CameraType previousCameraType;
	public static boolean resetOverlaysAfterDismount = false;
	private static ClientChunkCache.Storage cameraStorage;
	private static boolean wasUpPressed;
	private static boolean wasDownPressed;
	private static boolean wasLeftPressed;
	private static boolean wasRightPressed;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		Entity cameraEntity = Minecraft.getInstance().cameraEntity;

		if (cameraEntity instanceof ScryerCamera cam) {
			Options options = Minecraft.getInstance().options;

			//up/down/left/right handling is split to prevent players who are viewing a camera from moving around in a boat or on a horse
			if (event.phase == TickEvent.Phase.START) {
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
			else if (event.phase == TickEvent.Phase.END) {
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
					player.connection.send(new ServerboundMovePlayerPacket.Rot(player.getYRot(), player.getXRot(), player.isOnGround()));
			}
		}
		else if (resetOverlaysAfterDismount) {
			resetOverlaysAfterDismount = false;
//			OverlayRegistry.enableOverlay(ClientHandler.cameraOverlay, false);
//			OverlayRegistry.enableOverlay(ClientHandler.hotbarBindOverlay, true);
			CameraController.restoreOverlayStates();
		}
	}

	private static void dismount() {
		Networking.INSTANCE.sendToServer(new PacketDismountCamera());
	}

	public static void moveViewUp(ScryerCamera cam) {
		float next = cam.getXRot() - (float) cam.cameraSpeed * cam.zoomAmount;

		if (cam.isCameraDown()) {
			if (next > 40F)
				cam.setRotation(cam.getYRot(), next);
		}
		else if (next > -25F)
			cam.setRotation(cam.getYRot(), next);
	}

	public static void moveViewDown(ScryerCamera cam) {
		float next = cam.getXRot() + (float) cam.cameraSpeed * cam.zoomAmount;

		if (cam.isCameraDown()) {
			if (next < 90F)
				cam.setRotation(cam.getYRot(), next);
		}
		else if (next < 60F)
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

	public static void zoomIn(ScryerCamera cam) {
//		if (!cam.zooming)
//			Minecraft.getInstance().level.playLocalSound(cam.blockPosition(), SCSounds.CAMERAZOOMIN.event, SoundSource.BLOCKS, 1.0F, 1.0F, true);

		cam.zooming = true;
		cam.zoomAmount = Math.max(cam.zoomAmount - 0.1F, 0.1F);
	}

	public static void zoomOut(ScryerCamera cam) {
//		if (!cam.zooming)
//			Minecraft.getInstance().level.playLocalSound(cam.blockPosition(), SCSounds.CAMERAZOOMIN.event, SoundSource.BLOCKS, 1.0F, 1.0F, true);

		cam.zooming = true;
		cam.zoomAmount = Math.min(cam.zoomAmount + 0.1F, 1.4F);
	}

//	public static void emitRedstone(ScryerCamera cam) {
//		if (cam.redstoneCooldown == 0) {
//			cam.toggleRedstonePower();
//			cam.redstoneCooldown = 30;
//		}
//	}
//
//	public static void giveNightVision(ScryerCamera cam) {
//		if (cam.toggleNightVisionCooldown == 0)
//			cam.toggleNightVision();
//	}

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

	public static void saveOverlayStates() {
		jumpBarElementEnabledPreviously = OverlayRegistry.getEntry(ForgeIngameGui.JUMP_BAR_ELEMENT).isEnabled();
		experienceBarElementEnabledPreviously = OverlayRegistry.getEntry(ForgeIngameGui.EXPERIENCE_BAR_ELEMENT).isEnabled();
		potionIconsElementEnabledPreviously = OverlayRegistry.getEntry(ForgeIngameGui.POTION_ICONS_ELEMENT).isEnabled();
	}

	public static void restoreOverlayStates() {
		if (jumpBarElementEnabledPreviously)
			OverlayRegistry.enableOverlay(ForgeIngameGui.JUMP_BAR_ELEMENT, true);

		if (experienceBarElementEnabledPreviously)
			OverlayRegistry.enableOverlay(ForgeIngameGui.EXPERIENCE_BAR_ELEMENT, true);

		if (potionIconsElementEnabledPreviously)
			OverlayRegistry.enableOverlay(ForgeIngameGui.POTION_ICONS_ELEMENT, true);
	}
}