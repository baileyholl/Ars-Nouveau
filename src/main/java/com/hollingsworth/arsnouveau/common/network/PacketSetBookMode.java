package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSetBookMode {

    public CompoundNBT tag;
    //Decoder
    public PacketSetBookMode(PacketBuffer buf){
        tag = buf.readCompoundTag();
    }

    //Encoder
    public void toBytes(PacketBuffer buf){
        buf.writeCompoundTag(tag);
    }

    public PacketSetBookMode(CompoundNBT tag){
        this.tag = tag;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ctx.get().enqueueWork(()-> {
                if (ctx.get().getSender() != null) {
                    ItemStack stack = ctx.get().getSender().getHeldItemMainhand();
                    if (stack != null && stack.getItem() instanceof SpellBook ) {
                        System.out.println("Setting tag");
                        stack.setTag(tag);
                    }
                }
            });
        } );
        ctx.get().setPacketHandled(true);
    }
}
