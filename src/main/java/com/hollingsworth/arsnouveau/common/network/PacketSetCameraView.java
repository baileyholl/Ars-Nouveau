package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.common.camera.CameraController;
import com.hollingsworth.arsnouveau.common.entity.ScryerCamera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

// https://github.com/Geforce132/SecurityCraft/blob/1.18.2/src/main/java/net/geforcemods/securitycraft/network/client/SetCameraView.java
public class PacketSetCameraView {
    private int id;

    public PacketSetCameraView() {
    }

    public PacketSetCameraView(Entity camera) {
        id = camera.getId();
    }

    public static void encode(PacketSetCameraView message, FriendlyByteBuf buf) {
        buf.writeVarInt(message.id);
    }

    public static PacketSetCameraView decode(FriendlyByteBuf buf) {
        PacketSetCameraView message = new PacketSetCameraView();

        message.id = buf.readVarInt();
        return message;
    }

    public static void onMessage(PacketSetCameraView message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Entity entity = mc.level.getEntity(message.id);
            boolean isCamera = entity instanceof ScryerCamera;

            if (isCamera || entity instanceof Player) {
                mc.setCameraEntity(entity);

                if (isCamera) {
                    CameraController.previousCameraType = mc.options.getCameraType();
                    mc.options.setCameraType(CameraType.FIRST_PERSON);
                    CameraController.setRenderPosition(entity);
                } else if (CameraController.previousCameraType != null)
                    mc.options.setCameraType(CameraController.previousCameraType);

                mc.levelRenderer.allChanged();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
