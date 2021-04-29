package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.common.items.RitualBook;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSetRitual {

    String ritualID;
    boolean isMainhand;

    public PacketSetRitual(){}

    public PacketSetRitual(String ritualID, boolean mainhand){
        this.ritualID = ritualID;
        this.isMainhand = mainhand;
    }

    //Decoder
    public PacketSetRitual(PacketBuffer buf){
        this.ritualID = buf.readUtf();
        this.isMainhand = buf.readBoolean();
    }

    //Encoder
    public void toBytes(PacketBuffer buf){
        buf.writeUtf(ritualID);
        buf.writeBoolean(isMainhand);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            if(ctx.get().getSender() != null){
                ItemStack stack = isMainhand ? ctx.get().getSender().getMainHandItem() : ctx.get().getSender().getOffhandItem();
                if(!stack.isEmpty() && stack.getItem() instanceof RitualBook && ritualID != null){
                    System.out.println(ritualID);
                    RitualBook.setRitualID(stack, ritualID);
                }
            }
        });
        ctx.get().setPacketHandled(true);

    }
}
