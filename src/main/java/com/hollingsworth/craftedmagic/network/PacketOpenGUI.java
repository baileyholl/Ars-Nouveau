package com.hollingsworth.craftedmagic.network;

import com.hollingsworth.craftedmagic.api.CraftedMagicAPI;
import com.hollingsworth.craftedmagic.client.gui.GuiSpellCreation;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenGUI{
    public CompoundNBT tag;
    //Decoder
    public PacketOpenGUI(PacketBuffer buf){
        tag = buf.readCompoundTag();
    }

    //Encoder
    public void toBytes(PacketBuffer buf){
        buf.writeCompoundTag(tag);
    }

    public PacketOpenGUI(CompoundNBT tag){
        this.tag = tag;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        System.out.println(ctx.get().getSender());
        ctx.get().enqueueWork(()->GuiSpellCreation.open(CraftedMagicAPI.getInstance(), tag));
        ctx.get().setPacketHandled(true);
    }

}
