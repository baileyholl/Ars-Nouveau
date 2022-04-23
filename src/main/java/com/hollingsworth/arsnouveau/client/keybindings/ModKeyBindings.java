package com.hollingsworth.arsnouveau.client.keybindings;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

/**
 * Registers this mod's {@link KeyBinding}s.
 *
 * @author Choonster
 */
@Mod.EventBusSubscriber(modid = ArsNouveau.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModKeyBindings {

    private static final String CATEGORY = "key.category.ars_nouveau.general";

    public static final KeyBinding OPEN_BOOK = new KeyBinding("key.ars_nouveau.open_book", GLFW.GLFW_KEY_C, CATEGORY);
    public static final KeyBinding OPEN_SPELL_SELECTION = new KeyBinding("key.ars_nouveau.selection_hud", GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyBinding NEXT_SLOT = new KeyBinding("key.ars_nouveau.next_slot",
            GLFW.GLFW_KEY_X,
            CATEGORY);

    public static final KeyBinding PREVIOUS__SLOT = new KeyBinding("key.ars_nouveau.previous_slot",
            GLFW.GLFW_KEY_Z,
            CATEGORY);


    @SubscribeEvent
    public static void registerKeyBindings(final FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(OPEN_BOOK);
        ClientRegistry.registerKeyBinding(OPEN_SPELL_SELECTION);
        ClientRegistry.registerKeyBinding(PREVIOUS__SLOT);
        ClientRegistry.registerKeyBinding(NEXT_SLOT);
    }
}