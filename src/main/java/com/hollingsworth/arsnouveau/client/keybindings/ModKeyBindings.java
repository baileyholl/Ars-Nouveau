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


    public static final KeyMapping QC_1 = new KeyMapping("key.ars_nouveau.qc1", -1, CATEGORY);
    public static final KeyMapping QC_2 = new KeyMapping("key.ars_nouveau.qc2", -1, CATEGORY);
    public static final KeyMapping QC_3 = new KeyMapping("key.ars_nouveau.qc3", -1, CATEGORY);
    public static final KeyMapping QC_4 = new KeyMapping("key.ars_nouveau.qc4", -1, CATEGORY);
    public static final KeyMapping QC_5 = new KeyMapping("key.ars_nouveau.qc5", -1, CATEGORY);
    public static final KeyMapping QC_6 = new KeyMapping("key.ars_nouveau.qc6", -1, CATEGORY);
    public static final KeyMapping QC_7 = new KeyMapping("key.ars_nouveau.qc7", -1, CATEGORY);
    public static final KeyMapping QC_8 = new KeyMapping("key.ars_nouveau.qc8", -1, CATEGORY);
    public static final KeyMapping QC_9 = new KeyMapping("key.ars_nouveau.qc9", -1, CATEGORY);
    public static final KeyMapping QC_10 = new KeyMapping("key.ars_nouveau.qc10", -1, CATEGORY);

    public static int usedQuickSlot(int key){
        for(QuickSlot q : QuickSlot.VALUES){
            if(q.key().getKey().getValue()== key){
                return q.slot;
            }
        }
        return -1;
    }

    @SubscribeEvent
    public static void registerKeyBindings(final FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(OPEN_BOOK);
        ClientRegistry.registerKeyBinding(OPEN_RADIAL_HUD);
        ClientRegistry.registerKeyBinding(PREVIOUS_SLOT);
        ClientRegistry.registerKeyBinding(NEXT_SLOT);
        ClientRegistry.registerKeyBinding(QC_1);
        ClientRegistry.registerKeyBinding(QC_2);
        ClientRegistry.registerKeyBinding(QC_3);
        ClientRegistry.registerKeyBinding(QC_4);
        ClientRegistry.registerKeyBinding(QC_5);
        ClientRegistry.registerKeyBinding(QC_6);
        ClientRegistry.registerKeyBinding(QC_7);
        ClientRegistry.registerKeyBinding(QC_8);
        ClientRegistry.registerKeyBinding(QC_9);
        ClientRegistry.registerKeyBinding(QC_10);
    }

    public record QuickSlot(int slot, KeyMapping key) {
        public static final QuickSlot[] VALUES = new QuickSlot[]{
            new QuickSlot(1, QC_1),
            new QuickSlot(2, QC_2),
            new QuickSlot(3, QC_3),
            new QuickSlot(4, QC_4),
            new QuickSlot(5, QC_5),
            new QuickSlot(6, QC_6),
            new QuickSlot(7, QC_7),
            new QuickSlot(8, QC_8),
            new QuickSlot(9, QC_9),
            new QuickSlot(10, QC_10)
        };
    }
}