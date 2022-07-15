package com.hollingsworth.arsnouveau.client.keybindings;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;


@Mod.EventBusSubscriber(modid = ArsNouveau.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModKeyBindings {

    private static final String CATEGORY = "key.category.ars_nouveau.general";

    public static final KeyMapping OPEN_BOOK = new KeyMapping("key.ars_nouveau.open_book", GLFW.GLFW_KEY_C, CATEGORY);
    public static final KeyMapping OPEN_RADIAL_HUD = new KeyMapping("key.ars_nouveau.selection_hud", GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyMapping NEXT_SLOT = new KeyMapping("key.ars_nouveau.next_slot",
            GLFW.GLFW_KEY_X,
            CATEGORY);

    public static final KeyMapping PREVIOUS_SLOT = new KeyMapping("key.ars_nouveau.previous_slot",
            GLFW.GLFW_KEY_Z,
            CATEGORY);


    @SubscribeEvent
    public static void registerKeyBindings(final FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(OPEN_BOOK);
        ClientRegistry.registerKeyBinding(OPEN_RADIAL_HUD);
        ClientRegistry.registerKeyBinding(PREVIOUS_SLOT);
        ClientRegistry.registerKeyBinding(NEXT_SLOT);
    }
}