package com.hollingsworth.craftedmagic;

import com.hollingsworth.craftedmagic.client.gui.GuiSpellCreation;
import com.hollingsworth.craftedmagic.client.renderer.entity.ModRenderers;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void modifySpellBook(ItemStack stack, NBTTagCompound tag){
        throw new WrongSideException("Modified spell book on client side");
    }

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        ModEntities.initModels();
        registerRenders();
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        System.out.println("Registering Models");
        ModItems.initModels();

    }


    @Override
    public void registerRenders() {
        System.out.println("Registering renderers");
        ModRenderers.register();
    }

    @Override
    public void openSpellGUI(ItemStack spellBook)  {
        Minecraft.getMinecraft().addScheduledTask(()-> Minecraft.getMinecraft().displayGuiScreen(new GuiSpellCreation()));
    }
}