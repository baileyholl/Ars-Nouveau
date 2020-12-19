package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.world.World;

public class VolcanicAccumulatorBI extends AnimBlockItem{
    public VolcanicAccumulatorBI(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    /**
     * Called when this item is used when targetting a Block
     */
    public ActionResultType onItemUse(ItemUseContext context) {
        return ActionResultType.PASS;
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     */
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        BlockRayTraceResult blockraytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.ANY);
        BlockRayTraceResult blockraytraceresult1 = blockraytraceresult.withPosition(blockraytraceresult.getPos().up());
        if(worldIn.getBlockState(blockraytraceresult.getPos()).isAir())
            return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
        super.onItemUse(new ItemUseContext(playerIn, handIn, blockraytraceresult1));
        return new ActionResult<>(ActionResultType.FAIL, playerIn.getHeldItem(handIn));
    }
}
