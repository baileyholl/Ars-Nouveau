package com.hollingsworth.arsnouveau.client.keybindings;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ISpellHotkeyListener;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketHotkeyPressed;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class KeyHandler {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    public static void checkKeysPressed(int key) {
        if (key == ModKeyBindings.OPEN_RADIAL_HUD.getKey().getValue()) {
            if (MINECRAFT.screen instanceof GuiRadialMenu) {
                MINECRAFT.player.closeContainer();
                return;
            }
        }

        Player player = MINECRAFT.player;
        InteractionHand hand = StackUtil.getHeldCasterTool(player);
        if (hand == null)
            return;
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty() || !(stack.getItem() instanceof ISpellHotkeyListener hotkeyListener))
            return;

        if (key == ModKeyBindings.NEXT_SLOT.getKey().getValue()) {
            sendHotkeyPacket(PacketHotkeyPressed.Key.NEXT);
            return;
        }

        if (key == ModKeyBindings.PREVIOUS_SLOT.getKey().getValue()) {
            sendHotkeyPacket(PacketHotkeyPressed.Key.PREVIOUS);
            return;
        }

        if (key == ModKeyBindings.OPEN_RADIAL_HUD.getKey().getValue()) {
            if (MINECRAFT.screen == null) {
                hotkeyListener.onRadialKeyPressed(stack, player);
                return;
            }
        }

        if (key == ModKeyBindings.OPEN_BOOK.getKey().getValue()) {
            if (MINECRAFT.screen instanceof GuiSpellBook && !((GuiSpellBook) MINECRAFT.screen).spell_name.isFocused()) {
                MINECRAFT.player.closeContainer();
                return;
            }

            if (MINECRAFT.screen == null) {
                hotkeyListener.onOpenBookMenuKeyPressed(stack, player);
            }
        }
    }

    @SubscribeEvent
    public static void mouseEvent(final InputEvent.Post event) {

        if (MINECRAFT.player == null || MINECRAFT.screen != null || event.getAction() != 1)
            return;
        checkKeysPressed(event.getButton());
    }

    @SubscribeEvent
    public static void keyEvent(final InputEvent.Key event) {
        if (MINECRAFT.player == null || MINECRAFT.screen != null || event.getAction() != 1)
            return;
        checkKeysPressed(event.getKey());

    }

    public static void sendHotkeyPacket(PacketHotkeyPressed.Key key) {
        Networking.INSTANCE.sendToServer(new PacketHotkeyPressed(key));
    }
}
