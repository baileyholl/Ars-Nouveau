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
   int r;
   int g;
   int b;

    public PacketUpdateSpellColors(int castSlot, int r, int g, int b){
        this.castSlot = castSlot;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    //Decoder
    public PacketUpdateSpellColors(FriendlyByteBuf buf){
        castSlot = buf.readInt();
        r = buf.readInt();
        g = buf.readInt();
        b = buf.readInt();
    }

    public PacketUpdateSpellColors(int slot, double red, double green, double blue) {
        this(slot, (int)red, (int) green, (int) blue);
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf){
        buf.writeInt(castSlot);
        buf.writeInt(r);
        buf.writeInt(g);
        buf.writeInt(b);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            if(ctx.get().getSender() != null){
                ItemStack stack = StackUtil.getHeldSpellbook(ctx.get().getSender());
                if(stack.getItem() instanceof SpellBook){
                    ISpellCaster caster = CasterUtil.getCaster(stack);
                    caster.setColor(new ParticleColor.IntWrapper(r, g, b), castSlot);
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
