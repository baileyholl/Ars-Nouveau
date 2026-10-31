package com.hollingsworth.arsnouveau.client.events;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.registry.ModKeyBindings;
import com.hollingsworth.arsnouveau.common.items.SchematicItem;
import com.hollingsworth.arsnouveau.common.items.data.SchematicData;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketCaptureSchematic;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.nuggets.client.area_capture.AreaCaptureHandler;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import java.util.UUID;

@EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class SchematicCaptureEvents {
    public static UUID lastSchematic = null;
    public static final AreaCaptureHandler CAPTURE_HANDLER = new AreaCaptureHandler(ArsNouveau.MODID, ModKeyBindings.TOOL_FOCUS,
            (bounds, handler) -> {
                LocalPlayer player = Minecraft.getInstance().player;
                SchematicData data = SchematicItem.getHeldSchematic(player).get(DataComponentRegistry.SCHEMATIC);
                if (data != null && SchematicItem.saveSchematic(player, bounds, data.uuid())) {
                    Networking.sendToServer(new PacketCaptureSchematic(new BlockPos(bounds.minX(), bounds.minY(), bounds.minZ()), new BlockPos(bounds.maxX(), bounds.maxY(), bounds.maxZ())));
                }
            });

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null){
            CAPTURE_HANDLER.cancelCapture();
            return;
        }
        if(SchematicItem.getHeldSchematic(player).is(ItemsRegistry.SCHEMATIC.get())) {
            SchematicData data = SchematicItem.getHeldSchematic(player).get(DataComponentRegistry.SCHEMATIC);
            if(lastSchematic != null && !lastSchematic.equals(data.uuid())){
                CAPTURE_HANDLER.cancelCapture();
            }
            lastSchematic = SchematicItem.getHeldSchematic(player).get(DataComponentRegistry.SCHEMATIC).uuid();
        }
        if(shouldShowVisualizer()){
            CAPTURE_HANDLER.tick();
            CAPTURE_HANDLER.toolKeyHit(ModKeyBindings.TOOL_FOCUS.isDown());
        }
    }

    @SubscribeEvent
    public static void renderBoundary(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY && shouldShowVisualizer()) {
            CAPTURE_HANDLER.renderBoundary(event.getPoseStack(), event.getModelViewMatrix());
        }
    }

    @SubscribeEvent
    public static void renderInstructions(RenderGuiLayerEvent.Post event) {
        if (event.getName().equals(VanillaGuiLayers.CROSSHAIR) && shouldShowVisualizer()) {
            CAPTURE_HANDLER.renderInstructions(event.getGuiGraphics(), Minecraft.getInstance().getWindow());
        }
    }

    @SubscribeEvent
    public static void mouseScrolled(InputEvent.MouseScrollingEvent event) {
        if (shouldShowVisualizer() && CAPTURE_HANDLER.mouseScrolled(event.getScrollDeltaY())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void mouseClicked(InputEvent.MouseButton.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (shouldShowVisualizer() && CAPTURE_HANDLER.showBoundary && minecraft.screen == null && event.getAction() == InputConstants.PRESS && minecraft.options.keyUse.matchesMouse(event.getButton())) {
            CAPTURE_HANDLER.rightClickEvent();
            event.setCanceled(true);
        }
    }

    private static boolean shouldShowVisualizer(){
        Player player = Minecraft.getInstance().player;
        return player != null && SchematicItem.getHeldSchematic(player).is(ItemsRegistry.SCHEMATIC.get());
    }
}
