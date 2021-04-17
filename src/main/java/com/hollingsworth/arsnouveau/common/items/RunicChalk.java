package com.hollingsworth.arsnouveau.common.items;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RunicChalk extends ModItem{
    public RunicChalk() {
        super(ItemsRegistry.defaultItemProperties().durability(15),LibItemNames.RUNIC_CHALK);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        BlockPos pos = context.getClickedPos();
        World world = context.getLevel();
        if(world.isClientSide)
            return super.useOn(context);

        if(world.getBlockState(pos.above()).getMaterial() == Material.AIR){
            world.setBlockAndUpdate(pos.above(), BlockRegistry.RUNE_BLOCK.defaultBlockState());
            if(world.getBlockEntity(pos.above()) instanceof RuneTile){
                ((RuneTile) world.getBlockEntity(pos.above())).uuid = context.getPlayer().getUUID();
            }
            context.getItemInHand().hurtAndBreak(1, context.getPlayer(), (t)->{});
        }
        return ActionResultType.SUCCESS;
    }
}
