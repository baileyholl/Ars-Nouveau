package com.hollingsworth.arsnouveau.api.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IRadialProvider {

    /**
     * The key press this radial provider responds to
     * @return the key code
     */
    @OnlyIn(Dist.CLIENT)
    int forKey();

    /**
     * When the key is clicked to open the menu
     */
    @OnlyIn(Dist.CLIENT)
    void onRadialKeyPressed(ItemStack stack, Player player);
}
