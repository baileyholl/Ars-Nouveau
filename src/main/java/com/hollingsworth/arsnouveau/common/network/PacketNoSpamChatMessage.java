package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * A text message sent from player to client
 */
public class PacketNoSpamChatMessage {
    private final ITextComponent message;
    private final int messageChannelId;
    private final boolean overlayMessage;

    /** Base channel for messages sent via this packet. Must be unique among users of this feature. */
    private static final int MESSAGE_ID = ArsNouveau.MODID.hashCode(); // -643241190

    /**
     * Creates a new packet to send a no-spam message to a player.
     *
     * Messages send to the player this way will not spam the chat log.
     *
     * @param message the message to send
     * @param messageChannelId an offset from the base chat channel. Later messages with the same id will cause earlier
     *                         ones to be deleted from the chat history, avoiding spam.
     * @param overlayMessage if true, the message will instead be displayed briefly in the center of the screen just
     *                       above the main bar.  If true, <code>messageChannelId</code> will be ignored.
     */
    public PacketNoSpamChatMessage(ITextComponent message, int messageChannelId, boolean overlayMessage) {
        this.message = message;
        this.messageChannelId = MESSAGE_ID + messageChannelId;
        this.overlayMessage = overlayMessage;
    }

    // Decoder
    public PacketNoSpamChatMessage(PacketBuffer buf) {
        this.message = buf.readComponent();
        this.messageChannelId = buf.readInt();
        this.overlayMessage = buf.readBoolean();
    }

    // Encoder
    public void toBytes(PacketBuffer buf) {
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
                NewChatGui gui = Minecraft.getInstance().gui.getChat();
                gui.addMessage(message, messageChannelId);
            }
        });
        ctx.setPacketHandled(true);
    }
}
