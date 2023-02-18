/*
 * MIT License
 *
 * Copyright © 2015 - 2021 Refined Mods
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.hollingsworth.arsnouveau.common.network.util;

import com.hollingsworth.arsnouveau.common.network.Networking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.function.Supplier;

public class MessageSplitPacket {
    /**
     * The payload.
     */
    private final byte[] payload;
    /**
     * Internal communication id. Used to indicate to what wrapped message this belongs to.
     */
    private final int communicationId;
    /**
     * The index of the split message in the wrapped message.
     */
    private final int packetIndex;

    public MessageSplitPacket(final int communicationId, final int packetIndex, final byte[] payload) {
        this.communicationId = communicationId;
        this.packetIndex = packetIndex;
        this.payload = payload;
    }

    public static void encode(MessageSplitPacket message, FriendlyByteBuf buf) {
        buf.writeVarInt(message.communicationId);
        buf.writeVarInt(message.packetIndex);
        buf.writeByteArray(message.payload);
    }

    public static MessageSplitPacket decode(final FriendlyByteBuf buf) {
        return new MessageSplitPacket(buf.readVarInt(), buf.readVarInt(), buf.readByteArray());
    }

    public static boolean handle(MessageSplitPacket data, Supplier<Context> ctx) {
        Networking.addPackagePart(data.communicationId, data.packetIndex, data.payload);
        ctx.get().setPacketHandled(true);
        return true;
    }
}
