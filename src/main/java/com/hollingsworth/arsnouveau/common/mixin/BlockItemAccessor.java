package com.hollingsworth.arsnouveau.common.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockItem.class)
public interface BlockItemAccessor {
    @Invoker
    BlockState invokeUpdateBlockStateFromTag(BlockPos pos, Level level, ItemStack stack, BlockState state);

    @Invoker
    static void invokeUpdateBlockEntityComponents(Level level, BlockPos poa, ItemStack stack) {
    }
}
