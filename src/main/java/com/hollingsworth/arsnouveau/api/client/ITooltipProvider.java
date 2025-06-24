package com.hollingsworth.arsnouveau.api.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// @Deprecated use Nuggets version
@Deprecated(forRemoval = true)
public interface ITooltipProvider {

    /**
     * A list of tool tips to render on the screen when looking at this target.
     */
    void getTooltip(List<Component> tooltip);

    @Nullable
    default TooltipComponent getTooltipImage() {
        return null;
    }

}
