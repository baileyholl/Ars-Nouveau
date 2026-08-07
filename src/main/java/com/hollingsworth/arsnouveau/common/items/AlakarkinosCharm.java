package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.AbstractSummonCharm;
import com.hollingsworth.arsnouveau.client.jei.AliasProvider;
import com.hollingsworth.arsnouveau.common.block.tile.SummoningTile;
import com.hollingsworth.arsnouveau.common.entity.Alakarkinos;
import com.hollingsworth.arsnouveau.common.items.data.PersistentFamiliarData;
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

public class AlakarkinosCharm extends AbstractSummonCharm implements AliasProvider {

    public AlakarkinosCharm() {
        super(defaultProps().component(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, new PersistentFamiliarData().setColor("red")));
    }

    /**
     * @param context useContext
     * @param world   level
     * @param pos     position of the block
     * @return SUCCESS to consume item, PASS to ignore
     */
    @Override
    public InteractionResult useOnBlock(UseOnContext context, Level world, BlockPos pos) {
        Alakarkinos alakarkinos = new Alakarkinos(world, pos, true);
        alakarkinos.fromCharmData(context.getItemInHand().getOrDefault(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, new PersistentFamiliarData()));
        if (!context.isInside()) {
            pos = pos.relative(context.getClickedFace());
        }
        alakarkinos.setPos(pos.getBottomCenter());
        world.addFreshEntity(alakarkinos);
        return InteractionResult.SUCCESS;
    }

    /**
     * @param context useContext
     * @param world   level
     * @param tile    summoning tile
     * @param pos     position of the block
     * @return SUCCESS to consume item, PASS to ignore
     */
    @Override
    public InteractionResult useOnSummonTile(UseOnContext context, Level world, SummoningTile tile, BlockPos pos) {
        return useOnBlock(context, world, pos);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        stack.addToTooltip(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, context, tooltip2::add, flagIn);
    }

    @Override
    public Collection<Alias> getAliases() {
        return List.of(
                new Alias("archaeology", "Archaeology"),
                new Alias("sherd", "Sherd")
        );
    }
}
