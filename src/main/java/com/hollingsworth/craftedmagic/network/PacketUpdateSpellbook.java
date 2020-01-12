package com.hollingsworth.craftedmagic.network;

import com.hollingsworth.craftedmagic.items.Spell;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class PacketUpdateSpellbook{

   String spell_Ids;
   int cast_slot;

    public PacketUpdateSpellbook(){}

    public PacketUpdateSpellbook(String spell_Ids, int cast_slot){
        this.spell_Ids = spell_Ids;
        this.cast_slot = cast_slot;

    }

    //Decoder
    public PacketUpdateSpellbook(PacketBuffer buf){
        spell_Ids = buf.readString();
        cast_slot = buf.readInt();
    }

    //Encoder
    public void toBytes(PacketBuffer buf){
        buf.writeString(spell_Ids);
        buf.writeInt(cast_slot);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            if(ctx.get().getSender() != null){
                ItemStack stack = ctx.get().getSender().getHeldItemMainhand();
                System.out.println(stack);
                System.out.println(spell_Ids);
                System.out.println(cast_slot);
                if(stack != null && stack.getItem() instanceof Spell && spell_Ids != null){
                    System.out.println("Making tag");
                    System.out.println(stack.hasTag());
                    CompoundNBT tag =stack.hasTag() ? stack.getTag() : new CompoundNBT();
                    System.out.println("Printing tag:");
                    System.out.println(tag);
                    System.out.println(spell_Ids.toString());
                    tag.putString(cast_slot + "recipe", spell_Ids.toString());
                    stack.setTag(tag);
                }
//                System.out.println(ctx.getSender().getHeldItemMainhand());
//                System.out.println(this.cast_slot);
//                System.out.println(this.spell_Ids);
            }

        });
        ctx.get().setPacketHandled(true);

    }
}
