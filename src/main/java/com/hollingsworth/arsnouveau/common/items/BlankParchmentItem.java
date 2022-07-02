package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.camera.ICameraMountable;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class BlankParchmentItem extends ModItem {
    public BlankParchmentItem(Properties properties) {
        super(properties);
    }


    public BlankParchmentItem() {
        super();
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().isClientSide)
            return super.useOn(pContext);
        if (pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof ICameraMountable) {
            ItemStack stack = new ItemStack(ItemsRegistry.SCRYER_SCROLL.get());
            ScryerScroll.ScryerScrollData data = new ScryerScroll.ScryerScrollData(stack);
            data.setPos(pContext.getClickedPos(), stack);
            if (!pContext.getPlayer().addItem(stack)) {
                pContext.getLevel().addFreshEntity(new ItemEntity(pContext.getLevel(), pContext.getPlayer().getX(), pContext.getPlayer().getY(), pContext.getPlayer().getZ(), stack));
            }
            pContext.getItemInHand().shrink(1);
            return InteractionResult.SUCCESS;
        }
        return super.useOn(pContext);
    }
}
