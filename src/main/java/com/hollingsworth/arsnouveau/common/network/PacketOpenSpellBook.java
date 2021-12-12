package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenSpellBook {
    public ItemStack stack;
    public int tier;
    public String unlockedSpells;

    //Decoder
    public PacketOpenSpellBook(FriendlyByteBuf buf){
        stack = buf.readItem();
        tier = buf.readInt();
        unlockedSpells = buf.readUtf(32767);
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf){
        buf.writeItem(stack);
        buf.writeInt(tier);
        buf.writeUtf(unlockedSpells);
    }

    public PacketOpenSpellBook(ItemStack stack, int tier, String unlockedSpells){
        this.stack = stack;
        this.tier = tier;
        this.unlockedSpells = unlockedSpells;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()-> GuiSpellBook.open(ArsNouveauAPI.getInstance(), stack, tier, unlockedSpells));
        ctx.get().setPacketHandled(true);
    }

}
