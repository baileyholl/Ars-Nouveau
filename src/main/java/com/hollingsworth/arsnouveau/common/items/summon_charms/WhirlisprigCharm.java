package com.hollingsworth.arsnouveau.common.items.summon_charms;


import com.hollingsworth.arsnouveau.api.item.AbstractSummonCharm;
import com.hollingsworth.arsnouveau.client.jei.AliasProvider;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningTile;
import com.hollingsworth.arsnouveau.common.block.tile.WhirlisprigTile;
import com.hollingsworth.arsnouveau.common.entity.Whirlisprig;
import com.hollingsworth.arsnouveau.common.items.data.PersistentFamiliarData;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.List;

public class WhirlisprigCharm extends AbstractSummonCharm implements AliasProvider {

    public WhirlisprigCharm() {
        super(ItemsRegistry.defaultItemProperties().component(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, new PersistentFamiliarData().setColor("summer")));
    }

    @Override
    public InteractionResult useOnBlock(UseOnContext context, Level world, BlockPos pos) {
        if (world.getBlockState(pos).is(BlockTags.FLOWERS)) {
            world.setBlockAndUpdate(pos, BlockRegistry.WHIRLISPRIG_FLOWER.defaultBlockState());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOnSummonTile(UseOnContext context, Level world, SummoningTile tile, BlockPos pos) {
        if (tile instanceof WhirlisprigTile) {
            Whirlisprig whirlisprig = new Whirlisprig(world, true, pos);
            whirlisprig.setPos(pos.above().getBottomCenter());
            whirlisprig.fromCharmData(context.getItemInHand().getOrDefault(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, new PersistentFamiliarData()));
            world.addFreshEntity(whirlisprig);
            whirlisprig.flowerPos = new BlockPos(pos);
            return InteractionResult.SUCCESS;
        } else return InteractionResult.PASS;
    }

    @Override
    public Collection<Alias> getAliases() {
        return List.of(
                new Alias("crop_farm", "Crop Farm")
        );
    }
}
