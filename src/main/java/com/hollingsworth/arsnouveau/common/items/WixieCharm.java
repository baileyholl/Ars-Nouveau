package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.CauldronBlock;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class WixieCharm extends ModItem{
    public WixieCharm() {
        super(LibItemNames.WIXIE_CHARM);
    }

    /**
     * Called when this item is used when targetting a Block
     */
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if(world.isClientSide)
            return ActionResultType.SUCCESS;
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
                PortUtil.sendMessage(context.getPlayer(), new TranslationTextComponent("ars_nouveau.wixie.has_wixie"));
            }
        }
        return ActionResultType.SUCCESS;
    }
}
