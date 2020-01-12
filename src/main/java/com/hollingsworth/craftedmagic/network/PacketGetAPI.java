package com.hollingsworth.craftedmagic.network;

import net.minecraftforge.fml.network.NetworkEvent;

public class PacketGetAPI  implements IMessage {
    @Override
    public boolean receive(NetworkEvent.Context context) {
        return false;
    }
}
