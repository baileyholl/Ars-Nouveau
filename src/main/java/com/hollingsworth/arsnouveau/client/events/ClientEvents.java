package com.hollingsworth.arsnouveau.client.events;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.PatchouliTooltipEvent;
import com.hollingsworth.arsnouveau.client.gui.SpellTooltip;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.common.block.tile.GhostWeaveTile;
import com.hollingsworth.arsnouveau.common.block.tile.SkyBlockTile;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class ClientEvents {

    @EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID, bus = EventBusSubscriber.Bus.MOD)
    static class ClientModEvents {
        @SubscribeEvent
        public static void registerTooltipFactory(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(SpellTooltip.class, SpellTooltip.SpellTooltipRenderer::new);
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
    public static void overlayEvent(RenderGuiLayerEvent.Pre event) {
        if (Minecraft.getInstance().screen instanceof GuiRadialMenu && event.getName().equals(VanillaGuiLayers.CROSSHAIR)) {
            event.setCanceled(true);
        }
    }


    @SubscribeEvent
    public static void onTooltip(final ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        //todo: reenable reactive
//        int level = stack.getEnchantmentLevel(event.getEntity().level.holderOrThrow(EnchantmentRegistry.REACTIVE_ENCHANTMENT));
//        var reactiveCaster = stack.get(DataComponentRegistry.REACTIVE_CASTER);
//
//        if (reactiveCaster != null && level > 0 && reactiveCaster.getSpell().isValid()) {
//            Spell spell = reactiveCaster.getSpell();
//            event.getToolTip().add(Component.literal(spell.getDisplayString()));
//        }
//
//        Collections.addAll(event.getToolTip(), ClientInfo.storageTooltip);
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
