package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
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

    public PacketCastSpell(AbstractCaster<?> caster, InteractionHand hand, @Nullable Component invalidMessage) {
        this.slot = caster.getCurrentSlot();
        var cam = Minecraft.getInstance().cameraEntity;
        this.xRot = cam.xRot;
        this.yRot = cam.yRot;
        this.hand = hand;
        this.invalidMessage = invalidMessage;
    }

    //Decoder
    public PacketCastSpell(RegistryFriendlyByteBuf buf) {
        this.slot = buf.readInt();
        this.xRot = buf.readFloat();
        this.yRot = buf.readFloat();
        this.hand = buf.readEnum(InteractionHand.class);
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

        AbstractCaster<?> caster = SpellCasterRegistry.from(stack);
        if (caster != null) {
            float pXRot = player.xRot;
            float pYRot = player.yRot;
            player.setXRot(this.xRot);
            player.setYHeadRot(this.yRot);

            caster.castSpell(player.level, player, this.hand, this.invalidMessage, caster.getSpell(this.slot));

            player.setXRot(pXRot);
            player.setYHeadRot(pYRot);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
