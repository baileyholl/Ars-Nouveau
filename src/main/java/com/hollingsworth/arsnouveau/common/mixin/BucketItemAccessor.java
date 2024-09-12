package com.hollingsworth.arsnouveau.common.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;

@Mixin(BucketItem.class)
public interface BucketItemAccessor {
    @Invoker
    boolean callCanBlockContainFluid(@Nullable Player player, Level worldIn, BlockPos posIn, BlockState blockstate);
}
