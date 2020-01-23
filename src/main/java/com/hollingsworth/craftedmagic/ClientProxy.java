package com.hollingsworth.craftedmagic;

import com.hollingsworth.craftedmagic.api.CraftedMagicAPI;
import com.hollingsworth.craftedmagic.client.gui.GuiSpellCreation;
import com.hollingsworth.craftedmagic.client.renderer.entity.ModRenderers;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.world.World;

public class ClientProxy implements  IProxy {

    @Override
    public void init() {

    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    //
//    @Override
//    public void modifySpellBook(ItemStack stack, NBTTagCompound tag){
//        throw new WrongSideException("Modified spell book on client side");
//    }
//
//    @Override
//    public void preInit(FMLPreInitializationEvent e) {
//        super.preInit(e);
//        ModEntities.initModels();
//        registerRenders();
//    }
//
//    @SubscribeEvent
//    public static void registerModels(ModelRegistryEvent event) {
//        System.out.println("Registering Models");
//        ModItems.initModels();
//
//    }
//
//
//    @Override
//    public void registerRenders() {
//        System.out.println("Registering renderers");
//        ModRenderers.register();
//    }
//

}