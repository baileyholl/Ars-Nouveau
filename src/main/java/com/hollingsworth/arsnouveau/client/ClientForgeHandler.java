package com.hollingsworth.arsnouveau.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.GuiManaHUD;
import com.hollingsworth.arsnouveau.client.gui.GuiSpellHUD;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class ClientForgeHandler {

    private static final GuiSpellHUD spellHUD = new GuiSpellHUD();
    private static final GuiManaHUD manaHUD = new GuiManaHUD();


    @SubscribeEvent
    public static void renderSpellHUD(final RenderGuiOverlayEvent.Post event) {
//        if (event.getType() != RenderGuiOverlayEvent.ElementType.ALL) return;
        spellHUD.drawHUD(event.getPoseStack());
        manaHUD.drawHUD(event.getPoseStack(), event.getPartialTick());
    }

    @SubscribeEvent
    public static void overlayEvent(RenderGuiOverlayEvent.Pre event) {
        if (Minecraft.getInstance().screen instanceof GuiRadialMenu) {
            // TODO: check disable overlay
//            if (event.getOverlay().overlay() == RenderGuiOverlayEvent.ElementType.LAYER) {
//                event.setCanceled(true);
//            }
        }
    }

    public static Component localize(String key, Object... params) {
        for (int i = 0; i < params.length; ++i) {
            Object parameter = params[i]; //to avoid ij dataflow warning
            if (parameter instanceof Component component && component.getContents() instanceof TranslatableContents translatableContents)
                params[i] = localize(translatableContents.getKey(), translatableContents.getArgs());
        }
        return Component.translatable(key, params);
    }
}
