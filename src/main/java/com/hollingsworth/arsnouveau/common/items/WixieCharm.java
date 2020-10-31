package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.block.WixieCauldron;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.CauldronBlock;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WixieCharm extends ModItem{
    public WixieCharm() {
        super(LibItemNames.WIXIE_CHARM);
    }

    /**
     * Called when this item is used when targetting a Block
     */
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        if(world.getBlockState(pos).getBlock() instanceof CauldronBlock){
            world.setBlockState(pos, BlockRegistry.WIXIE_CAULDRON.getDefaultState());
            context.getItem().shrink(1);
        }else if(world.getBlockState(pos).getBlock() instanceof WixieCauldron){
            EntityWixie wixie = new EntityWixie(world, true, pos);
            wixie.setPosition(pos.getX()+0.5, pos.getY() + 1.0, pos.getZ() +0.5);
            world.addEntity(wixie);
        }
        return ActionResultType.SUCCESS;
    }
}
