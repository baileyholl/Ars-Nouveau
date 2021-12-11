package com.hollingsworth.arsnouveau.common.items;


import com.hollingsworth.arsnouveau.common.block.tile.SummoningCrystalTile;
import com.hollingsworth.arsnouveau.common.entity.EntitySylph;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class SylphCharm extends ModItem{

    public SylphCharm() {
        super(LibItemNames.WHIRLISPRIGE_CHARM);
    }

    /**
     * Called when this item is used when targetting a Block
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        if(world.getBlockEntity(blockpos) instanceof SummoningCrystalTile){
            EntitySylph sylph = new EntitySylph(world, true, blockpos);
            sylph.setPos(blockpos.getX(), blockpos.getY() + 1.0, blockpos.getZ());
            world.addFreshEntity(sylph);
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
