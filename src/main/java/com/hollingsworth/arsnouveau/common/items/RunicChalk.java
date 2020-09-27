package com.hollingsworth.arsnouveau.common.items;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RunicChalk extends ModItem{
    public RunicChalk() {
        super(ItemsRegistry.defaultItemProperties().maxDamage(15),LibItemNames.RUNIC_CHALK);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        BlockPos pos = context.getPos();
        World world = context.getWorld();
        if(world.isRemote)
            return super.onItemUse(context);

        if(world.getBlockState(pos.up()).getMaterial() == Material.AIR){
            world.setBlockState(pos.up(), BlockRegistry.RUNE_BLOCK.getDefaultState());
            if(world.getTileEntity(pos.up()) instanceof RuneTile){
                ((RuneTile) world.getTileEntity(pos.up())).uuid = context.getPlayer().getUniqueID();
            }
            context.getItem().damageItem(1, context.getPlayer(), (t)->{});
        }
        return ActionResultType.SUCCESS;
    }
}
