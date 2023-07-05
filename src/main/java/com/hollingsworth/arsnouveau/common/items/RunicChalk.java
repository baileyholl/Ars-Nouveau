package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class RunicChalk extends ModItem {
    public RunicChalk() {
        super(ItemsRegistry.defaultItemProperties().durability(15));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        if (world.isClientSide)
            return super.useOn(context);

        if (world.getBlockState(pos.above()).isAir()) {
            world.setBlockAndUpdate(pos.above(), BlockRegistry.RUNE_BLOCK.defaultBlockState());
            if (world.getBlockEntity(pos.above()) instanceof RuneTile) {
                ((RuneTile) world.getBlockEntity(pos.above())).uuid = context.getPlayer().getUUID();
            }
            context.getItemInHand().hurtAndBreak(1, context.getPlayer(), (t) -> {
            });
        }
        return InteractionResult.SUCCESS;
    }
}
