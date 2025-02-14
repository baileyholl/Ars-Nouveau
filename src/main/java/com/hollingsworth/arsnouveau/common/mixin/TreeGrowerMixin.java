package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.api.event.SuccessfulTreeGrowthEvent;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TreeGrower.class)
public class TreeGrowerMixin {
    @WrapMethod(method = "growTree")
    public boolean growTree(ServerLevel level, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource random, Operation<Boolean> original) {
        if (original.call(level, chunkGenerator, pos, state, random)) {
            NeoForge.EVENT_BUS.post(new SuccessfulTreeGrowthEvent(level, pos, state));
            return true;
        }

        return false;
    }
}
