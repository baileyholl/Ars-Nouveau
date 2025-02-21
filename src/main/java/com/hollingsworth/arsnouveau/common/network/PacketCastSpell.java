package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.capability.IPlayerCap;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class PacketCastSpell extends AbstractPacket {
    public static final Type<PacketCastSpell> TYPE = new Type<>(ArsNouveau.prefix("cast_spell"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketCastSpell> CODEC = StreamCodec.ofMember(PacketCastSpell::toBytes, PacketCastSpell::new);

    int slot;
    float xRot;
    float yRot;
    InteractionHand hand;
    @Nullable
    Component invalidMessage;
    Spell spell;

    public PacketCastSpell(AbstractCaster<?> caster, InteractionHand hand, @Nullable Component invalidMessage) {
        this.slot = caster.getCurrentSlot();
        var cam = Minecraft.getInstance().cameraEntity;
        this.xRot = cam.xRot;
        this.yRot = cam.yRot;
        this.hand = hand;
        this.invalidMessage = invalidMessage;
        this.spell = caster.getSpell();
    }

    //Decoder
    public PacketCastSpell(RegistryFriendlyByteBuf buf) {
        this.slot = buf.readInt();
        this.xRot = buf.readFloat();
        this.yRot = buf.readFloat();
        this.hand = buf.readEnum(InteractionHand.class);
        this.spell = buf.readJsonWithCodec(Spell.CODEC.codec());
        if (buf.readBoolean()) {
            this.invalidMessage = buf.readJsonWithCodec(ComponentSerialization.CODEC);
        }
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.slot);
        buf.writeFloat(this.xRot);
        buf.writeFloat(this.yRot);
        buf.writeEnum(this.hand);
        buf.writeJsonWithCodec(Spell.CODEC.codec(), this.spell);
        if (invalidMessage != null) {
            buf.writeBoolean(true);
            buf.writeJsonWithCodec(ComponentSerialization.CODEC, this.invalidMessage);
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        ItemStack stack = player.getItemInHand(this.hand);

        if (stack.getItem() instanceof SpellBook spellbook && spellbook.tier != SpellTier.CREATIVE) {
            var iMana = CapabilityRegistry.getMana(player);
            if (iMana != null) {
                boolean shouldSync = false;
                if (iMana.getBookTier() < spellbook.tier.value) {
                    iMana.setBookTier(spellbook.tier.value);
                    shouldSync = true;
                }

                IPlayerCap cap = CapabilityRegistry.getPlayerDataCap(player);
                if (iMana.getGlyphBonus() < cap.getKnownGlyphs().size()) {
                    iMana.setGlyphBonus(cap.getKnownGlyphs().size());
                    shouldSync = true;
                }

                if (shouldSync) {
                    iMana.syncToClient(player);
                }
            }
        }

        AbstractCaster<?> caster = SpellCasterRegistry.from(stack);
        if (caster != null) {
            float pXRot = player.xRot;
            float pYRot = player.yRot;
            player.setXRot(this.xRot);
            player.setYHeadRot(this.yRot);

            caster.castSpell(player.level, player, this.hand, this.invalidMessage, this.spell);

            player.setXRot(pXRot);
            player.setYHeadRot(pYRot);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
