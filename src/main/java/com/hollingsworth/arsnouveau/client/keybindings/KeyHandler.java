package com.hollingsworth.arsnouveau.client.keybindings;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.IRadialProvider;
import com.hollingsworth.arsnouveau.api.item.ISpellHotkeyListener;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.registry.ModKeyBindings;
import com.hollingsworth.arsnouveau.common.network.*;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import static com.hollingsworth.arsnouveau.api.util.StackUtil.getHeldSpellbook;

@EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class KeyHandler {
    private static Minecraft mc() { return Minecraft.getInstance(); }
    public static KeyMapping[] CURIO_MAPPINGS = new KeyMapping[]{
            ModKeyBindings.HEAD_CURIO_HOTKEY
    };

    public static void checkKeysPressed(int key) {
        checkCurioHotkey(key);
        if (key == ModKeyBindings.FAMILIAR_TOGGLE.getKey().getValue() && !ModKeyBindings.FAMILIAR_TOGGLE.isUnbound()) {
            Networking.sendToServer(new PacketToggleFamiliar());
        }
        if (key == ModKeyBindings.OPEN_RADIAL_HUD.getKey().getValue() && !ModKeyBindings.OPEN_RADIAL_HUD.isUnbound()) {
            if (mc().screen instanceof GuiRadialMenu) {
                mc().player.closeContainer();
                return;
            }
        }
        checkCasterKeys(key);
    }

    public static void checkCasterKeys(int key) {
        if (key == -1)
            return;
        Player player = mc().player;
        ItemStack radialStack = StackUtil.getHeldRadial(player);
        if (radialStack.getItem() instanceof IRadialProvider radialProvider && key == ((IRadialProvider) radialStack.getItem()).forKey()) {
            if (mc().screen == null) {
                radialProvider.onRadialKeyPressed(radialStack, player);
                return;
            } else if (mc().screen instanceof GuiRadialMenu) {
                mc().player.closeContainer();
                return;
            }
        }

        InteractionHand hand = StackUtil.getHeldCasterTool(player);
        if (hand == null)
            return;
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty())
            return;
        if (!(stack.getItem() instanceof ISpellHotkeyListener hotkeyListener))
            return;

        if (key == ModKeyBindings.NEXT_SLOT.getKey().getValue()) {
            sendHotkeyPacket(PacketHotkeyPressed.Key.NEXT);
            return;
        }

        if (key == ModKeyBindings.PREVIOUS_SLOT.getKey().getValue()) {
            sendHotkeyPacket(PacketHotkeyPressed.Key.PREVIOUS);
            return;
        }


        if (key == ModKeyBindings.OPEN_BOOK.getKey().getValue()) {
            if (mc().screen instanceof GuiSpellBook && !((GuiSpellBook) mc().screen).spellNameBox.isFocused()) {
                mc().player.closeContainer();
                return;
            }

            if (mc().screen == null) {
                // TODO remove these exceptions and have casters tell which keys they capture
                ItemStack spellbook = getHeldSpellbook(player);
                if (!spellbook.isEmpty()) {
                    hotkeyListener.onOpenBookMenuKeyPressed(spellbook, player);
                } else if (stack.getItem() instanceof ISpellHotkeyListener listener) {
                    listener.onOpenBookMenuKeyPressed(stack, player);
                } else {
                    // Check other hand for book
                    InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                    ItemStack otherStack = player.getItemInHand(otherHand);
                    if (otherStack.getItem() instanceof ISpellHotkeyListener offhandListener) {
                        offhandListener.onOpenBookMenuKeyPressed(otherStack, player);
                    }
                }
            }
        }
        int slot = ModKeyBindings.usedQuickSlot(key);
        if (slot != -1) {
            Networking.sendToServer(new PacketQuickCast(slot));
        }
    }

    public static void checkCurioHotkey(int keyMapping) {
        for (KeyMapping mapping : CURIO_MAPPINGS) {
            if (mapping.getKey().getValue() == keyMapping) {
                IItemHandlerModifiable handler = CuriosUtil.getAllWornItems(mc().player);
                if (handler == null)
                    return;

                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack = handler.getStackInSlot(i);
                    if (stack.getItem() instanceof IRadialProvider radialProvider) {
                        if (mc().screen instanceof GuiRadialMenu) {
                            mc().player.closeContainer();
                        } else {
                            radialProvider.onRadialKeyPressed(stack, mc().player);
                        }
                    }
                }
                return;
            }
        }
    }

    @SubscribeEvent
    public static void mouseEvent(final InputEvent.MouseButton.Post event) {

        if (mc().player == null || event.getAction() != 1)
            return;
        if (mc().screen instanceof GuiRadialMenu<?> screen) {
            // 1.21.11: mouseClicked now takes MouseButtonEvent; simulate left-click at origin
            screen.mouseClicked(new MouseButtonEvent(0, 0, new MouseButtonInfo(0, 0)), false);
            return;
        }
        if (mc().screen == null)
            checkKeysPressed(event.getButton());
    }

    @SubscribeEvent
    public static void keyEvent(final InputEvent.Key event) {

        if (mc().player == null || event.getAction() != 1)
            return;
        if (mc().screen == null || mc().screen instanceof GuiRadialMenu)
            checkKeysPressed(event.getKey());
        if (event.getKey() == Minecraft.getInstance().options.keyJump.getKey().getValue()) {
            if (Minecraft.getInstance().player != null
                    && !Minecraft.getInstance().player.onGround()
                    && CuriosUtil.hasItem(Minecraft.getInstance().player, ItemsRegistry.JUMP_RING.get())
                    && Minecraft.getInstance().screen == null) {
                Networking.sendToServer(new PacketGenericClientMessage(PacketGenericClientMessage.Action.JUMP_RING));
            }
        }
    }

    public static void sendHotkeyPacket(PacketHotkeyPressed.Key key) {
        Networking.sendToServer(new PacketHotkeyPressed(key));
    }
}
