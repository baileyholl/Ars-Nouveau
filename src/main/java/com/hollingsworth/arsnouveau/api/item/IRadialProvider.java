package com.hollingsworth.arsnouveau.api.item;

import com.hollingsworth.arsnouveau.client.registry.ModKeyBindings;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface IRadialProvider {

    /**
     * The key press this radial provider responds to
     * @return the key code
     */
    @OnlyIn(Dist.CLIENT)
    default int forKey() {
        return ModKeyBindings.OPEN_RADIAL_HUD.getKey().getValue();
    }

    /**
     * When the key is clicked to open the menu
     */
    @OnlyIn(Dist.CLIENT)
    void onRadialKeyPressed(ItemStack stack, Player player);
}
