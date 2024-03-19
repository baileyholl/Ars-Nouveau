package com.hollingsworth.arsnouveau.client.events;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.PatchouliTooltipEvent;
import com.hollingsworth.arsnouveau.client.gui.SpellTooltip;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.renderer.world.PantomimeRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.GhostWeaveTile;
import com.hollingsworth.arsnouveau.common.block.tile.SkyBlockTile;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.hollingsworth.arsnouveau.common.spell.method.MethodPantomime;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class ClientEvents {

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    static class ClientModEvents {
        @SubscribeEvent
        public static void registerTooltipFactory(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(SpellTooltip.class, SpellTooltip.SpellTooltipRenderer::new);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderWorldLastEvent(final RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
            PantomimeRenderer.renderOutline(event.getPoseStack());
        }
    }

    @SubscribeEvent
    public static void TooltipEvent(RenderTooltipEvent.Pre e) {
        try {
            // Uses patchouli internals, don't crash if they change something :)
            PatchouliTooltipEvent.onTooltip(e.getGraphics().pose(), e.getItemStack(), e.getX(), e.getY());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void highlightBlockEvent(RenderHighlightEvent.Block e) {
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            BlockEntity be = level.getBlockEntity(e.getTarget().getBlockPos());
            if (be instanceof SkyBlockTile skyTile && !skyTile.showFacade()) {
                e.setCanceled(true);
            }
            if (be instanceof GhostWeaveTile ghostTile && ghostTile.isInvisible()) {
                e.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void overlayEvent(RenderGuiOverlayEvent.Pre event) {
        if (Minecraft.getInstance().screen instanceof GuiRadialMenu && event.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type()) {
            event.setCanceled(true);
        }
    }


    @SubscribeEvent
    public static void onTooltip(final ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        int level = stack.getEnchantmentLevel(EnchantmentRegistry.REACTIVE_ENCHANTMENT.get());
        if (level > 0 && new ReactiveCaster(stack).getSpell().isValid()) {
            Spell spell = new ReactiveCaster(stack).getSpell();
            event.getToolTip().add(Component.literal(spell.getDisplayString()));
        }

        Collections.addAll(event.getToolTip(), ClientInfo.storageTooltip);
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
