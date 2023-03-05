package com.hollingsworth.arsnouveau.common.items.summon_charms;

import com.hollingsworth.arsnouveau.common.block.tile.StorageLecternTile;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BookwyrmCharm extends ModItem {
    public BookwyrmCharm() {
        super();
    }


    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level world = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        if (world.isClientSide) return InteractionResult.SUCCESS;
        if (world.getBlockEntity(pos) instanceof StorageLecternTile tile) {
            if(tile.addBookwyrm()){
                pContext.getItemInHand().shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(pContext);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }
}
