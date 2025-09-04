package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PacketSetSound extends AbstractPacket {
    public static final Type<PacketSetSound> TYPE = new Type<>(ArsNouveau.prefix("set_sound"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSetSound> CODEC = StreamCodec.ofMember(PacketSetSound::toBytes, PacketSetSound::new);
    int castSlot;
    ConfiguredSpellSound sound;
    boolean mainHand;

    public PacketSetSound(int castSlot, @NotNull ConfiguredSpellSound sound, boolean mainHand) {
        this.castSlot = castSlot;
        this.sound = sound;
        this.mainHand = mainHand;
    }

    //Decoder
    public PacketSetSound(RegistryFriendlyByteBuf buf) {
        castSlot = buf.readInt();
        sound = ConfiguredSpellSound.STREAM.decode(buf);
        mainHand = buf.readBoolean();
    }

    //Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(castSlot);
        ConfiguredSpellSound.STREAM.encode(buf, sound);
        buf.writeBoolean(mainHand);
    }

    @Override
    public void onServerReceived(MinecraftServer minecraftServer, ServerPlayer player) {
        ItemStack stack = player.getItemInHand(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
        if (stack.getItem() instanceof SpellBook) {
            SpellCasterRegistry.from(stack).setSound(sound, castSlot).saveToStack(stack);
            Networking.sendToPlayerClient(new PacketUpdateBookGUI(stack), player);
            Networking.sendToPlayerClient(new PacketOpenSpellBook(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND), player);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
