package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.entity.AmethystGolem;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AmethystGolemCharm extends ModItem{
    public AmethystGolemCharm() {
        super();
    }

    public InteractionResult useOn(UseOnContext context) {
        if(context.getLevel().isClientSide)
            return InteractionResult.SUCCESS;
        Level world = context.getLevel();
        AmethystGolem carbuncle = new AmethystGolem(ModEntities.AMETHYST_GOLEM.get(), context.getLevel());
        Vec3 vec = context.getClickLocation();
        carbuncle.setPos(vec.x, vec.y, vec.z);
        world.addFreshEntity(carbuncle);
        context.getItemInHand().shrink(1);
        return InteractionResult.SUCCESS;
    }
}
