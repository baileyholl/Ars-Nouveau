package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class PacketUpdateGlowColor extends AbstractPacket {

    public int entity;
    public int color;

    public PacketUpdateGlowColor(int entity, int color) {
        this.entity = entity;
        this.color = color;
    }

    public void toBytes(RegistryFriendlyByteBuf RegistryFriendlyByteBuf) {
        RegistryFriendlyByteBuf.writeInt(this.entity);
        RegistryFriendlyByteBuf.writeInt(this.color);
    }

    public PacketUpdateGlowColor(RegistryFriendlyByteBuf RegistryFriendlyByteBuf) {
        this.entity = RegistryFriendlyByteBuf.readInt();
        this.color = RegistryFriendlyByteBuf.readInt();
    }

    @Override
    public void onClientReceived(Minecraft mc, Player player) {
        if (player.level.getEntity(this.entity) instanceof LivingEntity living)
            if (color != 0) {
                living.getPersistentData().putInt("ars_nouveau:glow_color", color);
            } else {
                living.getPersistentData().remove("ars_nouveau:glow_color");
            }
    }


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final Type<PacketUpdateGlowColor> TYPE = new Type<>(ArsNouveau.prefix("glow_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateGlowColor> CODEC = StreamCodec
            .ofMember(PacketUpdateGlowColor::toBytes, PacketUpdateGlowColor::new);

}
