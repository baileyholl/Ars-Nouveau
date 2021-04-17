package com.hollingsworth.arsnouveau.client.gui;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
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

        final PlayerEntity player = minecraft.player;

        spellHUD.drawHUD(event.getMatrixStack());
        manaHUD.drawHUD(event.getMatrixStack(), event.getPartialTicks());

    }

    /**
     *
     *
     * @param event The event
     */
    @SubscribeEvent
    public static void renderEntityHUD(final RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        RayTraceResult mouseOver = Minecraft.getInstance().hitResult;
        if (mouseOver != null && mouseOver.getType() == RayTraceResult.Type.ENTITY) {

          EntityRayTraceResult result = (EntityRayTraceResult) mouseOver;
          if(result.getEntity() instanceof ITooltipProvider)
              entityHUD.drawHUD(event.getMatrixStack(),((ITooltipProvider) result.getEntity()).getTooltip());

          if(result.getEntity() instanceof ItemFrameEntity){
              scrollHUD.drawHUD(event.getMatrixStack(), (ItemFrameEntity) result.getEntity());
          }

        }
        if (mouseOver != null && mouseOver.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult result = (BlockRayTraceResult) mouseOver;
            BlockPos pos = result.getBlockPos();
            if(Minecraft.getInstance().level != null && Minecraft.getInstance().level.getBlockEntity(pos) instanceof ITooltipProvider){
                entityHUD.drawHUD(event.getMatrixStack(), ((ITooltipProvider) Minecraft.getInstance().level.getBlockEntity(pos)).getTooltip());
            }
        }
    }
}