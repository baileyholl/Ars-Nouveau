package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class HUDEventHandler {
    private static final GuiSpellHUD spellHUD = new GuiSpellHUD();
    private static final GuiManaHUD manaHUD = new GuiManaHUD();

    /**
     * Render the current spell when the SpellBook is held in the players hand
     *
     * @param event The event
     */
    @SubscribeEvent
    public static void renderSpellHUD(final RenderGuiOverlayEvent.Post event) {
        if (event.getType() != RenderGuiOverlayEvent.ElementType.ALL) return;
        spellHUD.drawHUD(event.getPoseStack());
        manaHUD.drawHUD(event.getPoseStack(), event.getPartialTick());
    }
}