package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.block.BookwyrmLectern;
import com.hollingsworth.arsnouveau.common.block.tile.BookwyrmLecternTile;
import com.hollingsworth.arsnouveau.common.entity.EntityWhelp;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WhelpCharm extends ModItem{
    public WhelpCharm() {
        super(LibItemNames.WHELP_CHARM);
    }

    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if(world.getBlockState(pos).getBlock() == Blocks.LECTERN){
            world.setBlockAndUpdate(pos, BlockRegistry.BOOKWYRM_LECTERN.defaultBlockState().setValue(BookwyrmLectern.FACING, world.getBlockState(pos).getValue(LecternBlock.FACING)));
            context.getItemInHand().shrink(1);
        }else if(world.getBlockEntity(pos) instanceof BookwyrmLecternTile) {
            EntityWhelp whelp = new EntityWhelp(world, pos);
            whelp.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            world.addFreshEntity(whelp);
            context.getItemInHand().shrink(1);
        }
        return ActionResultType.SUCCESS;
    }
}
