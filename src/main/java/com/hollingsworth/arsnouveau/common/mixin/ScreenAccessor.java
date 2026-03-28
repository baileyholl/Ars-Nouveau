package com.hollingsworth.arsnouveau.common.mixin;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * Mixin accessor for Screen.renderables — needed by nuggets BaseScreen utility class.
 * Exposes the protected renderables list for tooltip collection in BaseScreen.
 */
@Mixin(Screen.class)
public interface ScreenAccessor {
    @Accessor("renderables")
    List<Renderable> getRenderables();
}
