package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.block.tile.DrygmyTile;
import com.hollingsworth.arsnouveau.common.entity.EntityDrygmy;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class DrygmyCharm extends ModItem{
    public DrygmyCharm(Properties properties) {
        super(properties);
    }

    public DrygmyCharm(Properties properties, String registryName) {
        super(properties, registryName);
    }

    public DrygmyCharm(String registryName) {
        super(registryName);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if(world.isClientSide)
            return InteractionResult.SUCCESS;
        BlockPos pos = context.getClickedPos();
        if(world.getBlockState(pos).getBlock() == Blocks.MOSSY_COBBLESTONE){
            world.setBlockAndUpdate(pos, BlockRegistry.DRYGMY_BLOCK.defaultBlockState());
            context.getItemInHand().shrink(1);
        }else if(world.getBlockEntity(pos) instanceof DrygmyTile) {
            EntityDrygmy drygmy = new EntityDrygmy(world, true);
            drygmy.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
            world.addFreshEntity(drygmy);
            drygmy.homePos = new BlockPos(pos);
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
