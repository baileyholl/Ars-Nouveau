package com.hollingsworth.arsnouveau.client.keybindings;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ISpellHotkeyListener;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.client.gui.RadialMenu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketHotkeyPressed;
import com.hollingsworth.arsnouveau.common.network.PacketQuickCast;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateCaster;
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

    public static void checkKeysPressed(int key){
        if(key == ModKeyBindings.OPEN_RADIAL_HUD.getKey().getValue()) {
            if (MINECRAFT.screen instanceof GuiRadialMenu) {
                MINECRAFT.player.closeContainer();
                return;
            }
        }

        Player player = MINECRAFT.player;
        InteractionHand hand = StackUtil.getHeldCasterTool(player);
        if(hand == null)
            return;
        ItemStack stack = player.getItemInHand(hand);
        if(stack.isEmpty() || !(stack.getItem() instanceof ISpellHotkeyListener hotkeyListener))
            return;

        if(key == ModKeyBindings.NEXT_SLOT.getKey().getValue()){
            sendHotkeyPacket(PacketHotkeyPressed.Key.NEXT);
            return;
        }

        if(key == ModKeyBindings.PREVIOUS_SLOT.getKey().getValue()){
            sendHotkeyPacket(PacketHotkeyPressed.Key.PREVIOUS);
            return;
        }

        if(key == ModKeyBindings.OPEN_RADIAL_HUD.getKey().getValue()){
            if(MINECRAFT.screen == null){
                hotkeyListener.onRadialKeyPressed(stack, player);
                return;
            }
        }

        if(key == ModKeyBindings.OPEN_BOOK.getKey().getValue()){
            if(MINECRAFT.screen instanceof GuiSpellBook && !((GuiSpellBook) MINECRAFT.screen).spell_name.isFocused()) {
                MINECRAFT.player.closeContainer();
                return;
            }

            if(MINECRAFT.screen == null){
                hotkeyListener.onOpenBookMenuKeyPressed(stack, player);
            }
        }
        int slot = ModKeyBindings.usedQuickSlot(key);
        if(slot != -1){
            Networking.INSTANCE.sendToServer(new PacketQuickCast(slot));
        }
    }
    @SubscribeEvent
    public static void mouseEvent(final InputEvent.MouseInputEvent event) {

        if(MINECRAFT.player == null || MINECRAFT.screen != null || event.getAction() != 1)
            return;
        checkKeysPressed(event.getButton());
    }
    @SubscribeEvent
    public static void keyEvent(final InputEvent.KeyInputEvent event) {
        if(MINECRAFT.player == null || MINECRAFT.screen != null || event.getAction() != 1)
            return;
        checkKeysPressed(event.getKey());

    }

    public static void sendUpdatePacket(ItemStack stack, int newMode){
        ISpellCaster caster = CasterUtil.getCaster(stack);
        String recipe = caster.getSpell(newMode).serialize();
        String name = caster.getSpellName(newMode);
        Networking.INSTANCE.sendToServer(new PacketUpdateCaster(recipe, newMode, name));
    }

    public static void sendHotkeyPacket(PacketHotkeyPressed.Key key){
        Networking.INSTANCE.sendToServer(new PacketHotkeyPressed(key));
    }


}
