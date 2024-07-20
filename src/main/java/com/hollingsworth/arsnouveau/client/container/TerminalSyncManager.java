package com.hollingsworth.arsnouveau.client.container;


import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

import java.util.HashMap;
import java.util.Map;

public class TerminalSyncManager {
	private Object2LongMap<StoredItemStack> items = new Object2LongOpenHashMap<>();
	private Map<StoredItemStack, StoredItemStack> itemList = new HashMap<>();
	private Object2IntMap<StoredItemStack> idMap = new Object2IntOpenHashMap<>();
	private Int2ObjectMap<StoredItemStack> idMap2 = new Int2ObjectArrayMap<>();
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
}
