package com.hollingsworth.craftedmagic.network;

import com.hollingsworth.craftedmagic.ExampleMod;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Networking {
    public static SimpleChannel INSTANCE;

    private static int ID = 0;
    public static int nextID(){return ID++;}
    public static void registerMessages(){
        System.out.println("Registering packets!!");
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(ExampleMod.MODID, "network"), () -> "1.0", s->true, s->true);

        INSTANCE.registerMessage(nextID(),
                PacketOpenGUI.class,
                PacketOpenGUI::toBytes,
                PacketOpenGUI::new,
                PacketOpenGUI::handle);
        INSTANCE.registerMessage(nextID(),
                PacketUpdateSpellbook.class,
                PacketUpdateSpellbook::toBytes,
                PacketUpdateSpellbook::new,
                PacketUpdateSpellbook::handle);

        INSTANCE.registerMessage(nextID(),
                PacketUpdateBookGUI.class,
                PacketUpdateBookGUI::toBytes,
                PacketUpdateBookGUI::new,
                PacketUpdateBookGUI::handle);
       // register(PacketOpenGUI.class, NetworkDirection.PLAY_TO_CLIENT);
      //  register(PacketUpdateSpellbook.class, NetworkDirection.PLAY_TO_SERVER);
    }

}
