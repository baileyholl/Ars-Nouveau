package com.hollingsworth.arsnouveau.client.events;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.registry.DynamicTooltipRegistry;
import com.hollingsworth.arsnouveau.api.registry.GenericRecipeRegistry;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.gui.DocItemTooltipHandler;
import com.hollingsworth.arsnouveau.client.gui.SchoolTooltip;
import com.hollingsworth.arsnouveau.client.gui.SpellTooltip;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.renderer.tile.PlanariumRenderer;
import com.hollingsworth.arsnouveau.client.renderer.world.PantomimeRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.PlanariumTile;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;


@EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class ClientEvents {

    /** Latest recipe map received from server via NeoForge RecipeContentPayload. Used by JEI. */
    public static RecipeMap clientRecipeMap = RecipeMap.EMPTY;

    @EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
    static class ClientModEvents {
        @SubscribeEvent
        public static void registerTooltipFactory(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(SpellTooltip.class, SpellTooltip.SpellTooltipRenderer::new);
            event.register(SchoolTooltip.class, SchoolTooltip.SchoolTooltipRenderer::new);
        }

        // TODO: 1.21.11 — IClientItemExtensions.getCustomRenderer() / BlockEntityWithoutLevelRenderer / renderByItem
        // have been removed. Archwood chest item rendering must be ported to the new SpecialModelRenderer
        // pipeline (NoDataSpecialModelRenderer + RegisterSpecialModelRendererEvent + item model JSON).
    }

    @SubscribeEvent
    public static void onRecipesUpdate(RecipesReceivedEvent event) {
        clientRecipeMap = event.getRecipeMap();
        GenericRecipeRegistry.reloadAll(event.getRecipeMap());
    }

    // 1.21.11: RenderLevelStageEvent.Stage enum removed — each stage is now its own concrete subclass.
    // Subscribe to the specific AfterTripwireBlocks subclass instead of checking getStage().
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderWorldLastEvent(final RenderLevelStageEvent.AfterTripwireBlocks event) {
        LightManager.updateAll(event.getLevelRenderer());
        PantomimeRenderer.renderOutline(event.getPoseStack());

        // Build then render so sorted transparency works correctly
        for (WeakReference<PlanariumTile> renderer : PlanariumRenderer.deferredRenders) {
            PlanariumTile tile = renderer.get();
            if (tile != null) {
                PlanariumRenderer.buildRender(tile, event.getPoseStack(), Minecraft.getInstance().player);
            }
        }

        // 1.21.11: getProjectionMatrix() removed from RenderLevelStageEvent; passing null as projection
        // matrix is safe here because drawRender's rendering path is currently fully stubbed out.
        for (WeakReference<PlanariumTile> renderer : PlanariumRenderer.deferredRenders) {
            PlanariumTile tile = renderer.get();
            if (tile != null) {
                PlanariumRenderer.drawRender(tile, event.getPoseStack(), null, event.getModelViewMatrix(), Minecraft.getInstance().player);
            }
        }
        PlanariumRenderer.deferredRenders = new ArrayList<>(8);
//        for (LevelInLevelRenderer renderer : LevelInLevelRenderer.renderers.values()) {
//            renderer.onRenderStage(event);
//        }
    }

//    @SubscribeEvent
//    public static void onClientTick(ClientTickEvent.Post event) {
//        for (LevelInLevelRenderer renderer : LevelInLevelRenderer.renderers.values()) {
//            renderer.onClientTick(event);
//        }
//    }

    @SubscribeEvent
    public static void TooltipEvent(RenderTooltipEvent.Pre e) {
        DocItemTooltipHandler.onTooltip(e.getGraphics(), e.getItemStack(), e.getX(), e.getY());
    }

    private static Slot slotUnderMouse = null;

    // ContainerScreenEvent.Render.Background removed in NeoForge 1.21.11 — Background no longer fired
    @SubscribeEvent
    public static void containerRenderForegroundForSlotTracking(ContainerScreenEvent.Render.Foreground e) {
        var screen = e.getContainerScreen();
        slotUnderMouse = screen.getSlotUnderMouse();
    }

    @SubscribeEvent
    public static void containerRenderForeground(ContainerScreenEvent.Render.Foreground e) {
        var screen = e.getContainerScreen();
        if (slotUnderMouse != screen.getSlotUnderMouse()) {
            DocItemTooltipHandler.resetLexiconLookupTime();
        }
    }

    @SubscribeEvent
    public static void addComponents(RenderTooltipEvent.GatherComponents event) {
        if (!Minecraft.getInstance().hasShiftDown() && event.getItemStack().isEnchanted() && event.getItemStack().has(DataComponentRegistry.REACTIVE_CASTER)) {
            var caster = event.getItemStack().get(DataComponentRegistry.REACTIVE_CASTER);
            if (caster != null && caster.getSpell().isValid()) {
                event.getTooltipElements().add(Either.right(new SpellTooltip(caster)));
            }
        }
        if (event.getItemStack().has(DataComponentRegistry.PRESTIDIGITATION)) {
            event.getTooltipElements().add(Either.left(Component.translatable("ars_nouveau.prestidigitation.tooltip")));
        }
    }

    // TODO: RenderHighlightEvent removed in NeoForge 1.21.11 — replace with ExtractBlockOutlineRenderStateEvent
    // or CustomBlockOutlineRenderer to suppress the block outline for SkyBlockTile / GhostWeaveTile

    @SubscribeEvent
    public static void overlayEvent(RenderGuiLayerEvent.Pre event) {
        if (Minecraft.getInstance().screen instanceof GuiRadialMenu && event.getName().equals(VanillaGuiLayers.CROSSHAIR)) {
            event.setCanceled(true);
        }
    }


    @SubscribeEvent
    public static void onTooltip(final ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        DynamicTooltipRegistry.appendTooltips(stack, event.getContext(), event.getToolTip()::add, event.getFlags());
        for (var tooltip : ClientInfo.storageTooltip) {
            event.getToolTip().add(tooltip);
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

    public static final List<Predicate<RecipesReceivedEvent>> recipeChangeListeners = new ArrayList<>();

    @SubscribeEvent
    public static void onClientResourcesReload(RecipesReceivedEvent event) {
        recipeChangeListeners.removeIf(p -> !p.test(event));
    }

    @SubscribeEvent
    public static void clientPlayerLogin(ClientPlayerNetworkEvent.LoggingIn e) {
        if (e.getPlayer() != null) {
            if (Config.INFORM_LIGHTS.get()) {
                Player entity = e.getPlayer();
                PortUtil.sendMessage(entity, Component.translatable("ars_nouveau.light_message").withStyle(ChatFormatting.GOLD));
                Config.INFORM_LIGHTS.set(false);
                Config.INFORM_LIGHTS.save();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(final ClientPlayerNetworkEvent.LoggingOut loggingOut) {
        EventQueue.getClientQueue().clear();
        PlanariumTile.dimManager.entries.clear();
        PlanariumRenderer.structureRenderData = new HashMap<>();
        PlanariumRenderer.deferredRenders = new ArrayList<>();
    }
}
