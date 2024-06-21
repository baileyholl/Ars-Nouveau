package com.hollingsworth.arsnouveau.common.network;


import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public class ChangeBiomePacket {
    private final BlockPos pos;
    private final ResourceKey<Biome> biomeId;

    public ChangeBiomePacket(BlockPos pos, ResourceKey<Biome> id) {
        this.pos = pos;
        this.biomeId = id;
    }

    public ChangeBiomePacket(FriendlyByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), 0, buf.readInt());
        this.biomeId = ResourceKey.create(Registries.BIOME, buf.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getZ());
        buf.writeResourceLocation(this.biomeId.location());
    }

    public static class Handler {

        @SuppressWarnings("Convert2Lambda")
        public static boolean onMessage(ChangeBiomePacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(new Runnable() {
                @Override
                public void run() {
                    ClientLevel world = Minecraft.getInstance().level;
                    LevelChunk chunkAt = (LevelChunk) world.getChunk(message.pos);

                    Holder<Biome> biome = world.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(message.biomeId);

                    int minY = QuartPos.fromBlock(world.getMinBuildHeight());
                    int maxY = minY + QuartPos.fromBlock(world.getHeight()) - 1;

                    int x = QuartPos.fromBlock(message.pos.getX());
                    int z = QuartPos.fromBlock(message.pos.getZ());

                    for (LevelChunkSection section : chunkAt.getSections()) {
                        for (int sy = 0; sy < 16; sy += 4) {
                            int y = Mth.clamp(QuartPos.fromBlock(chunkAt.getMinSection() + sy), minY, maxY);
                            if (section.getBiomes() instanceof PalettedContainer<Holder<Biome>> container)
                                container.set(x & 3, y & 3, z & 3, biome);
                            SectionPos pos = SectionPos.of(message.pos.getX() >> 4, (chunkAt.getMinSection() >> 4) + sy, message.pos.getZ() >> 4);
                            world.setSectionDirtyWithNeighbors(pos.x(), pos.y(), pos.z());
                        }
                    }
                    world.onChunkLoaded(new ChunkPos(message.pos));
                }
            });

            ctx.get().setPacketHandled(true);
            return true;
        }
    }
}