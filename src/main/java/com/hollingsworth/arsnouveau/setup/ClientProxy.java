package com.hollingsworth.arsnouveau.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ClientProxy implements  IProxy {

    @Override
    public void init() {

    }

    @Override
    public Level getClientWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public Player getPlayer(){return Minecraft.getInstance().player;}

    @Override
    public Minecraft getMinecraft(){return Minecraft.getInstance();}

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