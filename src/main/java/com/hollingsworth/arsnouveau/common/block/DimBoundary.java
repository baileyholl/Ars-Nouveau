package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.common.world.saved_data.JarDimData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;

import java.util.HashSet;

public class DimBoundary extends ModBlock {

    public DimBoundary() {
        super(BlockBehaviour.Properties.of().strength(2, 3600000.0F).noLootTable().sound(SoundType.GLASS).noOcclusion().pushReaction(PushReaction.BLOCK));
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            JarDimData jarDimData = JarDimData.from(serverLevel);
            var enteredFrom = jarDimData.getEnteredFrom(player.getUUID());
            if (enteredFrom == null) {
                sendPlayerToSpawn(serverLevel, serverPlayer);
                return true;
            }

            GlobalPos globalPos = enteredFrom.pos();
            ServerLevel returnLevel = serverLevel.getServer().getLevel(enteredFrom.pos().dimension());
            if (returnLevel != null) {
                BlockPos worldPos = globalPos.pos();
                player.teleportTo(returnLevel, worldPos.getX() + 0.5, worldPos.getY(), worldPos.getZ() + 0.5, new HashSet<>(), enteredFrom.rot().y, enteredFrom.rot().x);
            } else {
                sendPlayerToSpawn(serverLevel, serverPlayer);
            }

        }
        return true;
    }

    private void sendPlayerToSpawn(ServerLevel serverLevel, ServerPlayer player) {
        ServerLevel spawnlevel = serverLevel.getServer().getLevel(player.getRespawnDimension());
        BlockPos respawnPos = player.getRespawnPosition();
        if (spawnlevel == null || respawnPos == null) {
            spawnlevel = serverLevel.getServer().overworld();
            respawnPos = serverLevel.getSharedSpawnPos();
        }

        player.teleportTo(spawnlevel, respawnPos.getX(), respawnPos.getY(), respawnPos.getZ(), new HashSet<>(), player.getYRot(), player.getXRot());
    }
}
