package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ISpellHotkeyListener;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class PacketQuickCast extends AbstractPacket{
    public static final Type<PacketQuickCast> TYPE = new Type<>(ArsNouveau.prefix("quick_cast"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketQuickCast> CODEC = StreamCodec.ofMember(PacketQuickCast::toBytes, PacketQuickCast::new);
    int slot;
    float xRot;
    float yRot;

    public PacketQuickCast(int slot) {
        var cam = Minecraft.getInstance().cameraEntity;
        this.slot = slot;
        this.xRot = cam.xRot;
        this.yRot = cam.yRot;
    }

    public PacketQuickCast(int slot, float xRot, float yRot) {
        this.slot = slot;
        this.xRot = xRot;
        this.yRot = yRot;
    }

    //Decoder
    public PacketQuickCast(RegistryFriendlyByteBuf buf){
        this.slot = buf.readInt();
        this.xRot = buf.readFloat();
        this.yRot = buf.readFloat();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf){
        buf.writeInt(slot);
        buf.writeFloat(xRot);
        buf.writeFloat(yRot);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        InteractionHand hand = StackUtil.getQuickCaster(player);
        if(hand == null)
            return;
        ItemStack stack = player.getItemInHand(hand);
        if(!(stack.getItem() instanceof ISpellHotkeyListener hotkeyListener)){
            return;
        }

        float pXRot = player.xRot;
        float pYRot = player.yRot;
        player.setXRot(this.xRot);
        player.setYHeadRot(this.yRot);

        hotkeyListener.onQuickCast(stack, player, hand, slot);

        player.setXRot(pXRot);
        player.setYHeadRot(pYRot);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
