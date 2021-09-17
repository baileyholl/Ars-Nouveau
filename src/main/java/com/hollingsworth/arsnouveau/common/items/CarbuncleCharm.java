package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class CarbuncleCharm extends ModItem{
    public CarbuncleCharm() {
        super(LibItemNames.CARBUNCLE_CHARM);
    }

    /**
     * Called when this item is used when targetting a Block
     */
    public ActionResultType useOn(ItemUseContext context) {
        if(context.getLevel().isClientSide)
            return ActionResultType.SUCCESS;
        World world = context.getLevel();
        EntityCarbuncle carbuncle = new EntityCarbuncle(world, true);
        Vector3d vec = context.getClickLocation();
        carbuncle.setPos(vec.x, vec.y, vec.z);
        world.addFreshEntity(carbuncle);
        context.getItemInHand().shrink(1);
        return ActionResultType.SUCCESS;
    }
}
