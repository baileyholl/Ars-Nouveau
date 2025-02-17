package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.api.event.SuccessfulTreeGrowthEvent;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TreeGrower.class)
public class TreeGrowerMixin {
    @ModifyReturnValue(method = "growTree", at = @At("RETURN"))
    public boolean growTree(boolean original, @Local(argsOnly = true) ServerLevel level, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) BlockState state) {
        if (original) {
            NeoForge.EVENT_BUS.post(new SuccessfulTreeGrowthEvent(level, pos, state));
        }

        return original;
    }
}
