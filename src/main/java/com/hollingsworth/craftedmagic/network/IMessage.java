package com.hollingsworth.craftedmagic.network;

import net.minecraftforge.fml.network.NetworkEvent;

import java.io.Serializable;

public interface IMessage extends Serializable {

    public boolean receive(NetworkEvent.Context context);

}
