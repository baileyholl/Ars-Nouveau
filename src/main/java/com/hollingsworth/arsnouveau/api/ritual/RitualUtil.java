package com.hollingsworth.arsnouveau.api.ritual;


import com.hollingsworth.arsnouveau.common.network.ChangeBiomePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.neoforged.neoforge.network.PacketDistributor;

public class RitualUtil {

    /**
     * Provides the next 'closest' position between two positions and an index tracking the last generated position.
     * This is for lazily getting the next closed position between two positions without generating the entire iterator.
     */
    public static BlockPos betweenClosed(int pX1, int pY1, int pZ1, int pX2, int pY2, int pZ2, int index) {
        int i = pX2 - pX1 + 1;
        int j = pY2 - pY1 + 1;
        int k = pZ2 - pZ1 + 1;
        int l = i * j * k;

        int i1 = index % i;
        int j1 = index / i;
        int k1 = j1 % j;
        int l1 = j1 / j;
        return new BlockPos(pX1 + i1, pY1 + k1, pZ1 + l1);
    }

    public static BlockPos betweenClosed(BlockPos pFirstPos, BlockPos pSecondPos, int index) {
        return betweenClosed(Math.min(pFirstPos.getX(), pSecondPos.getX()), Math.min(pFirstPos.getY(), pSecondPos.getY()), Math.min(pFirstPos.getZ(), pSecondPos.getZ()), Math.max(pFirstPos.getX(), pSecondPos.getX()), Math.max(pFirstPos.getY(), pSecondPos.getY()), Math.max(pFirstPos.getZ(), pSecondPos.getZ()), index);
    }

    public static void changeBiome(Level level, BlockPos pos, ResourceKey<Biome> target) {
        Holder<Biome> biome = level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(target);
        BlockPos dPos = pos;

        if (level.getBiome(dPos).is(target))
            return;

        int minY = QuartPos.fromBlock(level.getMinBuildHeight());
        int maxY = minY + QuartPos.fromBlock(level.getHeight()) - 1;

        int x = QuartPos.fromBlock(dPos.getX());
        int z = QuartPos.fromBlock(dPos.getZ());

        LevelChunk chunkAt = level.getChunk(dPos.getX() >> 4, dPos.getZ() >> 4);
        for (LevelChunkSection section : chunkAt.getSections()) {
            for (int sy = 0; sy < 16; sy += 4) {
                int y = Mth.clamp(QuartPos.fromBlock(chunkAt.getMinSection() + sy), minY, maxY);
                if (section.getBiomes().get(x & 3, y & 3, z & 3).is(target))
                    continue;
                if (section.getBiomes() instanceof PalettedContainer<Holder<Biome>> container)
                    container.set(x & 3, y & 3, z & 3, biome);
            }
        }

        if (level instanceof ServerLevel server) {
            if (!chunkAt.isUnsaved()) chunkAt.setUnsaved(true);
            ChangeBiomePacket message = new ChangeBiomePacket(pos, target);
            PacketDistributor.sendToPlayersTrackingChunk(server, chunkAt.getPos(), message);
        }
    }
}
