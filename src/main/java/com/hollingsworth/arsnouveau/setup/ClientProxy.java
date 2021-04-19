package com.hollingsworth.arsnouveau.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientProxy implements  IProxy {

    @Override
    public void init() {

    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public PlayerEntity getPlayer(){return Minecraft.getInstance().player;}

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