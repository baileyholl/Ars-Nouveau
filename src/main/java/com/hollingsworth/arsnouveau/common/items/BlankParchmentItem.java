package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.camera.ICameraMountable;
import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class BlankParchmentItem extends ModItem implements IScribeable {
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

    @Override
    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack thisStack) {
        if(player.getItemInHand(handIn).getItem() instanceof ICasterTool casterTool){
            if(world.getBlockEntity(pos) instanceof ScribesTile scribesTile){
                scribesTile.setStack(new ItemStack(ItemsRegistry.SPELL_PARCHMENT.get()));
                if(scribesTile.getStack().getItem() instanceof IScribeable iScribeable){
                    iScribeable.onScribe(world, pos, player, handIn, scribesTile.getStack());
                }
                return true;
            }
        }
        return false;
    }
}
