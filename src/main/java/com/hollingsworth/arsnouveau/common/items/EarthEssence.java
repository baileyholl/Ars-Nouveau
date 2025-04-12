package com.hollingsworth.arsnouveau.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EarthEssence extends ModItem {

    public EarthEssence() {
        super();
        withTooltip(Component.translatable("tooltip.ars_nouveau.essences"));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {

        if (pContext.getPlayer().level.isClientSide) {
            return super.useOn(pContext);
        }

        if (pContext.getLevel().getBlockState(pContext.getClickedPos()).is(BlockTags.DIRT)) {
            pContext.getLevel().setBlock(pContext.getClickedPos(), Blocks.GRASS_BLOCK.defaultBlockState(), 3);
            if (!pContext.getPlayer().hasInfiniteMaterials()) {
                pContext.getItemInHand().shrink(1);
            }
        }

        return super.useOn(pContext);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        tooltip2.add(Component.translatable("ars_nouveau.earth_essence.tooltip").withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
    }
}
