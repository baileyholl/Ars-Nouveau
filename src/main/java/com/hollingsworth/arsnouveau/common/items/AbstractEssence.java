package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class AbstractEssence extends ModItem {

    public String name;

    public AbstractEssence(String name) {
        super();
        this.name = name;
        withTooltip(Component.translatable("tooltip.ars_nouveau.essences"));
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level worldIn = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (worldIn.getBlockEntity(pos) instanceof RuneTile runeTile) {
            runeTile.pattern = name;
            runeTile.updateBlock();
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }
}
