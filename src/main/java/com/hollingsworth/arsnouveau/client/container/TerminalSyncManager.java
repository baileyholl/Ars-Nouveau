package com.hollingsworth.arsnouveau.client.container;


import com.hollingsworth.arsnouveau.common.network.ClientToServerStoragePacket;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.ServerToClientStoragePacket;
import com.hollingsworth.arsnouveau.common.network.UpdateStorageItemsPacket;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.*;
import java.util.function.Consumer;

public class TerminalSyncManager {
	private Object2LongMap<StoredItemStack> items = new Object2LongOpenHashMap<>();
	private Map<StoredItemStack, StoredItemStack> itemList = new HashMap<>();
	private int lastId = 1;

//	private void writeStack(FriendlyByteBuf buf, StoredItemStack stack) {
//		ItemStack st = stack.getStack();
//		Item item = st.getItem();
//		CompoundTag compoundtag = getSyncTag(st);
//		byte flags = (byte) ((stack.getQuantity() == 0 ? 1 : 0) | (compoundtag != null ? 2 : 0));
//		boolean wr = true;
//		int id = idMap.getInt(stack);
//		if(id != 0) {
//			flags |= 4;
//			wr = false;
//		}
//		buf.writeByte(flags);
//		buf.writeVarInt(idMap.computeIfAbsent(stack, s -> {
//			int i = lastId++;
//			idMap2.put(i, (StoredItemStack) s);
//			return i;
//		}));
//		if(wr)writeItemId(buf, item);
//		if(stack.getQuantity() != 0)buf.writeVarLong(stack.getQuantity());
//		if(wr && compoundtag != null)buf.writeNbt(compoundtag);
//	}
//
//	private StoredItemStack read(FriendlyByteBuf buf) {
//		byte flags = buf.readByte();
//		int id = buf.readVarInt();
//		boolean rd = (flags & 4) == 0;
//		StoredItemStack stack;
//		if(rd) {
//			stack = new StoredItemStack(ItemStack.STREAM_CODEC.decode(buf));
//		} else {
//			stack = new StoredItemStack(idMap2.get(id).getStack());
//		}
//		long count = (flags & 1) != 0 ? 0 : buf.readVarLong();
//		stack.setCount(count);
//		if(rd && (flags & 2) != 0) {
//			//todo: check storage terminal for this
////		stack.getStack().
////			stack.getStack().setTag(buf.readNbt());
//		}
//		idMap.put(stack, id);
//		idMap2.put(id, stack);
//		return stack;
//	}

	public void update(Map<StoredItemStack, Long> items, ServerPlayer player, Consumer<CompoundTag> extraSync) {
		List<StoredItemStack> toWrite = new ArrayList<>();
		Set<StoredItemStack> found = new HashSet<>();
		items.forEach((s, c) -> {
			long pc = this.items.getLong(s);
			if(pc != 0L)found.add(s);
			if(pc != c) {
				toWrite.add(new StoredItemStack(s.getStack(), c));
			}
		});
		this.items.forEach((s, c) -> {
			if(!found.contains(s))
				toWrite.add(new StoredItemStack(s.getStack(), 0L));
		});
		this.items.clear();
		this.items.putAll(items);
		Networking.sendToPlayerClient(new UpdateStorageItemsPacket(toWrite), player);
		if(extraSync != null){
			CompoundTag t = new CompoundTag();
			extraSync.accept(t);
			Networking.sendToPlayerClient(new ServerToClientStoragePacket(t), player);
		}
	}

	public boolean updateItemList(List<StoredItemStack> items) {
		items.forEach(s -> {
			if(s.getQuantity() == 0) {
				this.itemList.remove(s);
			} else {
				this.itemList.put(s, s);
			}
		});
		return true;
	}

	public void sendClientInteract(StoredItemStack intStack, StorageTerminalMenu.SlotAction action, boolean pullOne) {
		CompoundTag interactTag = new CompoundTag();
		interactTag.putBoolean("pullOne", pullOne);
		interactTag.putInt("action", action.ordinal());
		if(intStack != null){
			interactTag.put("stack", ANCodecs.encode(StoredItemStack.CODEC, intStack));
		}
		CompoundTag dataTag = new CompoundTag();
		dataTag.put("interaction", interactTag);
		Networking.sendToServer(new ClientToServerStoragePacket(dataTag));
	}

	public void receiveInteract(CompoundTag tag, StorageTerminalMenu handler) {
		if(!tag.contains("interaction"))
			return;

		CompoundTag interactTag = tag.getCompound("interaction");
		boolean pullOne = interactTag.getBoolean("pullOne");
		StoredItemStack stack = null;
		if(interactTag.contains("stack")){
			stack = ANCodecs.decode(StoredItemStack.CODEC, interactTag.get("stack"));
		}
		StorageTerminalMenu.SlotAction action = StorageTerminalMenu.SlotAction.values()[interactTag.getInt("action")];
		handler.onInteract(stack, action, pullOne);
	}

	public List<StoredItemStack> getAsList() {
		return new ArrayList<>(this.itemList.values());
	}

	public long getAmount(StoredItemStack stack) {
		StoredItemStack s = itemList.get(stack);
		return s != null ? s.getQuantity() : 0L;
	}

	public static ResourceLocation getItemId(Item item) {
		return BuiltInRegistries.ITEM.getKey(item);
	}
}
