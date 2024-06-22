package com.hollingsworth.arsnouveau.api.camera;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.entity.ScryerCamera;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSetCameraView;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public interface ICameraMountable {

    default void mountCamera(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            ServerPlayer serverPlayer = (ServerPlayer) player;
            SectionPos chunkPos = SectionPos.of(pos);
            int viewDistance = serverPlayer.server.getPlayerList().getViewDistance();
            Entity var10 = serverPlayer.getCamera();
            ScryerCamera dummyEntity;
            if (var10 instanceof ScryerCamera cam) {
                dummyEntity = new ScryerCamera(level, pos, cam);
            } else {
                dummyEntity = new ScryerCamera(level, pos);
            }

            level.addFreshEntity(dummyEntity);

            for (int x = chunkPos.getX() - viewDistance; x <= chunkPos.getX() + viewDistance; x++) {
                for (int z = chunkPos.getZ() - viewDistance; z <= chunkPos.getZ() + viewDistance; z++) {
                    ForcedChunkManager.forceChunk(serverLevel, ArsNouveau.MODID, dummyEntity, x, z, true, false);
                }
            }


            serverPlayer.camera = dummyEntity;
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PacketSetCameraView(dummyEntity));
            startViewing();
        }
    }

    void startViewing();

    void stopViewing();
}
