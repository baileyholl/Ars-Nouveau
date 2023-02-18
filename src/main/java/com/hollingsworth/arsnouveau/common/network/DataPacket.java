package com.hollingsworth.arsnouveau.common.network;

import com.hollingsworth.arsnouveau.common.tss.platform.util.IDataReceiver;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DataPacket {
	public CompoundTag tag;

	public DataPacket(CompoundTag tag) {
		this.tag = tag;
	}

	public DataPacket(FriendlyByteBuf pb) {
		tag = pb.readAnySizeNbt();
	}

	public void toBytes(FriendlyByteBuf pb) {
		pb.writeNbt(tag);
	}

	public static class Handler {

		@SuppressWarnings("Convert2Lambda")
		public static boolean onMessage(DataPacket message, Supplier<NetworkEvent.Context> ctx) {
			if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
				ctx.get().enqueueWork(() -> {
					ServerPlayer sender = ctx.get().getSender();
					if (sender.containerMenu instanceof IDataReceiver) {
						((IDataReceiver) sender.containerMenu).receive(message.tag);
					}
				});
			} else if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
				ctx.get().enqueueWork(() -> {
					if (Minecraft.getInstance().screen instanceof IDataReceiver) {
						((IDataReceiver) Minecraft.getInstance().screen).receive(message.tag);
					}
				});
			}
			ctx.get().setPacketHandled(true);
			return true;
		}
	}
}
