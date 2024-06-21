package com.hollingsworth.arsnouveau.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;
import java.util.function.Supplier;

public abstract class AbstractPacket {

    public AbstractPacket(FriendlyByteBuf buf) {}

    public AbstractPacket(){}

    public abstract void toBytes(FriendlyByteBuf buf);

    public abstract void handle(Supplier<NetworkEvent.Context> ctx);
}
