package com.hollingsworth.arsnouveau.client.keybindings;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;


@Mod.EventBusSubscriber(modid = ArsNouveau.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModKeyBindings {

    public static final String CATEGORY = "key.category.ars_nouveau.general";

    public static final KeyMapping OPEN_BOOK = new KeyMapping("key.ars_nouveau.open_book", GLFW.GLFW_KEY_C, CATEGORY);
    public static final KeyMapping OPEN_RADIAL_HUD = new KeyMapping("key.ars_nouveau.selection_hud", GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyMapping NEXT_SLOT = new KeyMapping("key.ars_nouveau.next_slot",
            GLFW.GLFW_KEY_X,
            CATEGORY);

    public static final KeyMapping PREVIOUS_SLOT = new KeyMapping("key.ars_nouveau.previous_slot",
            GLFW.GLFW_KEY_Z,
            CATEGORY);
    public static final KeyMapping HEAD_CURIO_HOTKEY = new KeyMapping("key.ars_nouveau.head_curio_hotkey",
            GLFW.GLFW_KEY_G,
            CATEGORY);
    public static final KeyMapping FAMILIAR_TOGGLE = new KeyMapping("key.ars_nouveau.familiar_toggle", -1, CATEGORY);
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
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_BOOK);
        event.register(OPEN_RADIAL_HUD);
        event.register(NEXT_SLOT);
        event.register(PREVIOUS_SLOT);
        event.register(HEAD_CURIO_HOTKEY);
        event.register(QC_1);
        event.register(QC_2);
        event.register(QC_3);
        event.register(QC_4);
        event.register(QC_5);
        event.register(QC_6);
        event.register(QC_7);
        event.register(QC_8);
        event.register(QC_9);
        event.register(QC_10);
        event.register(FAMILIAR_TOGGLE);
    }

    public record QuickSlot(int slot, KeyMapping key) {
        public static final QuickSlot[] VALUES = new QuickSlot[]{
            new QuickSlot(0, QC_1),
            new QuickSlot(1, QC_2),
            new QuickSlot(2, QC_3),
            new QuickSlot(3, QC_4),
            new QuickSlot(4, QC_5),
            new QuickSlot(5, QC_6),
            new QuickSlot(6, QC_7),
            new QuickSlot(7, QC_8),
            new QuickSlot(8, QC_9),
            new QuickSlot(9, QC_10)
        };
    }
}
