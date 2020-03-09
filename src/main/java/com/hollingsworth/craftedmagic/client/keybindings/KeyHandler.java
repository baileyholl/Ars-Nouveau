package com.hollingsworth.craftedmagic.client.keybindings;


import com.hollingsworth.craftedmagic.ArsNouveau;
import com.hollingsworth.craftedmagic.api.ArsNouveauAPI;
import com.hollingsworth.craftedmagic.client.gui.GuiRadialMenu;
import com.hollingsworth.craftedmagic.client.gui.GuiSpellBook;
import com.hollingsworth.craftedmagic.items.SpellBook;
import com.hollingsworth.craftedmagic.network.Networking;
import com.hollingsworth.craftedmagic.network.PacketUpdateSpellbook;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsNouveau.MODID)
public class KeyHandler {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    @SubscribeEvent
    public static void keyEvent(final InputEvent.KeyInputEvent event) {
        ItemStack heldItem = MINECRAFT.player.getHeldItemMainhand();


        if(event.getKey() == ModKeyBindings.NEXT_SLOT.getKey().getKeyCode() && event.getAction() == 1 && heldItem.getItem() instanceof SpellBook){
            if(!heldItem.hasTag())
                return;
            CompoundNBT tag = heldItem.getTag();
            int newMode = SpellBook.getMode(tag) + 1;
            if(newMode > 10)
                newMode = 0;

           sendUpdatePacket(tag, newMode);
           return;
        }

        if(event.getKey() == ModKeyBindings.PREVIOUS__SLOT.getKey().getKeyCode() && event.getAction() == 1 && heldItem.getItem() instanceof SpellBook){
            if(!heldItem.hasTag())
                return;
            CompoundNBT tag = heldItem.getTag();
            int newMode = SpellBook.getMode(tag) - 1;
            if(newMode < 0)
                newMode = 10;

            sendUpdatePacket(tag, newMode);
            return;
        }

        if(event.getKey() == ModKeyBindings.OPEN_SPELL_SELECTION.getKey().getKeyCode() && event.getAction() == 1){
            if(MINECRAFT.currentScreen instanceof GuiRadialMenu) {
                MINECRAFT.player.closeScreen();
                return;
            }
            ItemStack stack = MINECRAFT.player.getHeldItemMainhand();
            if(stack.getItem() instanceof SpellBook && stack.hasTag() && MINECRAFT.currentScreen == null){
                MINECRAFT.displayGuiScreen(new GuiRadialMenu(ModKeyBindings.OPEN_SPELL_SELECTION, stack.getTag()));
            }
        }

        if(event.getKey() == ModKeyBindings.OPEN_BOOK.getKey().getKeyCode() && event.getAction() == 1){
            if(MINECRAFT.currentScreen instanceof GuiSpellBook && !((GuiSpellBook) MINECRAFT.currentScreen).spell_name.isFocused()) {
                MINECRAFT.player.closeScreen();
                return;
            }
            ItemStack stack = MINECRAFT.player.getHeldItemMainhand();
            if(stack.getItem() instanceof SpellBook && stack.hasTag() && MINECRAFT.currentScreen == null){
                GuiSpellBook.open(ArsNouveauAPI.getInstance(), stack.getTag(), ((SpellBook) stack.getItem()).getTier().ordinal());
            }
        }
    }

    public static void sendUpdatePacket(CompoundNBT tag, int newMode){
        String recipe = SpellBook.getRecipeString(tag, newMode);
        String name = SpellBook.getSpellName(tag, newMode);
        Networking.INSTANCE.sendToServer(new PacketUpdateSpellbook(recipe, newMode, name));
    }

    @SubscribeEvent
    public static void clientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

//        if(ModKeyBindings.OPEN_SPELL_SELECTION.isKeyDown()){
//            System.out.println("Ticking with radial down");
//            ItemStack stack = MINECRAFT.player.getHeldItemMainhand();
//            if(stack.getItem() instanceof SpellBook && stack.hasTag()){
//                if(MINECRAFT.currentScreen instanceof GuiRadialMenu){
//                    System.out.println("Closing radial");
//                    MINECRAFT.player.closeScreen();
//                }else
//                    MINECRAFT.displayGuiScreen(new GuiRadialMenu(ModKeyBindings.OPEN_SPELL_SELECTION, stack.getTag()));
//            }
//        }

//        if (ModKeyBindings.OPEN_BOOK.isKeyDown()) {
//            ItemStack stack = MINECRAFT.player.getHeldItemMainhand();
//            if(stack.getItem() instanceof SpellBook && stack.hasTag()){
//                CompoundNBT tag = stack.getTag();
//                if(MINECRAFT.currentScreen instanceof GuiSpellBook){
//                    MINECRAFT.player.closeScreen();
//                }else
//                    GuiSpellBook.open(CraftedMagicAPI.getInstance(), tag, ((SpellBook) stack.getItem()).getTier().ordinal());
//            }
//        }

    }
}
