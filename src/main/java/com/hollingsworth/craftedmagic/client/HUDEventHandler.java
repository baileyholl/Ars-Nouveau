package com.hollingsworth.craftedmagic.client;

import com.hollingsworth.craftedmagic.ArsNouveau;
import com.hollingsworth.craftedmagic.items.ModItems;
import com.hollingsworth.craftedmagic.client.gui.GuiManaHUD;
import com.hollingsworth.craftedmagic.client.gui.GuiSpellHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


/**
 * Renders this mod's HUDs.
 *
 * @author Choonster
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class HUDEventHandler {
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final GuiSpellHUD spellHUD = new GuiSpellHUD();
    private static final GuiManaHUD manaHUD = new GuiManaHUD();
    /**
     * Render the current spell when the SpellBook is held in the players hand
     *
     * @param event The event
     */
    @SubscribeEvent
    public static void renderSpellHUD(final RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        final PlayerEntity player = minecraft.player;
        if (player.getHeldItemMainhand().getItem() != ModItems.spellBook && player.getHeldItemOffhand().getItem() != ModItems.spellBook)
            return;

        spellHUD.drawHUD();
        manaHUD.drawHUD();
    }

//    @SubscribeEvent
//    public static void renderManaHUD(final RenderGameOverlayEvent.Post event) {
//        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
//
//        final PlayerEntity player = minecraft.player;
//        if (player.getHeldItemMainhand().getItem() != ModItems.spell && player.getHeldItemOffhand().getItem() != ModItems.spell)
//            return;
//
//        manaHUD.drawHUD();
//    }
}