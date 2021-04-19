package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.common.block.tile.IAnimationListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketAnimEntity {

    int entityID;
    int anim;

    public PacketAnimEntity(int entityID){
        this.entityID = entityID;
        this.anim = 0;
    }

    public PacketAnimEntity(int entityID, int anim){
        this.entityID = entityID;
        this.anim = anim;
    }

    public static PacketAnimEntity decode(PacketBuffer buf) {
        return new PacketAnimEntity(buf.readInt(),buf.readInt());
    }

    public static void encode(PacketAnimEntity msg, PacketBuffer buf) {
        buf.writeInt(msg.entityID);
        buf.writeInt(msg.anim);
    }

    public static class Handler {
        public static void handle(final PacketAnimEntity m, final Supplier<NetworkEvent.Context> ctx) {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ctx.get().setPacketHandled(true);
                return;
            }

            ctx.get().enqueueWork(new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    Minecraft mc = Minecraft.getInstance();
                    ClientWorld world = mc.level;
                    if(world.getEntity(m.entityID) instanceof IAnimationListener){
                        ((IAnimationListener)world.getEntity(m.entityID)).startAnimation(m.anim);
                    }
                }
            });
            ctx.get().setPacketHandled(true);

        }
    }
}
