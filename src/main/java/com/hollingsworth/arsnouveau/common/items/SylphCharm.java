package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.block.tile.SummoningCrystalTile;
import com.hollingsworth.arsnouveau.common.entity.EntitySylph;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SylphCharm extends ModItem{

    public SylphCharm() {
        super(LibItemNames.SYLPH_CHARM);
    }

    /**
     * Called when this item is used when targetting a Block
     */
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        if(world.getTileEntity(blockpos) instanceof SummoningCrystalTile){
            EntitySylph sylph = new EntitySylph(world, true, blockpos);
            sylph.setPosition(blockpos.getX(), blockpos.getY() + 1.0, blockpos.getZ());
            world.addEntity(sylph);
            ((SummoningCrystalTile) world.getTileEntity(blockpos)).summon(sylph);
            context.getItem().shrink(1);
        }
        return ActionResultType.SUCCESS;
    }
}
