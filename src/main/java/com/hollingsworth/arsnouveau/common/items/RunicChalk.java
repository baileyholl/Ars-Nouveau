package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class RunicChalk extends ModItem {
    public RunicChalk() {
        super(ItemsRegistry.defaultItemProperties().durability(15));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide)
            return super.useOn(context);
        BlockPos target = context.getClickedPos().relative(context.getClickedFace());
        if (world.getBlockState(target).canBeReplaced()) {
            BlockState placement = BlockRegistry.RUNE_BLOCK.get().getStateForPlacement(new BlockPlaceContext(context));
            world.setBlockAndUpdate(target, placement);
            if (world.getBlockEntity(target) instanceof RuneTile runeTile) {
                runeTile.uuid = context.getPlayer().getUUID();
            }
            context.getItemInHand().hurtAndBreak(1, context.getPlayer(), (t) -> {
            });
        }
        return InteractionResult.SUCCESS;
    }
}
