package com.hollingsworth.arsnouveau.api.block;

import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ArcanePedestal;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public interface IPedestalMachine {
    default List<BlockPos> pedestalList(BlockPos blockPos, int offset, @NotNull Level level) {
        ArrayList<BlockPos> posList = new ArrayList<>();
        for (BlockPos b : BlockPos.betweenClosed(blockPos.offset(offset, -offset, offset), blockPos.offset(-offset, offset, -offset))) {
            if (level.getBlockEntity(b) instanceof ArcanePedestalTile tile) {
                posList.add(b.immutable());
            }
        }
        return posList;
    }

    void lightPedestal(Level level);

    default void spawnParticlesForPedestal(Level level, List<BlockPos> pedestalList) {
        for (BlockPos pos : pedestalList) {
            spawnParticlesForPedestal(level, pos);
        }
    }

    default void spawnParticlesForPedestal(Level level, BlockPos pos) {
        BlockState pedestalState = level.getBlockState(pos);
        if (!(pedestalState.getBlock() instanceof ArcanePedestal arcanePedestal)) {
            return;
        }
        Vector3f offsetVec = arcanePedestal.getItemOffset(pedestalState, pos);
        double x = offsetVec.x + ParticleUtil.inRange(-0.3, 0.3);
        double y = offsetVec.y + ParticleUtil.inRange(-0.2, 0.2);
        double z = offsetVec.z + ParticleUtil.inRange(-0.3, 0.3);

        if (level instanceof ServerLevel serverLevel) {
            ParticleOptions type = GlowParticleData.createData(ParticleColor.makeRandomColor(255, 255, 255, level.random), 0.4f, 0.5f, 300);
            ParticleUtil.sendParticles(serverLevel, type, x, y, z, 1, 0d, 0d, 0, 0);
        } else {
            ParticleOptions type = GlowParticleData.createData(ParticleColor.makeRandomColor(255, 255, 255, level.random));
            level.addParticle(type, x, y, z, 0d, 0d, 0);
        }

    }
}
