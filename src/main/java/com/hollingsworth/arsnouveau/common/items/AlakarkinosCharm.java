package com.hollingsworth.arsnouveau.common.items;

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

import java.util.List;

public class AlakarkinosCharm extends ModItem {

    public AlakarkinosCharm() {
        super(defaultProps().component(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, new PersistentFamiliarData().setColor("red")));
    }


    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level world = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        if (world.isClientSide) return InteractionResult.SUCCESS;
        pos = pos.relative(pContext.getClickedFace());
        Alakarkinos alakarkinos = new Alakarkinos(world, pos, true);
        PersistentFamiliarData data = pContext.getItemInHand().get(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA);
        if(data != null) {
            alakarkinos.fromCharmData(data);
        }
        world.addFreshEntity(alakarkinos);
        pContext.getItemInHand().shrink(1);
        return super.useOn(pContext);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        stack.addToTooltip(DataComponentRegistry.PERSISTENT_FAMILIAR_DATA, context, tooltip2::add, flagIn);
    }
}
