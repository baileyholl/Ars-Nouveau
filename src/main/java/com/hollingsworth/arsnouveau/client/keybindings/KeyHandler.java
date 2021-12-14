package com.hollingsworth.arsnouveau.client.keybindings;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.client.gui.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateSpellbook;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class KeyHandler {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    public static void checkKeysPressed(int key){
        ItemStack stack = StackUtil.getHeldSpellbook(MINECRAFT.player);
        ISpellCaster caster = null;
        if(stack.getItem() instanceof SpellBook){
            caster =  CasterUtil.getCaster(stack);
        }
        if(key == ModKeyBindings.NEXT_SLOT.getKey().getValue()  && stack.getItem() instanceof SpellBook){
            if(!stack.hasTag())
                return;

            caster.setNextSlot();
            sendUpdatePacket(stack, caster.getCurrentSlot());
            return;
        }

        if(key == ModKeyBindings.PREVIOUS__SLOT.getKey().getValue()  && stack.getItem() instanceof SpellBook){
            if(!stack.hasTag())
                return;
            caster.setPreviousSlot();
            sendUpdatePacket(stack, caster.getCurrentSlot());
            return;
        }

        if(key == ModKeyBindings.OPEN_SPELL_SELECTION.getKey().getValue()){
            if(MINECRAFT.screen instanceof GuiRadialMenu) {
                MINECRAFT.player.closeContainer();
                return;
            }
            if(stack.getItem() instanceof SpellBook && stack.hasTag() && MINECRAFT.screen == null){
                MINECRAFT.setScreen(new GuiRadialMenu(stack));
            }
        }

        if(key == ModKeyBindings.OPEN_BOOK.getKey().getValue()){
            if(MINECRAFT.screen instanceof GuiSpellBook && !((GuiSpellBook) MINECRAFT.screen).spell_name.isFocused()) {
                MINECRAFT.player.closeContainer();
                return;
            }

            if(stack.getItem() instanceof SpellBook && stack.hasTag() && MINECRAFT.screen == null){
                GuiSpellBook.open(ArsNouveauAPI.getInstance(), stack, ((SpellBook) stack.getItem()).getTier().ordinal(), SpellBook.getUnlockedSpellString(stack.getTag()));
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

    public static void sendUpdatePacket(ItemStack stack, int newMode){
        ISpellCaster caster = CasterUtil.getCaster(stack);
        String recipe = caster.getSpell(newMode).serialize();
        String name = caster.getSpellName(newMode);
        Networking.INSTANCE.sendToServer(new PacketUpdateSpellbook(recipe, newMode, name));
    }


}
