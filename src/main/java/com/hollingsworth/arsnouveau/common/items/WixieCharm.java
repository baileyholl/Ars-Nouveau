package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

public class WixieCharm extends ModItem{
    public WixieCharm() {
        super(LibItemNames.WIXIE_CHARM);
    }

    /**
     * Called when this item is used when targetting a Block
     */
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if(world.isClientSide)
            return InteractionResult.SUCCESS;
        BlockPos pos = context.getClickedPos();
        if(world.getBlockState(pos).getBlock() instanceof CauldronBlock){
            world.setBlockAndUpdate(pos, BlockRegistry.WIXIE_CAULDRON.defaultBlockState());
            context.getItemInHand().shrink(1);
        }else if(world.getBlockEntity(pos) instanceof WixieCauldronTile){
            WixieCauldronTile tile = (WixieCauldronTile) world.getBlockEntity(pos);
            if(!tile.hasWixie()){
                EntityWixie wixie = new EntityWixie(world, true, pos);
                wixie.setPos(pos.getX()+0.5, pos.getY() + 1.0, pos.getZ() +0.5);
                world.addFreshEntity(wixie);
                tile.entityID = wixie.getId();
                context.getItemInHand().shrink(1);
            }else{
                PortUtil.sendMessage(context.getPlayer(), new TranslatableComponent("ars_nouveau.wixie.has_wixie"));
            }
        }
        return InteractionResult.SUCCESS;
    }
}
