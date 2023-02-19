package com.hollingsworth.arsnouveau.common.tss.platform.util;


import com.hollingsworth.arsnouveau.common.network.DataPacket;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.tss.platform.gui.StorageTerminalMenu;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Consumer;

public class TerminalSyncManager {
	private static final int MAX_PACKET_SIZE = 32000;
	private Object2IntMap<StoredItemStack> idMap = new Object2IntOpenHashMap<>();
	private Int2ObjectMap<StoredItemStack> idMap2 = new Int2ObjectArrayMap<>();
	private Object2LongMap<StoredItemStack> items = new Object2LongOpenHashMap<>();
	private Map<StoredItemStack, StoredItemStack> itemList = new HashMap<>();
	private int lastId = 1;
	private FriendlyByteBuf workBuf = new FriendlyByteBuf(Unpooled.buffer());

	private void writeStack(FriendlyByteBuf buf, StoredItemStack stack) {
		ItemStack st = stack.getStack();
		Item item = st.getItem();
		CompoundTag compoundtag = getSyncTag(st);
		byte flags = (byte) ((stack.getQuantity() == 0 ? 1 : 0) | (compoundtag != null ? 2 : 0));
		boolean wr = true;
		int id = idMap.getInt(stack);
		if(id != 0) {
			flags |= 4;
			wr = false;
		}
		buf.writeByte(flags);
		buf.writeVarInt(idMap.computeIfAbsent(stack, s -> {
			int i = lastId++;
			idMap2.put(i, (StoredItemStack) s);
			return i;
		}));
		if(wr)writeItemId(buf, item);
		if(stack.getQuantity() != 0)buf.writeVarLong(stack.getQuantity());
		if(wr && compoundtag != null)buf.writeNbt(compoundtag);
	}

	private StoredItemStack read(FriendlyByteBuf buf) {
		byte flags = buf.readByte();
		int id = buf.readVarInt();
		boolean rd = (flags & 4) == 0;
		StoredItemStack stack;
		if(rd) {
			stack = new StoredItemStack(new ItemStack(readItemId(buf)));
		} else {
			stack = new StoredItemStack(idMap2.get(id).getStack());
		}
		long count = (flags & 1) != 0 ? 0 : buf.readVarLong();
		stack.setCount(count);
		if(rd && (flags & 2) != 0) {
			stack.getStack().setTag(buf.readNbt());
		}
		idMap.put(stack, id);
		idMap2.put(id, stack);
		return stack;
	}

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
		if(!toWrite.isEmpty()) {
			workBuf.writerIndex(0);
			int j = 0;
			for (int i = 0; i < toWrite.size(); i++, j++) {
				StoredItemStack stack = toWrite.get(i);
				int li = workBuf.writerIndex();
				writeStack(workBuf, stack);
				int s = workBuf.writerIndex();
				if((s > MAX_PACKET_SIZE || j > 32000) && j > 1) {
					CompoundTag t = writeBuf("d", workBuf, li);
					t.putShort("l", (short) j);
					Networking.sendToPlayer(new DataPacket(t), player);
					j = 0;
					workBuf.writerIndex(0);
					workBuf.writeBytes(workBuf, li, s - li);
				}
			}
			if(j > 0 || extraSync != null) {
				CompoundTag t;
				if(j > 0) {
					t = writeBuf("d", workBuf, workBuf.writerIndex());
					t.putShort("l", (short) j);
				} else t = new CompoundTag();
				if(extraSync != null)extraSync.accept(t);
				Networking.sendToPlayer(new DataPacket(t), player);
			}
		} else if(extraSync != null) {
			CompoundTag t = new CompoundTag();
			extraSync.accept(t);
			Networking.sendToPlayer(new DataPacket(t), player);
		}
	}

	public boolean receiveUpdate(CompoundTag tag) {
		if(tag.contains("d")) {
			FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(tag.getByteArray("d")));
			List<StoredItemStack> in = new ArrayList<>();
			short len = tag.getShort("l");
			for (int i = 0; i < len; i++) {
				in.add(read(buf));
			}
			in.forEach(s -> {
				if(s.getQuantity() == 0) {
					this.itemList.remove(s);
				} else {
					this.itemList.put(s, s);
				}
			});
			return true;
		}
		return false;
	}

	public void sendInteract(StoredItemStack intStack, StorageTerminalMenu.SlotAction action, boolean mod) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		int flags = mod ? 1 : 0;
		if(intStack == null) {
			buf.writeByte(flags | 2);
		} else {
			buf.writeByte(flags);
			buf.writeVarInt(idMap.getInt(intStack));
			buf.writeVarLong(intStack.getQuantity());
		}
		buf.writeEnum(action);
		Networking.sendToServer(new DataPacket(writeBuf("a", buf, buf.writerIndex())));
	}

	private CompoundTag writeBuf(String id, FriendlyByteBuf buf, int len) {
		byte[] data = new byte[len];
		buf.getBytes(0, data);
		CompoundTag tag = new CompoundTag();
		tag.putByteArray(id, data);
		return tag;
	}

	public void receiveInteract(CompoundTag tag, InteractHandler handler) {
		if(tag.contains("a")) {
			FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(tag.getByteArray("a")));
			byte flags = buf.readByte();
			StoredItemStack stack;
			if((flags & 2) != 0) {
				stack = null;
			} else {
				stack = new StoredItemStack(idMap2.get(buf.readVarInt()).getStack());
				long count = buf.readVarLong();
				stack.setCount(count);
			}
			handler.onInteract(stack, buf.readEnum(StorageTerminalMenu.SlotAction.class), (flags & 1) != 0);
		}
	}

	public List<StoredItemStack> getAsList() {
		return new ArrayList<>(this.itemList.values());
	}

	public long getAmount(StoredItemStack stack) {
		StoredItemStack s = itemList.get(stack);
		return s != null ? s.getQuantity() : 0L;
	}

	public static interface InteractHandler {
		void onInteract(StoredItemStack intStack, StorageTerminalMenu.SlotAction action, boolean mod);
	}

	public static ResourceLocation getItemId(Item item) {
		return ForgeRegistries.ITEMS.getKey(item);
	}

	public static void writeItemId(FriendlyByteBuf buf, Item item) {
		buf.writeId(Registry.ITEM, item);
	}

	public static Item readItemId(FriendlyByteBuf buf) {
		return buf.readById(Registry.ITEM);
	}

	public static CompoundTag getSyncTag(ItemStack stack) {
		Item item = stack.getItem();
		CompoundTag compoundtag = null;
		if (item.isDamageable(stack) || item.shouldOverrideMultiplayerNbt()) {
			compoundtag = stack.getShareTag();
		}
		return compoundtag;
	}
}
