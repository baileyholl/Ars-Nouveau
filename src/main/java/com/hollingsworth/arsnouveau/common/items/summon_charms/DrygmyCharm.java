package com.hollingsworth.arsnouveau.common.items.summon_charms;

import com.hollingsworth.arsnouveau.api.item.AbstractSummonCharm;
import com.hollingsworth.arsnouveau.common.block.tile.DrygmyTile;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningTile;
import com.hollingsworth.arsnouveau.common.entity.EntityDrygmy;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class DrygmyCharm extends AbstractSummonCharm {

    public DrygmyCharm() {
        super();
    }

    @Override
    public InteractionResult useOnBlock(UseOnContext context, Level world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() == Blocks.MOSSY_COBBLESTONE) {
            world.setBlockAndUpdate(pos, BlockRegistry.DRYGMY_BLOCK.defaultBlockState());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOnSummonTile(UseOnContext context, Level world, SummoningTile tile, BlockPos pos) {
        if (tile instanceof DrygmyTile) {
            EntityDrygmy drygmy = new EntityDrygmy(world, true);
            drygmy.readCharm(context.getItemInHand());
            drygmy.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
            world.addFreshEntity(drygmy);
            drygmy.homePos = new BlockPos(pos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

}
