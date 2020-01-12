package com.hollingsworth.craftedmagic;
//
//import com.hollingsworth.craftedmagic.api.CraftedMagicAPI;
//import com.hollingsworth.craftedmagic.items.Spell;
//import net.minecraft.block.Block;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fml.common.event.FMLInitializationEvent;
//import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
//import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CommonProxy {
//
//    public void preInit(FMLPreInitializationEvent e) {
//        CraftedMagicAPI singlton = CraftedMagicAPI.getInstance();
//        System.out.println(singlton.spell_map.keySet());
//    }
//
//    public CraftedMagicAPI getAPI(){
//        return CraftedMagicAPI.getInstance();
//    }
//
//    public void init(FMLInitializationEvent e) {
//    }
//
//    public void postInit(FMLPostInitializationEvent e) {
//
//    }
//
//    public void registerRenders(){
//
//    }
//
//    public void modifySpellBook(ItemStack stack, NBTTagCompound tag){
//
//    }
//
//    public void openSpellGUI(ItemStack stack)  {
//        throw new WrongSideException("Opened on common proxy.");
//    }
//
//    @SubscribeEvent
//    public static void registerBlocks(RegistryEvent.Register<Block> event) {
//    }
//
//    @SubscribeEvent
//    public static void registerItems(RegistryEvent.Register<Item> event) {
//        event.getRegistry().register(new Spell());
//    }
//
//
//    /**
//     * Thrown when a proxy method is called from the wrong side.
//     */
//    class WrongSideException extends RuntimeException {
//        public WrongSideException(final String message) {
//            super(message);
//        }
//
//        public WrongSideException(final String message, final Throwable cause) {
//            super(message, cause);
//        }
//    }
}
