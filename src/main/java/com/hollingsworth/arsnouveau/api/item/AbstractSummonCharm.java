package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.common.block.tile.SummoningTile;
import com.hollingsworth.arsnouveau.common.items.ModItem;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.Item.Properties;

public abstract class AbstractSummonCharm extends ModItem {

    public AbstractSummonCharm(Properties properties) {
        super(properties);
    }

    public AbstractSummonCharm() {
        this(ItemsRegistry.defaultItemProperties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide) return InteractionResult.SUCCESS;
        BlockPos pos = context.getClickedPos();
        InteractionResult result;
        if (world.getBlockEntity(pos) instanceof SummoningTile tile) {
            result = useOnSummonTile(context, world, tile, pos);
        } else {
            result = useOnBlock(context, world, pos);
        }
        if (result == InteractionResult.SUCCESS) context.getItemInHand().shrink(1);

        return result;
    }

    /**
     * @param context useContext
     * @param world   level
     * @param pos     position of the block
     * @return SUCCESS to consume item, PASS to ignore
     */
    public abstract InteractionResult useOnBlock(UseOnContext context, Level world, BlockPos pos);

    /**
     * @param context useContext
     * @param world   level
     * @param tile    summoning tile
     * @param pos     position of the block
     * @return SUCCESS to consume item, PASS to ignore
     */
    public abstract InteractionResult useOnSummonTile(UseOnContext context, Level world, SummoningTile tile, BlockPos pos);

}
