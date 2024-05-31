package com.hollingsworth.arsnouveau.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketSyncTag {

    public CompoundTag tag;
    public int entityId;

    public PacketSyncTag(CompoundTag tag, int entityId){
        this.tag = tag;
        this.entityId = entityId;
    }

    public static PacketSyncTag decode(FriendlyByteBuf buf) {
        return new PacketSyncTag(buf.readNbt(), buf.readInt());
    }

    public static void encode(PacketSyncTag msg, FriendlyByteBuf buf) {
        buf.writeNbt(msg.tag);
        buf.writeInt(msg.entityId);
    }

    public static class Handler {
        public static void handle(final PacketSyncTag m, final Supplier<NetworkEvent.Context> ctx) {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ctx.get().setPacketHandled(true);
                return;
            }

            ctx.get().enqueueWork(new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    Minecraft mc = Minecraft.getInstance();
                    ClientLevel world = mc.level;
                    if (world.getEntity(m.entityId) instanceof ITagSyncable tagSyncable) {
                        tagSyncable.onTagSync(m.tag);
                    }
                }
            });
            ctx.get().setPacketHandled(true);

        }
    }
}
