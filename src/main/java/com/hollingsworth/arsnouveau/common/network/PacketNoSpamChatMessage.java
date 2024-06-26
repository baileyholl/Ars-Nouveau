package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.mixin.ChatComponentAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.Random;

/**
 * A text message sent from player to client
 */
public class PacketNoSpamChatMessage extends AbstractPacket {
    public static final Type<PacketNoSpamChatMessage> TYPE = new Type<>(ArsNouveau.prefix("no_spam_chat_message"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PacketNoSpamChatMessage> CODEC = StreamCodec.ofMember(PacketNoSpamChatMessage::toBytes, PacketNoSpamChatMessage::new);

    private final Component message;
    private final int messageChannelId;
    private final boolean overlayMessage;

    /**
     * Base channel for messages sent via this packet. Must be unique among users of this feature.
     */
    private static final int MESSAGE_ID = ArsNouveau.MODID.hashCode(); // -643241190
    private static final MessageSignature AN_SIGNATURE;

    static {
        byte[] bytes = new byte[256];
        Random random = new Random();
        for (int i = 0; i < 256; i++) {
            bytes[i] = (byte) random.nextInt(127);
        }
        AN_SIGNATURE = new MessageSignature(bytes);
    }
    /**
     * Creates a new packet to send a no-spam message to a player.
     * <p>
     * Messages send to the player this way will not spam the chat log.
     *
     * @param message          the message to send
     * @param messageChannelId an offset from the base chat channel. Later messages with the same id will cause earlier
     *                         ones to be deleted from the chat history, avoiding spam.
     * @param overlayMessage   if true, the message will instead be displayed briefly in the center of the screen just
     *                         above the main bar.  If true, <code>messageChannelId</code> will be ignored.
     */
    public PacketNoSpamChatMessage(Component message, int messageChannelId, boolean overlayMessage) {
        this.message = message;
        this.messageChannelId = MESSAGE_ID + messageChannelId;
        this.overlayMessage = overlayMessage;
    }

    // Decoder
    public PacketNoSpamChatMessage(RegistryFriendlyByteBuf buf) {
        this.message = buf.readComponent();
        this.messageChannelId = buf.readInt();
        this.overlayMessage = buf.readBoolean();
    }

    // Encoder
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeComponent(message);
        buf.writeInt(messageChannelId);
        buf.writeBoolean(overlayMessage);
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        // This packet is only registered to be received on the client
        if (overlayMessage) {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.displayClientMessage(message, true);
            }
        } else {
            ChatComponent gui = minecraft.gui.getChat();
            ChatComponentAccessor chatComponentAccessor = (ChatComponentAccessor) gui;
            chatComponentAccessor.getAllMessages().removeIf(m -> m.signature() != null && m.signature().equals(AN_SIGNATURE));
            gui.rescaleChat();
            gui.addMessage(message.plainCopy().setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)), AN_SIGNATURE, GuiMessageTag.system());

        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
