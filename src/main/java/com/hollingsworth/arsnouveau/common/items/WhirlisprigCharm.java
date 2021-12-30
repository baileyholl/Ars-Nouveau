package com.hollingsworth.arsnouveau.common.items;


import com.hollingsworth.arsnouveau.common.block.tile.DrygmyTile;
import com.hollingsworth.arsnouveau.common.entity.Whirlisprig;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class WhirlisprigCharm extends ModItem{

    public WhirlisprigCharm() {
        super(LibItemNames.WHIRLISPRIG_CHARM);
    }

    /**
     * Called when this item is used when targetting a Block
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if(world.isClientSide)
            return InteractionResult.SUCCESS;
        BlockPos pos = context.getClickedPos();
        if(world.getBlockState(pos).is(BlockTags.FLOWERS)){
            world.setBlockAndUpdate(pos, BlockRegistry.WHIRLISPRIG_FLOWER.defaultBlockState());
            context.getItemInHand().shrink(1);
        }else if(world.getBlockEntity(pos) instanceof DrygmyTile) {
            Whirlisprig whirlisprig = new Whirlisprig(world, true, pos);
            whirlisprig.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
            world.addFreshEntity(whirlisprig);
            whirlisprig.flowerPos = new BlockPos(pos);
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
