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
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EarthEssence extends AbstractEssence {

    public EarthEssence() {
        super("earth");
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext pContext) {
        if (pContext.getPlayer() != null && pContext.getPlayer().level.isClientSide) {
            return super.useOn(pContext);
        }

        BlockState state = pContext.getLevel().getBlockState(pContext.getClickedPos());
        if (state.is(BlockTags.DIRT) && !state.is(Blocks.GRASS_BLOCK)) {
            pContext.getLevel().setBlock(pContext.getClickedPos(), Blocks.GRASS_BLOCK.defaultBlockState(), 3);
            if (!pContext.getPlayer().hasInfiniteMaterials()) {
                pContext.getItemInHand().shrink(1);
            }
            return InteractionResult.SUCCESS;
        }

        return super.useOn(pContext);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        tooltip2.add(Component.translatable("ars_nouveau.earth_essence.tooltip").withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
    }
}
