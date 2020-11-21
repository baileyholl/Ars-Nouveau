package com.hollingsworth.arsnouveau.mixin.block;

import com.hollingsworth.arsnouveau.common.block.LavaLily;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MagmaBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MagmaBlock.class)
public abstract class MagmaBlockMixin extends Block {
    public MagmaBlockMixin(AbstractBlock.Properties properties) { super(properties); }

    @Inject(method = "onEntityWalk", at = @At("HEAD"), cancellable = true)
    protected void hookOnEntityWalk(World worldIn, BlockPos pos, Entity entityIn, CallbackInfo callbackInfo)
    {
        // Bypass the MagmaBlock-specific logic if a LavaLily is directly above.
        if (worldIn.getBlockState(pos.up()).getBlock() instanceof LavaLily)
        {
            super.onEntityWalk(worldIn, pos, entityIn);
            callbackInfo.cancel();
        }
    }
}
