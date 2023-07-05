package com.hollingsworth.arsnouveau.client.keybindings;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.IRadialProvider;
import com.hollingsworth.arsnouveau.api.item.ISpellHotkeyListener;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.registry.ModKeyBindings;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketHotkeyPressed;
import com.hollingsworth.arsnouveau.common.network.PacketQuickCast;
import com.hollingsworth.arsnouveau.common.network.PacketToggleFamiliar;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.IItemHandlerModifiable;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class KeyHandler {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    public static KeyMapping[] CURIO_MAPPINGS = new KeyMapping[]{
            ModKeyBindings.HEAD_CURIO_HOTKEY
    };
    public static void checkKeysPressed(int key) {
        checkCurioHotkey(key);
        if(key == ModKeyBindings.FAMILIAR_TOGGLE.getKey().getValue()){
            Networking.sendToServer(new PacketToggleFamiliar());
        }
        if (key == ModKeyBindings.OPEN_RADIAL_HUD.getKey().getValue()) {
            if (MINECRAFT.screen instanceof GuiRadialMenu) {
                MINECRAFT.player.closeContainer();
                return;
            }
        }
        checkCasterKeys(key);
    }

    public static void checkCasterKeys(int key){
        if(key == -1)
            return;
        Player player = MINECRAFT.player;
        ItemStack radialStack = StackUtil.getHeldRadial(player);
        if(radialStack.getItem() instanceof IRadialProvider radialProvider && key == ((IRadialProvider) radialStack.getItem()).forKey()) {
            if (MINECRAFT.screen == null) {
                radialProvider.onRadialKeyPressed(radialStack, player);
                return;
            }else if(MINECRAFT.screen instanceof GuiRadialMenu){
                MINECRAFT.player.closeContainer();
                return;
            }
        }

        InteractionHand hand = StackUtil.getHeldCasterTool(player);
        if(hand == null)
            return;
        ItemStack stack = player.getItemInHand(hand);
        if(stack.isEmpty())
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
            if (MINECRAFT.screen instanceof GuiSpellBook && !((GuiSpellBook) MINECRAFT.screen).spell_name.isFocused()) {
                MINECRAFT.player.closeContainer();
                return;
            }

            if (MINECRAFT.screen == null) {
                // TODO remove these exceptions and have casters tell which keys they capture
                if(stack.getItem() instanceof SpellBook) {
                    hotkeyListener.onOpenBookMenuKeyPressed(stack, player);
                }else{
                    // Check other hand for book
                    InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                    ItemStack otherStack = player.getItemInHand(otherHand);
                    if(otherStack.getItem() instanceof ISpellHotkeyListener offhandListener){
                        offhandListener.onOpenBookMenuKeyPressed(otherStack, player);
                    }
                }
            }
        }
        int slot = ModKeyBindings.usedQuickSlot(key);
        if(slot != -1){
            Networking.INSTANCE.sendToServer(new PacketQuickCast(slot));
        }
    }

    public static void checkCurioHotkey(int keyMapping){
        for(KeyMapping mapping : CURIO_MAPPINGS){
            if(mapping.getKey().getValue() == keyMapping){
                LazyOptional<IItemHandlerModifiable> stacks = CuriosUtil.getAllWornItems(MINECRAFT.player);
                if(!stacks.isPresent())
                    return;
                IItemHandlerModifiable handler = stacks.orElse(null);
                for(int i = 0; i < handler.getSlots(); i++){
                    ItemStack stack = handler.getStackInSlot(i);
                    if(stack.getItem() instanceof IRadialProvider radialProvider){
                        if(MINECRAFT.screen instanceof GuiRadialMenu){
                            MINECRAFT.player.closeContainer();
                        }else {
                            radialProvider.onRadialKeyPressed(stack, MINECRAFT.player);
                        }
                    }
                }
                return;
            }
        }
    }

    @SubscribeEvent
    public static void mouseEvent(final InputEvent.MouseButton.Post event) {

        if (MINECRAFT.player == null || event.getAction() != 1)
            return;
        if(MINECRAFT.screen == null || MINECRAFT.screen instanceof GuiRadialMenu)
            checkKeysPressed(event.getButton());
    }

    @SubscribeEvent
    public static void keyEvent(final InputEvent.Key event) {
        if (MINECRAFT.player == null || event.getAction() != 1)
            return;
        if(MINECRAFT.screen == null || MINECRAFT.screen instanceof GuiRadialMenu)
            checkKeysPressed(event.getKey());
    }

    public static void sendHotkeyPacket(PacketHotkeyPressed.Key key) {
        Networking.INSTANCE.sendToServer(new PacketHotkeyPressed(key));
    }
}
