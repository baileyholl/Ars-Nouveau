package com.hollingsworth.craftedmagic.client;

import com.hollingsworth.craftedmagic.ExampleMod;
import com.hollingsworth.craftedmagic.ModItems;
import com.hollingsworth.craftedmagic.client.gui.GuiSpellHUD;
import javafx.geometry.Side;
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
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ExampleMod.MODID)
public class HUDEventHandler {
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final GuiSpellHUD spellHUD = new GuiSpellHUD();

    /**
     * Render the current spell when the SpellBook is held in the players hand
     *
     * @param event The event
     */
    @SubscribeEvent
    public static void renderChunkEnergyHUD(final RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        final PlayerEntity player = minecraft.player;
        if (player.getHeldItemMainhand().getItem() != ModItems.spell && player.getHeldItemOffhand().getItem() != ModItems.spell)
            return;

        spellHUD.drawHUD();
    }
}