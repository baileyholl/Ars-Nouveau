package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.common.entity.BehaviorRegistry;
import com.hollingsworth.arsnouveau.common.entity.IBehaviorSyncable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncBehaviorData {

    public CompoundTag tag;
    public int id;

    public PacketSyncBehaviorData(Entity entity, CompoundTag tag) {
        this.id = entity.getId();
        this.tag = tag;
    }

    public PacketSyncBehaviorData(FriendlyByteBuf buf) {
        buf.writeInt(id);
        buf.writeNbt(tag);
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        id = buf.readInt();
        tag = buf.readNbt();
    }

    public static class Handler {
        public static void handle(final PacketSyncBehaviorData m, final Supplier<NetworkEvent.Context> ctx) {
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
                    if (world.getEntity(m.id) != null && world.getEntity(m.id) instanceof IBehaviorSyncable behaviorSyncable) {
                        behaviorSyncable.setBehavior(BehaviorRegistry.create((Entity) behaviorSyncable, m.tag));
                    }
                }
            });
            ctx.get().setPacketHandled(true);

        }
    }
}
