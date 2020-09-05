package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtract;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class Debug extends ModItem{
    public Debug() {
        super("debug");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerIn, Hand handIn) {
        if(world.isRemote)
            return ActionResult.resultPass(playerIn.getHeldItem(handIn));

        RayTraceResult result =  playerIn.pick(5, 0, true);
        ItemUseContext context = new ItemUseContext(playerIn, handIn, (BlockRayTraceResult) result);

        if(result.getType() != RayTraceResult.Type.BLOCK)
            return ActionResult.resultPass(playerIn.getHeldItem(handIn));


        List<BlockPos> posList = SpellUtil.calcAOEBlocks(playerIn, context.getPos(), (BlockRayTraceResult) result,3, 3, 1, -1);
        BlockState state;
        for(BlockPos pos1 : posList) {
            state = world.getBlockState(pos1);
            // Iron block or lower unpowered

            world.destroyBlock(pos1, true);
            world.notifyBlockUpdate(pos1, state, state, 3);
        }
        return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
    }
}
