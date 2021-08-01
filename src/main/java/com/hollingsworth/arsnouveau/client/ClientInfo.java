package com.hollingsworth.arsnouveau.client;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ClientInfo {
    private ClientInfo(){};

    public static CompoundNBT persistentData = new CompoundNBT();
    public static int ticksInGame = 0;
    public static List<BlockPos> scryingPositions = new ArrayList<>();
}
