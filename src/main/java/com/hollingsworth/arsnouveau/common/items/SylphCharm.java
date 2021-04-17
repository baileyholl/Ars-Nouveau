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
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        if(world.getBlockEntity(blockpos) instanceof SummoningCrystalTile){
            EntitySylph sylph = new EntitySylph(world, true, blockpos);
            sylph.setPos(blockpos.getX(), blockpos.getY() + 1.0, blockpos.getZ());
            world.addFreshEntity(sylph);
            ((SummoningCrystalTile) world.getBlockEntity(blockpos)).summon(sylph);
            context.getItemInHand().shrink(1);
        }
        return ActionResultType.SUCCESS;
    }
}
