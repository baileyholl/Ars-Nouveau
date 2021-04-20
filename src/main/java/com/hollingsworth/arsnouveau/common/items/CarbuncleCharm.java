package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CarbuncleCharm extends ModItem{
    public CarbuncleCharm() {
        super(LibItemNames.CARBUNCLE_CHARM);
    }

    /**
     * Called when this item is used when targetting a Block
     */
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        EntityCarbuncle carbuncle = new EntityCarbuncle(world, true);
        carbuncle.setPos(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
        world.addFreshEntity(carbuncle);
        context.getItemInHand().shrink(1);
        return ActionResultType.SUCCESS;
    }
}
