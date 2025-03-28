package com.hollingsworth.arsnouveau.common.items.summon_charms;

import com.hollingsworth.arsnouveau.api.item.AbstractSummonCharm;
import com.hollingsworth.arsnouveau.client.jei.AliasProvider;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningTile;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.items.data.StarbuncleCharmData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class StarbuncleCharm extends AbstractSummonCharm implements AliasProvider {
    public StarbuncleCharm() {
        super(defaultProps().component(DataComponentRegistry.STARBUNCLE_DATA, new StarbuncleCharmData()));
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

    @Override
    public Collection<Alias> getAliases() {
        return List.of(
            new Alias("hopper", "Hopper"),
            new Alias("pipe", "Pipe"),
            new Alias("item_transporter", "Item Transporter")
        );
    }



    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        StarbuncleCharmData data = stack.get(DataComponentRegistry.STARBUNCLE_DATA);
        if(data != null) {
            data.addToTooltip(context, tooltip2::add, flagIn);
        }
    }
}
