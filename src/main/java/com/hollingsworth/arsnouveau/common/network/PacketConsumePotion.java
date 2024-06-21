package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.common.items.PotionFlask;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketConsumePotion {
    int inventorySlot;


    public PacketConsumePotion(FriendlyByteBuf buf) {
        this.inventorySlot = buf.readInt();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf){
        buf.writeInt(inventorySlot);
    }

    public PacketConsumePotion(int inventorySlot) {
        this.inventorySlot = inventorySlot;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null)
                return;

            if(inventorySlot >= player.inventory.getContainerSize())
                return;
            ItemStack stack = player.inventory.getItem(inventorySlot);
            if(stack.getItem() instanceof PotionItem){
                PotionData data = new PotionData(stack);
                data.applyEffects(player, player, player);
                stack.shrink(1);
                player.inventory.add(new ItemStack(Items.GLASS_BOTTLE));
                player.level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.5f, player.level.random.nextFloat() * 0.1F + 0.9F);
            }else if(stack.getItem() instanceof PotionFlask){
                PotionFlask.FlaskData data = new PotionFlask.FlaskData(stack);
                if(data.getPotion().isEmpty() || data.getCount() <= 0)
                    return;
                data.getPotion().applyEffects(player, player, player);
                data.setCount(data.getCount() - 1);
                player.level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.5f, player.level.random.nextFloat() * 0.1F + 0.9F);
            }

        });
        ctx.get().setPacketHandled(true);
    }
}
