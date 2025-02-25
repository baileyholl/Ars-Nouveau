package com.hollingsworth.arsnouveau.client.events;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.registry.DynamicTooltipRegistry;
import com.hollingsworth.arsnouveau.client.gui.DocItemTooltipHandler;
import com.hollingsworth.arsnouveau.client.gui.SchoolTooltip;
import com.hollingsworth.arsnouveau.client.gui.SpellTooltip;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.renderer.world.PantomimeRenderer;
import com.hollingsworth.arsnouveau.common.block.tile.ArchwoodChestTile;
import com.hollingsworth.arsnouveau.common.block.tile.GhostWeaveTile;
import com.hollingsworth.arsnouveau.common.block.tile.SkyBlockTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.NotNull;


@EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class ClientEvents {

    @EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID, bus = EventBusSubscriber.Bus.MOD)
    static class ClientModEvents {
        @SubscribeEvent
        public static void registerTooltipFactory(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(SpellTooltip.class, SpellTooltip.SpellTooltipRenderer::new);
            event.register(SchoolTooltip.class, SchoolTooltip.SchoolTooltipRenderer::new);
        }


        @SubscribeEvent
        public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
            event.registerItem(new IClientItemExtensions() {
                @Override
                public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                    Minecraft mc = Minecraft.getInstance();

                    return new BlockEntityWithoutLevelRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels()) {
                        private final BlockEntity tile = new ArchwoodChestTile(BlockPos.ZERO, BlockRegistry.ARCHWOOD_CHEST.get().defaultBlockState());

                        @Override
                        public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext transformType, @NotNull PoseStack pose, @NotNull MultiBufferSource buffer, int x, int y) {
                            mc.getBlockEntityRenderDispatcher().renderItem(tile, pose, buffer, x, y);
                        }

                    };
                }
            }, BlockRegistry.ARCHWOOD_CHEST.get().asItem());
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
        DocItemTooltipHandler.onTooltip(e.getGraphics(), e.getItemStack(), e.getX(), e.getY());
    }

    @SubscribeEvent
    public static void addComponents(RenderTooltipEvent.GatherComponents event) {
        if (!Screen.hasShiftDown() && event.getItemStack().isEnchanted() && event.getItemStack().has(DataComponentRegistry.REACTIVE_CASTER)) {
            var caster = event.getItemStack().get(DataComponentRegistry.REACTIVE_CASTER);
            if (caster != null && caster.getSpell().isValid()) {
                event.getTooltipElements().add(Either.right(new SpellTooltip(caster)));
            }
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
        DynamicTooltipRegistry.appendTooltips(stack, event.getContext(), event.getToolTip()::add, event.getFlags());
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
