package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.item.ISpellHotkeyListener;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketQuickCast {
    int slot;

    public PacketQuickCast(int slot){
        this.slot = slot;
    }

    //Decoder
    public PacketQuickCast(FriendlyByteBuf buf){
        this.slot = buf.readInt();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf){
        buf.writeInt(slot);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            ServerPlayer player = ctx.get().getSender();
            if(player != null){
                InteractionHand hand = StackUtil.getQuickCaster(player);
                if(hand == null)
                    return;
                ItemStack stack = player.getItemInHand(hand);
                if(!(stack.getItem() instanceof ISpellHotkeyListener hotkeyListener)){
                    return;
                }
                hotkeyListener.onQuickCast(stack, player, hand, slot);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
