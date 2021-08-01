package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.block.tile.SummoningCrystalTile;
import com.hollingsworth.arsnouveau.common.entity.EntityWhelp;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WhelpCharm extends ModItem{
    public WhelpCharm() {
        super(LibItemNames.WHELP_CHARM);
    }

    /**
     * Called when this item is used when targetting a Block
     */
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        if(world.getBlockEntity(blockpos) instanceof SummoningCrystalTile){
            EntityWhelp whelp = new EntityWhelp(world, blockpos);
            whelp.setPos(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
            world.addFreshEntity(whelp);
            ((SummoningCrystalTile) world.getBlockEntity(blockpos)).summon(whelp);
            context.getItemInHand().shrink(1);
        }
        return ActionResultType.SUCCESS;
    }
}
