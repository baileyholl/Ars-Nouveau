package com.hollingsworth.arsnouveau.client;

import net.minecraft.nbt.CompoundNBT;

public class ClientInfo {
    private ClientInfo(){};

    public static CompoundNBT persistentData = new CompoundNBT();
    public static int ticksInGame = 0;
}
