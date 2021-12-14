package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class StarbuncleCharm extends ModItem{
    public StarbuncleCharm() {
        super(LibItemNames.STARBUNCLE_CHARM);
    }

    /**
     * Called when this item is used when targetting a Block
     */
    public InteractionResult useOn(UseOnContext context) {
        if(context.getLevel().isClientSide)
            return InteractionResult.SUCCESS;
        Level world = context.getLevel();
        Starbuncle carbuncle = new Starbuncle(world, true);
        Vec3 vec = context.getClickLocation();
        carbuncle.setPos(vec.x, vec.y, vec.z);
        world.addFreshEntity(carbuncle);
        context.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }
}
