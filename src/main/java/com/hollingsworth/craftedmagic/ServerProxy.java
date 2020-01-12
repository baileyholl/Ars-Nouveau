package com.hollingsworth.craftedmagic;
import net.minecraft.world.World;

public class ServerProxy implements IProxy {

    @Override
    public void init() {

    }

    @Override
    public World getClientWorld() {
        throw new IllegalStateException("Accessing client world on server proxy");
    }

}