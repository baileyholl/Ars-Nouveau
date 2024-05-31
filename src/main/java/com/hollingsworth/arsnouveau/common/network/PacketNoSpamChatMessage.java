package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.mixin.ChatComponentAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.Style;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.Random;
import java.util.function.Supplier;

/**
 * A text message sent from player to client
 */
public class PacketNoSpamChatMessage {
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
    public PacketNoSpamChatMessage(FriendlyByteBuf buf) {
        this.message = buf.readComponent();
        this.messageChannelId = buf.readInt();
        this.overlayMessage = buf.readBoolean();
    }

    // Encoder
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeComponent(message);
        buf.writeInt(messageChannelId);
        buf.writeBoolean(overlayMessage);
    }

    // Handler
    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            // This packet is only registered to be received on the client
            if (overlayMessage) {
                if (Minecraft.getInstance().player != null) {
                    Minecraft.getInstance().player.displayClientMessage(message, true);
                }
            } else {
                ChatComponent gui = Minecraft.getInstance().gui.getChat();
                ChatComponentAccessor chatComponentAccessor = (ChatComponentAccessor) gui;
                chatComponentAccessor.getAllMessages().removeIf(m -> m.signature() != null && m.signature().equals(AN_SIGNATURE));
                gui.rescaleChat();
                gui.addMessage(message.plainCopy().setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)), AN_SIGNATURE, GuiMessageTag.system());

            }
        });
        ctx.setPacketHandled(true);
    }
}
