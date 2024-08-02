package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.camera.ICameraMountable;
import com.hollingsworth.arsnouveau.common.items.data.ScryPosData;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ScryerScroll extends ModItem {

    public ScryerScroll() {
        super(defaultProps().component(DataComponentRegistry.SCRY_DATA, new ScryPosData(Optional.empty())));
        withTooltip(Component.translatable("tooltip.ars_nouveau.scryer_scroll"));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().isClientSide)
            return super.useOn(pContext);
        if (pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof ICameraMountable) {
            ScryPosData data = new ScryPosData(pContext.getClickedPos());
            pContext.getItemInHand().set(DataComponentRegistry.SCRY_DATA, data);
            PortUtil.sendMessage(pContext.getPlayer(), Component.translatable("ars_nouveau.scryer_scroll.bound", pContext.getClickedPos().getX() + ", " + pContext.getClickedPos().getY() + ", " + pContext.getClickedPos().getZ()));
        }
        return super.useOn(pContext);
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        ScryPosData data = stack.get(DataComponentRegistry.SCRY_DATA);
        var pos = data.pos().orElse(null);
        if (pos != null) {
            tooltip2.add(Component.translatable("ars_nouveau.scryer_scroll.bound", pos.getX() + ", " + pos.getY() + ", " + pos.getZ()));
        } else {
            tooltip2.add(Component.translatable("ars_nouveau.scryer_scroll.craft"));
        }
        super.appendHoverText(stack, context, tooltip2, flagIn);
    }
}
