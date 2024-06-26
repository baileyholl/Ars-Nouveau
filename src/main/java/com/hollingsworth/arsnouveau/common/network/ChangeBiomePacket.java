package com.hollingsworth.arsnouveau.common.network;


import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;

public class ChangeBiomePacket extends AbstractPacket {
    public static final Type<ChangeBiomePacket> TYPE = new Type<>(ArsNouveau.prefix("change_biome"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeBiomePacket> CODEC = StreamCodec.ofMember(ChangeBiomePacket::toBytes, ChangeBiomePacket::new);

    private final BlockPos pos;
    private final ResourceKey<Biome> biomeId;

    public ChangeBiomePacket(BlockPos pos, ResourceKey<Biome> id) {
        this.pos = pos;
        this.biomeId = id;
    }

    public ChangeBiomePacket(RegistryFriendlyByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), 0, buf.readInt());
        this.biomeId = ResourceKey.create(Registries.BIOME, buf.readResourceLocation());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void toBytes(RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getZ());
        buf.writeResourceLocation(this.biomeId.location());
    }

    @Override
    public void onClientReceived(Minecraft minecraft, Player player) {
        ClientLevel world = minecraft.level;
        LevelChunk chunkAt = (LevelChunk) world.getChunk(pos);

        Holder<Biome> biome = world.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(biomeId);

        int minY = QuartPos.fromBlock(world.getMinBuildHeight());
        int maxY = minY + QuartPos.fromBlock(world.getHeight()) - 1;

        int x = QuartPos.fromBlock(pos.getX());
        int z = QuartPos.fromBlock(pos.getZ());

        for (LevelChunkSection section : chunkAt.getSections()) {
            for (int sy = 0; sy < 16; sy += 4) {
                int y = Mth.clamp(QuartPos.fromBlock(chunkAt.getMinSection() + sy), minY, maxY);
                if (section.getBiomes() instanceof PalettedContainer<Holder<Biome>> container)
                    container.set(x & 3, y & 3, z & 3, biome);
                SectionPos pos = SectionPos.of(this.pos.getX() >> 4, (chunkAt.getMinSection() >> 4) + sy, this.pos.getZ() >> 4);
                world.setSectionDirtyWithNeighbors(pos.x(), pos.y(), pos.z());
            }
        }
        world.onChunkLoaded(new ChunkPos(pos));
    }
}