package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.common.util.WorldUtil;
import com.hollingsworth.arsnouveau.common.world.saved_data.JarDimData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class DimBoundary extends ModBlock implements IWandable {

    public DimBoundary() {
        super(BlockBehaviour.Properties.of().strength(0.2f, 3600000.0F)
                .noLootTable()
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isValidSpawn(Blocks::never)
                .pushReaction(PushReaction.BLOCK));
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            if (serverPlayer instanceof FakePlayer) {
                return false;
            }
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

    @Override
    public Result onFirstConnection(@Nullable GlobalPos storedPos, @Nullable Direction face, @Nullable LivingEntity storedEntity, Player playerEntity) {
        Level level = playerEntity.level;
        if (storedPos == null
                || !(level instanceof ServerLevel serverLevel)
                || !level.dimension().equals(storedPos.dimension())
                || !WorldUtil.isOfWorldType(level, ArsNouveau.DIMENSION_TYPE_KEY)) {
            return Result.FAIL;
        }
        JarDimData dimData = JarDimData.from(serverLevel);
        if (dimData == null) {
            return Result.FAIL;
        }
        AABB innerBox = new AABB(0, 0, 0, 31, 30, 31);
        BlockPos pos = storedPos.pos();
        if (!innerBox.contains(pos.getX(), pos.getY(), pos.getZ())) {
            playerEntity.sendSystemMessage(Component.translatable("ars_nouveau.jar_spawn_out_of_bounds"));
        } else {
            dimData.setSpawnPos(storedPos.pos());
            playerEntity.sendSystemMessage(Component.translatable("ars_nouveau.set_jar_spawn"));
        }
        return Result.SUCCESS;
    }
}
