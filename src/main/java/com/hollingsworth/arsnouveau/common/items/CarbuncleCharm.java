package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.block.tile.SummoningCrytalTile;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.common.entity.EntityWhelp;
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
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        EntityCarbuncle carbuncle = new EntityCarbuncle(world, true);
        carbuncle.setPosition(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
        world.addEntity(carbuncle);
        context.getItem().shrink(1);
        return ActionResultType.SUCCESS;
    }
}
