package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import java.util.function.Supplier;

public class PacketUpdateCaster {

    Spell spellRecipe;
    int cast_slot;
    String spellName;
    boolean mainHand;

    public PacketUpdateCaster(Spell spellRecipe, int cast_slot, String spellName, boolean mainHand) {
        this.spellRecipe = spellRecipe;
        this.cast_slot = cast_slot;
        this.spellName = spellName;
        this.mainHand = mainHand;
    }

    //Decoder
    public PacketUpdateCaster(FriendlyByteBuf buf) {
        spellRecipe = Spell.fromTag(buf.readNbt());
        cast_slot = buf.readInt();
        spellName = buf.readUtf(32767);
        this.mainHand = buf.readBoolean();
    }

    //Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(spellRecipe.serialize());
        buf.writeInt(cast_slot);
        buf.writeUtf(spellName);
        buf.writeBoolean(mainHand);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() != null) {
                InteractionHand hand = mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                ItemStack stack = ctx.get().getSender().getItemInHand(hand);
                if(!(stack.getItem() instanceof ICasterTool))
                    return;
                if (spellRecipe != null) {
                    ISpellCaster caster = CasterUtil.getCaster(stack);
                    caster.setCurrentSlot(cast_slot);
                    // Update just the recipe, don't overwrite the entire spell.
                    Spell spell = caster.getSpell(cast_slot).setRecipe(spellRecipe.recipe);
                    caster.setSpell(spell, cast_slot);
                    caster.setSpellName(spellName, cast_slot);

                    Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()), new PacketUpdateBookGUI(stack));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
