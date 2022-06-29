package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class PacketUpdateSpellColors {

   int castSlot;
   ParticleColor color;

    public PacketUpdateSpellColors(int slot, ParticleColor color){
        this.castSlot = slot;
        this.color = color;
    }
    //Decoder
    public PacketUpdateSpellColors(FriendlyByteBuf buf){
        castSlot = buf.readInt();
        color = ParticleColor.deserialize(buf.readNbt());
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf){
        buf.writeInt(castSlot);
        buf.writeNbt(color.serialize());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            if(ctx.get().getSender() != null){
                ItemStack stack = StackUtil.getHeldSpellbook(ctx.get().getSender());
                if(stack.getItem() instanceof SpellBook){
                    ISpellCaster caster = CasterUtil.getCaster(stack);
                    caster.setColor(color, castSlot);
                    System.out.println(color);
                    caster.setCurrentSlot(castSlot);
                    Networking.INSTANCE.send(PacketDistributor.PLAYER.with(()->ctx.get().getSender()), new PacketUpdateBookGUI(stack));
                    Networking.INSTANCE.send(PacketDistributor.PLAYER.with(()->ctx.get().getSender()),
                            new PacketOpenSpellBook(stack, ((SpellBook) stack.getItem()).tier.value));

                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
