package com.hollingsworth.arsnouveau.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface IProxy {

    void init();

    World getClientWorld();

    Minecraft getMinecraft();

    PlayerEntity getPlayer();
}
