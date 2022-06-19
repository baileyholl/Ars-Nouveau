package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.block.BookwyrmLectern;
import com.hollingsworth.arsnouveau.common.block.tile.BookwyrmLecternTile;
import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LecternBlock;

public class BookwyrmCharm extends ModItem{
    public BookwyrmCharm() {
        super();
    }

    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if(world.getBlockState(pos).getBlock() == Blocks.LECTERN){
            world.setBlockAndUpdate(pos, BlockRegistry.BOOKWYRM_LECTERN.defaultBlockState().setValue(BookwyrmLectern.FACING, world.getBlockState(pos).getValue(LecternBlock.FACING)));
            context.getItemInHand().shrink(1);
        }else if(world.getBlockEntity(pos) instanceof BookwyrmLecternTile) {
            EntityBookwyrm whelp = new EntityBookwyrm(world, pos);
            whelp.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            world.addFreshEntity(whelp);
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
