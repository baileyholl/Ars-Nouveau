package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class PacketUpdateCaster {

   String spellRecipe;
   int cast_slot;
   String spellName;

    public PacketUpdateCaster(){}
    // TODO: Change from string to list of resource locations
    public PacketUpdateCaster(String spellRecipe, int cast_slot, String spellName){
        this.spellRecipe = spellRecipe;
        this.cast_slot = cast_slot;
        this.spellName = spellName;
    }

    //Decoder
    public PacketUpdateCaster(FriendlyByteBuf buf){
        spellRecipe = buf.readUtf(32767);
        cast_slot = buf.readInt();
        spellName = buf.readUtf(32767);
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf){
        buf.writeUtf(spellRecipe);
        buf.writeInt(cast_slot);
        buf.writeUtf(spellName);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(()->{
            if(ctx.get().getSender() != null){
                InteractionHand hand = StackUtil.getHeldCasterTool(ctx.get().getSender());
                if(hand == null)
                    return;
                ItemStack stack = ctx.get().getSender().getItemInHand(hand);
                if(spellRecipe != null){
                    ISpellCaster caster = CasterUtil.getCaster(stack);
                    caster.setCurrentSlot(cast_slot);
                    caster.setSpell(Spell.deserialize(spellRecipe));
                    caster.setSpellName(spellName);
                    Networking.INSTANCE.send(PacketDistributor.PLAYER.with(()->ctx.get().getSender()), new PacketUpdateBookGUI(stack));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
