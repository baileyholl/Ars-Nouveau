package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.common.items.FlaskCannon;
import com.hollingsworth.arsnouveau.common.items.PotionFlask;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class PacketSetLauncher extends AbstractPacket{

    public int inventorySlot;

    public PacketSetLauncher(FriendlyByteBuf buf) {
        inventorySlot = buf.readInt();
    }

    public PacketSetLauncher(int slot) {
        this.inventorySlot = slot;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(inventorySlot);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null)
                return;
            if(inventorySlot >= player.inventory.getContainerSize())
                return;
            ItemStack stack = player.inventory.getItem(inventorySlot);
            ItemStack launcherStack = ItemStack.EMPTY;
            if(player.getMainHandItem().getItem() instanceof FlaskCannon){
                launcherStack = player.getMainHandItem();
            }else if(player.getOffhandItem().getItem() instanceof FlaskCannon){
                launcherStack = player.getOffhandItem();
            }
            if(launcherStack.isEmpty()){
                return;
            }
            FlaskCannon.PotionLauncherData data = new FlaskCannon.PotionLauncherData(launcherStack);
            if (stack.getItem() instanceof PotionFlask ) {
                PotionFlask.FlaskData flaskData = new PotionFlask.FlaskData(stack);
                data.setLastDataForRender(flaskData.getPotion());
                data.setLastSlot(inventorySlot);
                data.setAmountLeft(flaskData.getCount());
            }else if(stack.getItem() instanceof PotionItem){
                PotionData potionData = new PotionData(stack);
                data.setLastDataForRender(potionData);
                data.setLastSlot(inventorySlot);
                data.setAmountLeft(stack.getCount());
            }

        });
    }
}
