package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class PacketSyncTag extends AbstractPacket{

    public CompoundTag tag;
    public int entityId;

    public PacketSyncTag(CompoundTag tag, int entityId){
        this.tag = tag;
        this.entityId = entityId;
    }

    public static PacketSyncTag decode(RegistryFriendlyByteBuf buf) {
        return new PacketSyncTag(buf.readNbt(), buf.readInt());
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(tag);
        buf.writeInt(entityId);
    }

    @Override
    public void onClientReceived(Minecraft mc, Player player) {
        ClientLevel world = mc.level;
        if (world.getEntity(entityId) instanceof ITagSyncable tagSyncable) {
            tagSyncable.onTagSync(tag);
        }
    }

    public static final Type<PacketSyncTag> TYPE = new Type<>(ArsNouveau.prefix("sync_tag"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSyncTag> CODEC = StreamCodec.ofMember(PacketSyncTag::toBytes, PacketSyncTag::decode);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
