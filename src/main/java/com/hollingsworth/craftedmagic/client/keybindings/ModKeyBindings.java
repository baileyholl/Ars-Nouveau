package com.hollingsworth.craftedmagic.client.keybindings;

import com.hollingsworth.craftedmagic.ArsNouveau;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
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

    public static final KeyBinding OPEN_BOOK = new KeyBinding("key.ars_nouveau.open_book", KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_C, CATEGORY);

    @SubscribeEvent
    public static void registerKeyBindings(final FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(OPEN_BOOK);
    }
}