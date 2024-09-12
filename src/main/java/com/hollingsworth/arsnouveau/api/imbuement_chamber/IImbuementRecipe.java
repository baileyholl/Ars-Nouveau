package com.hollingsworth.arsnouveau.api.imbuement_chamber;

import com.hollingsworth.arsnouveau.common.block.tile.ImbuementTile;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

public interface IImbuementRecipe extends Recipe<ImbuementTile> {
    int getSourceCost(ImbuementTile imbuementTile);

    default Component getCraftingStartedText(ImbuementTile imbuementTile) {
        return Component.translatable("ars_nouveau.imbuement.crafting_started", this.assemble(imbuementTile, imbuementTile.getLevel().registryAccess()).getHoverName());
    }

    default Component getCraftingText(ImbuementTile imbuementTile) {
        return Component.translatable("ars_nouveau.crafting", this.assemble(imbuementTile, imbuementTile.getLevel().registryAccess()).getHoverName());
    }

    default Component getCraftingProgressText(ImbuementTile imbuementTile, int progress) {
        return Component.translatable("ars_nouveau.crafting_progress", progress).withStyle(ChatFormatting.GOLD);
    }
}
