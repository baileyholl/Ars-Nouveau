package com.hollingsworth.craftedmagic;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ServerProxy implements IProxy {

    @Override
    public void init() {

    }

    @Override
    public World getClientWorld() {
        throw new IllegalStateException("Accessing client world on server proxy");
    }

    @Override
    public Minecraft getMinecraft() {
        throw new IllegalStateException("Accessing client Minecraft on server proxy");
    }

    @Override
    public PlayerEntity getPlayer() {
        throw new IllegalStateException("Accessing client player on server proxy");
    }

}