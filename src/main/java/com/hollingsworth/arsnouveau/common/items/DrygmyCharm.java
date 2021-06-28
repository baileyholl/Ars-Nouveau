package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.block.tile.DrygmyTile;
import com.hollingsworth.arsnouveau.common.entity.EntityDrygmy;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if(world.isClientSide)
            return ActionResultType.SUCCESS;
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
        return ActionResultType.SUCCESS;
    }
}
