package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSetBookMode {

    public CompoundNBT tag;
    //Decoder
    public PacketSetBookMode(PacketBuffer buf){
        tag = buf.readNbt();
    }

    //Encoder
    public void toBytes(PacketBuffer buf){
        buf.writeNbt(tag);
    }

    public PacketSetBookMode(CompoundNBT tag){
        this.tag = tag;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ctx.get().enqueueWork(()-> {
                ServerPlayerEntity sender = ctx.get().getSender();
                if (sender == null) return;

                ItemStack stack = StackUtil.getHeldSpellbook(ctx.get().getSender());
                if (stack.getItem() instanceof SpellBook) {
                    stack.setTag(tag);
                }
            });
        } );
        ctx.get().setPacketHandled(true);
    }
}
