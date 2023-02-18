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

import com.google.common.primitives.Bytes;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketSplitter {
    private final static int MAX_PACKET_SIZE = 32767;
    private static final Map<Integer, Map<Integer, byte[]>> packageCache = new HashMap<>();
    private final ResourceLocation CHANNEL_ID;
    private final SimpleChannel CHANNEL;
    private final Map<Integer, ServerPlayer> messageTargets = new HashMap<>();
    private final Map<Integer, Integer> packetMaximums = new HashMap<>();
    private final Set<Class<?>> messagesToSplit = new HashSet<>();
    private final int maxNumberOfMessages;
    private int comId = 0;
    private int ID;

    public PacketSplitter(int maxNumberOfMessages, SimpleChannel CHANNEL, ResourceLocation CHANNEL_ID) {
        this.maxNumberOfMessages = maxNumberOfMessages;
        this.CHANNEL = CHANNEL;
        this.CHANNEL_ID = CHANNEL_ID;
    }

    public boolean shouldMessageBeSplit(Class<?> clazz) {
        return this.messagesToSplit.contains(clazz);
    }

    public void sendToPlayer(ServerPlayer player, Object message) {
        if (this.ID == 0) this.ID++; // in case we wrapped around, 0 is reserved for server
        int id = this.ID++;
        this.messageTargets.put(id, player);
        this.sendPacket(message, id, PacketDistributor.PLAYER.with(() -> player));
    }

    public void sendToServer(Object message) {
        this.messageTargets.put(0, null);
        this.sendPacket(message, 0, PacketDistributor.SERVER.noArg());
    }

    //@Volatile mostly copied from SimpleChannel
    private void sendPacket(Object Message, int id, PacketDistributor.PacketTarget target) {
        final FriendlyByteBuf bufIn = new FriendlyByteBuf(Unpooled.buffer());

        //write the message id to be able to figure out where the packet is supposed to go in the wrapper
        bufIn.writeInt(id);

        int index = this.CHANNEL.encodeMessage(Message, bufIn);
        target.send(target.getDirection().buildPacket(Pair.of(bufIn, index), this.CHANNEL_ID).getThis());
    }

    public <MSG> void registerMessage(int index, Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<Context>> messageConsumer) {
        this.registerMessage(index, this.maxNumberOfMessages, messageType, encoder, decoder, messageConsumer);
    }

    public <MSG> void registerMessage(int index, int maxNumberOfMessages, Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<Context>> messageConsumer) {
        this.packetMaximums.put(index, maxNumberOfMessages);
        this.messagesToSplit.add(messageType);

        BiConsumer<MSG, FriendlyByteBuf> wrappedEncoder = (msg, buffer) -> {
            int id = buffer.readInt();
            buffer.discardReadBytes();
            ServerPlayer player = this.messageTargets.get(id);
            this.messageTargets.remove(id);

            //write a zero for the number of packets in case the packet does not need to be split
            buffer.writeShort(0);
            encoder.accept(msg, buffer);
            this.createSplittingConsumer(player).accept(msg, buffer);
        };


        this.CHANNEL.registerMessage(index, messageType, wrappedEncoder, this.createPacketCombiner().andThen(decoder), messageConsumer);
    }

    private <MSG> BiConsumer<MSG, FriendlyByteBuf> createSplittingConsumer(ServerPlayer playerEntity) {
        return (MSG, buf) -> {

            if (buf.writerIndex() < MAX_PACKET_SIZE) {
                return;
            }

            //read packetId for this packet
            int packetId = buf.readUnsignedByte();

            //this short is written here in case we are not splitting, ignore for split packages
            buf.readShort();

            //ignore the above as it is not required for the final packet
            int currentIndex = buf.readerIndex();
            int packetIndex = 0;
            final int comId = this.comId++;

            //Data for this packet
            byte[] packetData = new byte[0];

            int maximumPackets = this.packetMaximums.get(packetId);
            int expectedPackets = buf.writerIndex() / MAX_PACKET_SIZE + 1;
            boolean failure = false;

            //Loop while data is available.
            while (currentIndex < buf.writerIndex()) {

                int sliceSize = Math.min(MAX_PACKET_SIZE, buf.writerIndex() - currentIndex);

                //Extract the sub data array.
                byte[] subPacketData = Arrays.copyOfRange(buf.array(), currentIndex, currentIndex + sliceSize);

                if (packetIndex == 0) { // Assign Data for first Packet to this packet.
                    packetData = subPacketData;
                    packetIndex++;
                } else {
                    //Construct the split packet.
                    MessageSplitPacket splitPacketMessage = new MessageSplitPacket(comId, packetIndex++, subPacketData);

                    if (playerEntity == null) {
                        this.CHANNEL.send(PacketDistributor.SERVER.noArg(), splitPacketMessage);
                    } else {
                        this.CHANNEL.send(PacketDistributor.PLAYER.with(() -> playerEntity), splitPacketMessage);
                    }
                }

                //Move our working index.
                currentIndex += sliceSize;

                if (packetIndex > maximumPackets) {
                    LogManager.getLogger().error("Failure Splitting Packets on Channel \"" + this.CHANNEL_ID + "\"." + " with " + MSG.getClass() + ". " +
                            " Number of Packets sent " + (packetIndex - 1) + ", expected number of Packets " + expectedPackets + ", maximum number of packets for a message of this type " + this.packetMaximums.get(packetId));
                    failure = true;
                    break;
                }
            }

            //start writing at the beginning
            buf.setIndex(0, 0);

            //packetId is required for forge to match the packet
            buf.writeByte(packetId);

            //number of packets the packet was split into
            buf.writeShort(failure ? expectedPackets : packetIndex);
            buf.writeInt(comId);
            buf.writeByteArray(packetData);

            //copies the written data into a new buffer discarding the old one
            buf.capacity(buf.writerIndex());
        };
    }

    private Function<FriendlyByteBuf, FriendlyByteBuf> createPacketCombiner() {
        return (buf) -> {
            int size = buf.readShort();

            //This packet was not split
            if (size < 2) return buf;

            int comId = buf.readInt();

            Map<Integer, byte[]> partsMap = packageCache.get(comId);
            if (partsMap == null || partsMap.size() != size - 1) {
                int partSize = partsMap == null ? 0 : partsMap.size();
                int id = buf.readUnsignedByte();
                int max = this.packetMaximums.get(id) == null ? 0 : this.packetMaximums.get(id);
                throw new PacketSplittingException(this.CHANNEL_ID, partSize, size, max, id);
            }

            //Add data that came from this packet
            this.addPackagePart(comId, 0, buf.readByteArray());

            //Combine Cached Data
            final byte[] packetData = partsMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue)
                    .reduce(new byte[0], Bytes::concat);

            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(packetData));

            //remove data from cache
            packageCache.remove(comId);
            return buffer;
        };
    }

    public void addPackagePart(int communicationId, int packetIndex, byte[] payload) {
        //Sync on the message cache since this is still on the Netty thread.
        synchronized (PacketSplitter.packageCache) {
            PacketSplitter.packageCache.computeIfAbsent(communicationId, (id) -> new ConcurrentHashMap<>());
            PacketSplitter.packageCache.get(communicationId).put(packetIndex, payload);
        }
    }
}

class PacketSplittingException extends RuntimeException {
    ResourceLocation channnelId;
    int actualSize;
    int expectedSize;
    int maximumSize;
    int packetId;

    public PacketSplittingException(ResourceLocation channnelId, int actualSize, int expectedSize, int maximumSize, int packetId) {
        this.channnelId = channnelId;
        this.actualSize = actualSize;
        this.expectedSize = expectedSize;
        this.maximumSize = maximumSize;
        this.packetId = packetId;
    }

    @Override
    public String getMessage() {
        return "Failure Splitting Packets on Channel \"" + this.channnelId.toString() + "\"." +
                " Number of Packets sent " + this.actualSize + ", Number of Packets expected " + this.expectedSize + ", maximum number of packets for a message of this type " + this.maximumSize;
    }

}