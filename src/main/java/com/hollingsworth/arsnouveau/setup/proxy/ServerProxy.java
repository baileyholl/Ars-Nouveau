package com.hollingsworth.arsnouveau.setup.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ServerProxy implements IProxy {

    @Override
    public void init() {
    }

    @Override
    public Level getClientWorld() {
        throw new IllegalStateException("Accessing client world on server proxy");
    }

    @Override
    public Minecraft getMinecraft() {
        throw new IllegalStateException("Accessing client Minecraft on server proxy");
    }

    @Override
    public Player getPlayer() {
        throw new IllegalStateException("Accessing client player on server proxy");
    }

    @Override
    public boolean isClientSide() {
        return false;
    }
}