package com.hollingsworth.arsnouveau.common.items.summon_charms;

import com.hollingsworth.arsnouveau.api.item.AbstractSummonCharm;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningTile;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.items.data.StarbuncleCharmData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class StarbuncleCharm extends AbstractSummonCharm {
    public StarbuncleCharm() {
        super();
    }

    @Override
    public InteractionResult useOnBlock(UseOnContext context, Level world, BlockPos pos) {
        Starbuncle carbuncle = new Starbuncle(world, true);
        StarbuncleCharmData data = context.getItemInHand().getOrDefault(DataComponentRegistry.STARBUNCLE_DATA, new StarbuncleCharmData());
        pos = pos.relative(context.getClickedFace());
        carbuncle.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        carbuncle.data = data.mutable();
        world.addFreshEntity(carbuncle);
        carbuncle.restoreFromTag();
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOnSummonTile(UseOnContext context, Level world, SummoningTile tile, BlockPos pos) {
        return useOnBlock(context, world, pos);
    }
}
