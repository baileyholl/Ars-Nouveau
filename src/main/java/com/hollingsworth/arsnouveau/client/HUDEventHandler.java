package com.hollingsworth.arsnouveau.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.client.gui.GuiManaHUD;
import com.hollingsworth.arsnouveau.client.gui.GuiSpellHUD;
import com.hollingsworth.arsnouveau.common.entity.EntityWhelp;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

import java.util.List;


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
    /**
     * Render the current spell when the SpellBook is held in the players hand
     *
     * @param event The event
     */
    @SubscribeEvent
    public static void renderSpellHUD(final RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        final PlayerEntity player = minecraft.player;

        if ((StackUtil.getHeldSpellbook(player) == ItemStack.EMPTY))
            return;

        spellHUD.drawHUD();
        manaHUD.drawHUD();

    }

    /**
     * Render the current spell when the SpellBook is held in the players hand
     *
     * @param event The event
     */
    @SubscribeEvent
    public static void renderEntityHUD(final RenderGameOverlayEvent.Post event) {
        RayTraceResult mouseOver = Minecraft.getInstance().objectMouseOver;
        if (mouseOver != null && mouseOver.getType() == RayTraceResult.Type.ENTITY) {

          EntityRayTraceResult result = (EntityRayTraceResult) mouseOver;
          if(result.getEntity() instanceof EntityWhelp)
              entityHUD.drawHUD((EntityWhelp)result.getEntity());


        }
    }


    public static void renderTooltip(int x, int y, List<String> tooltipData, int color, int color2) {
        boolean lighting = GL11.glGetBoolean(GL11.GL_LIGHTING);
        if (lighting)
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

        if (!tooltipData.isEmpty()) {
            int var5 = 0;
            int var6;
            int var7;
            FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
            for (var6 = 0; var6 < tooltipData.size(); ++var6) {
                var7 = fontRenderer.getStringWidth(tooltipData.get(var6));
                if (var7 > var5)
                    var5 = var7;
            }
            var6 = x + 12;
            var7 = y - 12;
            int var9 = 8;
            if (tooltipData.size() > 1)
                var9 += 2 + (tooltipData.size() - 1) * 10;
            float z = 300F;
            drawGradientRect(var6 - 3, var7 - 4, z, var6 + var5 + 3, var7 - 3, color2, color2);
            drawGradientRect(var6 - 3, var7 + var9 + 3, z, var6 + var5 + 3, var7 + var9 + 4, color2, color2);
            drawGradientRect(var6 - 3, var7 - 3, z, var6 + var5 + 3, var7 + var9 + 3, color2, color2);
            drawGradientRect(var6 - 4, var7 - 3, z, var6 - 3, var7 + var9 + 3, color2, color2);
            drawGradientRect(var6 + var5 + 3, var7 - 3, z, var6 + var5 + 4, var7 + var9 + 3, color2, color2);
            int var12 = (color & 0xFFFFFF) >> 1 | color & -16777216;
            drawGradientRect(var6 - 3, var7 - 3 + 1, z, var6 - 3 + 1, var7 + var9 + 3 - 1, color, var12);
            drawGradientRect(var6 + var5 + 2, var7 - 3 + 1, z, var6 + var5 + 3, var7 + var9 + 3 - 1, color, var12);
            drawGradientRect(var6 - 3, var7 - 3, z, var6 + var5 + 3, var7 - 3 + 1, color, color);
            drawGradientRect(var6 - 3, var7 + var9 + 2, z, var6 + var5 + 3, var7 + var9 + 3, var12, var12);

            GlStateManager.disableDepthTest();
            for (int var13 = 0; var13 < tooltipData.size(); ++var13) {
                String var14 = tooltipData.get(var13);
                fontRenderer.drawStringWithShadow(var14, var6, var7, -1);
                if (var13 == 0)
                    var7 += 2;
                var7 += 10;
            }
            GlStateManager.enableDepthTest();
        }
        if (!lighting)
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GlStateManager.color4f(1, 1, 1, 1);
    }

    public static void drawGradientRect(int par1, int par2, float z, int par3, int par4, int par5, int par6) {
        float var7 = (par5 >> 24 & 255) / 255F;
        float var8 = (par5 >> 16 & 255) / 255F;
        float var9 = (par5 >> 8 & 255) / 255F;
        float var10 = (par5 & 255) / 255F;
        float var11 = (par6 >> 24 & 255) / 255F;
        float var12 = (par6 >> 16 & 255) / 255F;
        float var13 = (par6 >> 8 & 255) / 255F;
        float var14 = (par6 & 255) / 255F;
        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator var15 = Tessellator.getInstance();
        BufferBuilder wr = var15.getBuffer();
        wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        wr.pos(par3, par2, z).color(var8, var9, var10, var7).endVertex();
        wr.pos(par1, par2, z).color(var8, var9, var10, var7).endVertex();
        wr.pos(par1, par4, z).color(var12, var13, var14, var11).endVertex();
        wr.pos(par3, par4, z).color(var12, var13, var14, var11).endVertex();
        var15.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableTexture();
    }
}