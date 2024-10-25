package com.hollingsworth.arsnouveau.common.items.summon_charms;

import com.hollingsworth.arsnouveau.api.item.AbstractSummonCharm;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningTile;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.items.data.PersistentFamiliarData;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CauldronBlock;

public class WixieCharm extends AbstractSummonCharm {
    public WixieCharm() {
        super(ItemsRegistry.defaultItemProperties().component(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, new PersistentFamiliarData().setColor("black")));
    }

    /**
     * Called when this item is used when targetting a Block
     */
    public InteractionResult useOn(UseOnContext context) {
        return super.useOn(context);
    }

    @Override
    public InteractionResult useOnBlock(UseOnContext context, Level world, BlockPos pos) {
        if (world.getBlockState(pos).getBlock() instanceof CauldronBlock) {
            world.setBlockAndUpdate(pos, BlockRegistry.WIXIE_CAULDRON.defaultBlockState());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult useOnSummonTile(UseOnContext context, Level world, SummoningTile tile, BlockPos pos) {
        if (tile instanceof WixieCauldronTile cauldronTile) {
            if (cauldronTile.hasWixie()) {
                PortUtil.sendMessage(context.getPlayer(), Component.translatable("ars_nouveau.wixie.has_wixie"));
            } else {
                EntityWixie wixie = new EntityWixie(world, pos);
                wixie.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                wixie.fromCharmData(context.getItemInHand().getOrDefault(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, new PersistentFamiliarData()));
                world.addFreshEntity(wixie);
                cauldronTile.entityID = wixie.getId();
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

}
