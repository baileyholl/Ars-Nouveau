package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.camera.CameraController;
import com.hollingsworth.arsnouveau.common.entity.ScryerCamera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

// https://github.com/Geforce132/SecurityCraft/blob/1.18.2/src/main/java/net/geforcemods/securitycraft/network/client/SetCameraView.java
public class PacketSetCameraView extends AbstractPacket {
    public static final Type<PacketSetCameraView> TYPE = new Type<>(ArsNouveau.prefix("set_camera_view"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetCameraView> CODEC = StreamCodec.ofMember(PacketSetCameraView::toBytes, PacketSetCameraView::new);
    private int id;

    public PacketSetCameraView() {
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(id);
    }

    public PacketSetCameraView(Entity camera) {
        id = camera.getId();
    }

    public PacketSetCameraView(RegistryFriendlyByteBuf buf) {
        id = buf.readVarInt();
    }

    @Override
    public void onClientReceived(Minecraft mc, Player player) {
        Entity entity = mc.level.getEntity(id);
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
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
