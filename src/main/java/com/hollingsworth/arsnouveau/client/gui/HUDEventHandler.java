package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;


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
    private static final GuiEntityInfoHUD entityHUD = new GuiEntityInfoHUD();
    private static final GuiScrollHUD scrollHUD = new GuiScrollHUD();
    /**
     * Render the current spell when the SpellBook is held in the players hand
     *
     * @param event The event
     */
    @SubscribeEvent
    public static void renderSpellHUD(final RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        final Player player = minecraft.player;

        spellHUD.drawHUD(event.getMatrixStack());
        manaHUD.drawHUD(event.getMatrixStack(), event.getPartialTicks());

    }

    @SubscribeEvent
    public static void renderEntityHUD(final RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        HitResult mouseOver = Minecraft.getInstance().hitResult;
        if (mouseOver != null && mouseOver.getType() == HitResult.Type.ENTITY) {

          EntityHitResult result = (EntityHitResult) mouseOver;
          if(result.getEntity() instanceof ITooltipProvider)
              entityHUD.drawHUD(event.getMatrixStack(),((ITooltipProvider) result.getEntity()).getTooltip(new ArrayList<>()));

          if(result.getEntity() instanceof ItemFrame){
              scrollHUD.drawHUD(event.getMatrixStack(), (ItemFrame) result.getEntity());
          }

        }
        if (mouseOver != null && mouseOver.getType() == HitResult.Type.BLOCK) {
            BlockHitResult result = (BlockHitResult) mouseOver;
            BlockPos pos = result.getBlockPos();
            if(Minecraft.getInstance().level != null && Minecraft.getInstance().level.getBlockEntity(pos) instanceof ITooltipProvider){
                entityHUD.drawHUD(event.getMatrixStack(), ((ITooltipProvider) Minecraft.getInstance().level.getBlockEntity(pos)).getTooltip(new ArrayList<>()));
            }
        }
    }
}