package com.hollingsworth.arsnouveau.client.keybindings;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.client.gui.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateSpellbook;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class KeyHandler {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    public static void checkKeysPressed(int key){
        ItemStack stack = StackUtil.getHeldSpellbook(MINECRAFT.player);

        if(key == ModKeyBindings.NEXT_SLOT.getKey().getValue()  && stack.getItem() instanceof SpellBook){
            if(!stack.hasTag())
                return;
            CompoundNBT tag = stack.getTag();
            int newMode = SpellBook.getMode(tag) + 1;
            if(newMode > 10)
                newMode = 0;

            sendUpdatePacket(tag, newMode);
            return;
        }

        if(key == ModKeyBindings.PREVIOUS__SLOT.getKey().getValue()  && stack.getItem() instanceof SpellBook){
            if(!stack.hasTag())
                return;
            CompoundNBT tag = stack.getTag();
            int newMode = SpellBook.getMode(tag) - 1;
            if(newMode < 0)
                newMode = 10;

            sendUpdatePacket(tag, newMode);
            return;
        }

        if(key == ModKeyBindings.OPEN_SPELL_SELECTION.getKey().getValue()){
            if(MINECRAFT.screen instanceof GuiRadialMenu) {
                MINECRAFT.player.closeContainer();
                return;
            }
            if(stack.getItem() instanceof SpellBook && stack.hasTag() && MINECRAFT.screen == null){
                MINECRAFT.setScreen(new GuiRadialMenu(stack.getTag()));
            }
        }

        if(key == ModKeyBindings.OPEN_BOOK.getKey().getValue()){
            if(MINECRAFT.screen instanceof GuiSpellBook && !((GuiSpellBook) MINECRAFT.screen).spell_name.isFocused()) {
                MINECRAFT.player.closeContainer();
                return;
            }

            if(stack.getItem() instanceof SpellBook && stack.hasTag() && MINECRAFT.screen == null){
                GuiSpellBook.open(stack.getTag(), ((SpellBook) stack.getItem()).getTier().ordinal(), SpellBook.getUnlockedSpellString(stack.getTag()));
            }
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

    public static void sendUpdatePacket(CompoundNBT tag, int newMode){
        String recipe = SpellBook.getRecipeString(tag, newMode);
        String name = SpellBook.getSpellName(tag, newMode);
        Networking.INSTANCE.sendToServer(new PacketUpdateSpellbook(recipe, newMode, name));
    }


}
