package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.block.tile.SummoningCrytalTile;
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
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        if(world.getTileEntity(blockpos) instanceof SummoningCrytalTile){
            EntityWhelp whelp = new EntityWhelp(world, blockpos);
            whelp.setPosition(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
            world.addEntity(whelp);
            ((SummoningCrytalTile) world.getTileEntity(blockpos)).summon(whelp);
            context.getItem().shrink(1);
        }
        return ActionResultType.SUCCESS;
    }
}
