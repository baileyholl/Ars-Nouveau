package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class PacketUpdateCaster extends AbstractPacket{

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
    public PacketUpdateCaster(RegistryFriendlyByteBuf buf) {
        spellRecipe = Spell.fromTag(buf.readNbt());
        cast_slot = buf.readInt();
        spellName = buf.readUtf(32767);
        this.mainHand = buf.readBoolean();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeNbt(spellRecipe.serialize());
        buf.writeInt(cast_slot);
        buf.writeUtf(spellName);
        buf.writeBoolean(mainHand);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        InteractionHand hand = mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack stack = player.getItemInHand(hand);
        if(!(stack.getItem() instanceof ICasterTool))
            return;
        if (spellRecipe != null) {
            ISpellCaster caster = CasterUtil.getCaster(stack);
            caster.setCurrentSlot(cast_slot);
            // Update just the recipe, don't overwrite the entire spell.
            Spell spell = caster.getSpell(cast_slot).setRecipe(spellRecipe.recipe);
            caster.setSpell(spell, cast_slot);
            caster.setSpellName(spellName, cast_slot);

            Networking.sendToPlayerClient(new PacketUpdateBookGUI(stack), player);
        }
    }

    public static final Type<PacketUpdateCaster> TYPE = new Type<>(ArsNouveau.prefix("update_caster"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateCaster> CODEC = StreamCodec.ofMember(PacketUpdateCaster::toBytes, PacketUpdateCaster::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
