package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.common.items.FlaskCannon;
import com.hollingsworth.arsnouveau.common.items.PotionFlask;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;

public class PacketSetLauncher extends AbstractPacket{
    public static final Type<PacketSetLauncher> TYPE = new Type<>(ArsNouveau.prefix("set_launcher"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetLauncher> CODEC = StreamCodec.ofMember(PacketSetLauncher::toBytes, PacketSetLauncher::new);

    public int inventorySlot;

    public PacketSetLauncher(RegistryFriendlyByteBuf  buf) {
        inventorySlot = buf.readInt();
    }

    public PacketSetLauncher(int slot) {
        this.inventorySlot = slot;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(inventorySlot);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
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
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
