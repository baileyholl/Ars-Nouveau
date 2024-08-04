package com.hollingsworth.arsnouveau.client.container;

import com.hollingsworth.arsnouveau.common.block.tile.StorageLecternTile;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.ServerToClientStoragePacket;
import com.hollingsworth.arsnouveau.common.network.SetTerminalSettingsPacket;
import com.hollingsworth.arsnouveau.common.network.UpdateStorageItemsPacket;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.hollingsworth.arsnouveau.setup.registry.MenuRegistry;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.*;

public class StorageTerminalMenu extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
	protected StorageLecternTile te;
	protected int playerSlotsStart;
	protected List<SlotStorage> storageSlotList = new ArrayList<>();
	public List<StoredItemStack> itemList = new ArrayList<>();
    protected Inventory pinv;
	public String search;
	public String selectedTab = null;
	public Map<StoredItemStack, StoredItemStack> itemMap = new HashMap<>();

	public StorageTerminalMenu(int id, Inventory inv, StorageLecternTile te) {
		this(MenuRegistry.STORAGE.get(), id, inv, te);
		this.addPlayerSlots(inv, 8, 120);
	}

	public StorageTerminalMenu(MenuType<?> type, int id, Inventory inv, StorageLecternTile te) {
		super(type, id);
		this.te = te;
		this.pinv = inv;
		addStorageSlots(false);
	}

	public StorageTerminalMenu(MenuType<?> type, int id, Inventory inv) {
		this(type, id, inv, null);
	}

	protected void addPlayerSlots(Inventory playerInventory, int x, int y) {
		this.playerSlotsStart = slots.size() - 1;
		for (int i = 0;i < 3;++i) {
			for (int j = 0;j < 9;++j) {
				addSlot(new Slot(playerInventory, j + i * 9 + 9, x + j * 18, y + i * 18));
			}
		}

		for (int i = 0;i < 9;++i) {
			addSlot(new Slot(playerInventory, i, x + i * 18, y + 58));
		}
	}

	public void addStorageSlots(boolean expanded) {
		storageSlotList.clear();
        int lines = !expanded ? 3 : 7;
		for (int i = 0; i < lines; ++i) {
			for (int j = 0;j < 9;++j) {
				storageSlotList.add(new SlotStorage(this.te, i * 9 + j, 13 + j * 18, 21 + i * 18));
			}
		}
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return te != null && te.canInteractWith(playerIn);
	}

	public final void setSlotContents(int id, StoredItemStack stack) {
		storageSlotList.get(id).stack = stack;
	}

	public final SlotStorage getSlotByID(int id) {
		return storageSlotList.get(id);
	}

	public enum SlotAction {
		PULL_OR_PUSH_STACK, PULL_ONE, SPACE_CLICK, SHIFT_PULL, GET_HALF, GET_QUARTER //CRAFT
	}
	private Object2LongMap<StoredItemStack> itemLongMap = new Object2LongOpenHashMap<>();
	@Override
	public void broadcastChanges() {
		if(te == null){
			return;
		}
		Map<StoredItemStack, Long> itemsCount = te.getStacks(selectedTab);
		List<StoredItemStack> toWrite = new ArrayList<>();
		Set<StoredItemStack> found = new HashSet<>();
		itemsCount.forEach((s, c) -> {
			long pc = this.itemLongMap.getLong(s);
			if(pc != 0L)found.add(s);
			if(pc != c) {
				toWrite.add(new StoredItemStack(s.getStack(), c));
			}
		});
		this.itemLongMap.forEach((s, c) -> {
			if(!found.contains(s))
				toWrite.add(new StoredItemStack(s.getStack(), 0L));
		});
		this.itemLongMap.clear();
		this.itemLongMap.putAll(itemsCount);
		Networking.sendToPlayerClient(new UpdateStorageItemsPacket(toWrite),  (ServerPlayer) pinv.player);
		CompoundTag tag = new CompoundTag();
		if(!te.getLastSearch().equals(search)) {
			search = te.getLastSearch();
			tag.putString("search", search);
		}
		ListTag tabs = new ListTag();
		for(String s : te.getTabNames()){
			CompoundTag nameTag = new CompoundTag();
			nameTag.putString("name", s);
			tabs.add(nameTag);
		}
		tag.put("tabs", tabs);
		Networking.sendToPlayerClient(new SetTerminalSettingsPacket(te.sortSettings, null), (ServerPlayer) pinv.player);
		Networking.sendToPlayerClient(new ServerToClientStoragePacket(tag), (ServerPlayer) pinv.player);
		super.broadcastChanges();
	}

	@Override
	public final ItemStack quickMoveStack(Player playerIn, int index) {
		if(slots.size() <= index)
			return ItemStack.EMPTY;

		if (index > playerSlotsStart && te != null) {
			if (slots.get(index) != null && slots.get(index).hasItem()) {
				Slot slot = slots.get(index);
				ItemStack slotStack = slot.getItem();
				StoredItemStack c = te.pushStack(new StoredItemStack(slotStack, slotStack.getCount()), selectedTab);
				ItemStack itemstack = c != null ? c.getActualStack() : ItemStack.EMPTY;
				slot.set(itemstack);
				if (!playerIn.level.isClientSide)
					broadcastChanges();
			}
		} else {
			return shiftClickItems(playerIn, index);
		}

		return ItemStack.EMPTY;
	}

	protected ItemStack shiftClickItems(Player playerIn, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public void fillCraftSlotsStackedContents(StackedContents itemHelperIn) {
	}

	@Override
	public void clearCraftingContent() {
	}

	@Override
	public boolean recipeMatches(RecipeHolder pRecipe) {
		return false;
	}

	@Override
	public int getResultSlotIndex() {
		return 0;
	}

	@Override
	public int getGridWidth() {
		return 0;
	}

	@Override
	public int getGridHeight() {
		return 0;
	}

	@Override
	public int getSize() {
		return 0;
	}

	public void updateItems(List<StoredItemStack> stacks){
		stacks.forEach(s -> {
			if(s.getQuantity() == 0) {
				this.itemMap.remove(s);
			} else {
				this.itemMap.put(s, s);
			}

		});
		itemList = new ArrayList<>(itemMap.values());
		pinv.setChanged();
	}

	public final void receiveClientNBTPacket(CompoundTag message) {
		if(message.contains("search"))
			search = message.getString("search");
	}


	public void receive(HolderLookup.Provider reg, CompoundTag message) {
		if(pinv.player.isSpectator())return;
		if(message.contains("search")) {
			te.setLastSearch(message.getString("search"));
		}
		this.receiveInteract(message);
	}

	public void receiveSettings(SortSettings settings, String selectedTab) {
		this.selectedTab = selectedTab;
		te.setSorting(settings);
	}

	public void receiveInteract(CompoundTag tag) {
		if(!tag.contains("interaction"))
			return;

		CompoundTag interactTag = tag.getCompound("interaction");
		boolean pullOne = interactTag.getBoolean("pullOne");
		StoredItemStack stack = null;
		if(interactTag.contains("stack")){
			stack = ANCodecs.decode(StoredItemStack.CODEC, interactTag.get("stack"));
		}
		StorageTerminalMenu.SlotAction action = StorageTerminalMenu.SlotAction.values()[interactTag.getInt("action")];
		onInteract(stack, action, pullOne);
	}

	@Override
	public RecipeBookType getRecipeBookType() {
		return RecipeBookType.CRAFTING;
	}

	@Override
	public boolean shouldMoveToInventory(int p_150635_) {
		return false;
	}

	public void onInteract(StoredItemStack clicked, SlotAction act, boolean pullOne) {
		ServerPlayer player = (ServerPlayer) pinv.player;
		player.resetLastActionTime();
		if(act == SlotAction.SPACE_CLICK) {
			for (int i = playerSlotsStart + 1;i < playerSlotsStart + 28;i++) {
				quickMoveStack(player, i);
			}
		} else {
			if (act == SlotAction.PULL_OR_PUSH_STACK) {
				ItemStack stack = getCarried();
				if (!stack.isEmpty()) {
					StoredItemStack rem = te.pushStack(new StoredItemStack(stack), selectedTab);
					ItemStack itemstack = rem == null ? ItemStack.EMPTY : rem.getActualStack();
					setCarried(itemstack);
				} else {
					if (clicked == null)return;
					StoredItemStack pulled = te.pullStack(clicked, clicked.getMaxStackSize(), selectedTab);
					if(pulled != null) {
						setCarried(pulled.getActualStack());
					}
				}
			} else if (act == SlotAction.PULL_ONE) {
				ItemStack stack = getCarried();
				if (clicked == null)return;
				if (pullOne) {
					StoredItemStack pulled = te.pullStack(clicked, 1, selectedTab);
					if(pulled != null) {
						ItemStack itemstack = pulled.getActualStack();
						this.moveItemStackTo(itemstack, playerSlotsStart + 1, this.slots.size(), true);
						if (itemstack.getCount() > 0)
							te.pushOrDrop(itemstack, selectedTab);
						player.getInventory().setChanged();
					}
				} else {
					if (!stack.isEmpty()) {
						if (ItemStack.isSameItemSameComponents(stack, clicked.getStack()) && stack.getCount() + 1 <= stack.getMaxStackSize()) {
							StoredItemStack pulled = te.pullStack(clicked, 1, selectedTab);
							if (pulled != null) {
								stack.grow(1);
							}
						}
					} else {
						StoredItemStack pulled = te.pullStack(clicked, 1, selectedTab);
						if (pulled != null) {
							setCarried(pulled.getActualStack());
						}
					}
				}
			} else if (act == SlotAction.GET_HALF) {
				ItemStack stack = getCarried();
				if (!stack.isEmpty()) {
					ItemStack stack1 = stack.split(Math.max(Math.min(stack.getCount(), stack.getMaxStackSize()) / 2, 1));
					ItemStack itemstack = te.pushStack(stack1, selectedTab);
					stack.grow(!itemstack.isEmpty() ? itemstack.getCount() : 0);
					setCarried(stack);
				} else {
					if (clicked == null) {
						return;
					}
					StoredItemStack pulled = te.pullStack(clicked, (int) Math.max(Math.min(clicked.getQuantity() / 2, clicked.getMaxStackSize() / 2), 1), selectedTab);
					if(pulled != null) {
						setCarried(pulled.getActualStack());
					}
				}
			} else if (act == SlotAction.GET_QUARTER) {
				ItemStack stack = getCarried();
				if (!stack.isEmpty()) {
					ItemStack stack1 = stack.split(Math.max(Math.min(stack.getCount(), stack.getMaxStackSize()) / 4, 1));
					ItemStack itemstack = te.pushStack(stack1, selectedTab);
					stack.grow(!itemstack.isEmpty() ? itemstack.getCount() : 0);
					setCarried(stack);
				} else {
					if (clicked == null)return;
					long maxCount = 64;
					for (StoredItemStack e : itemList) {
						if (e.equals(clicked)) maxCount = e.getQuantity();
					}
					StoredItemStack pulled = te.pullStack(clicked, (int) Math.max(Math.min(maxCount, clicked.getMaxStackSize()) / 4, 1), selectedTab);
					if(pulled != null) {
						setCarried(pulled.getActualStack());
					}
				}
			} else {
				if (clicked == null)return;
				StoredItemStack pulled = te.pullStack(clicked, clicked.getMaxStackSize(), selectedTab);
				if(pulled != null) {
					ItemStack itemstack = pulled.getActualStack();
					this.moveItemStackTo(itemstack, playerSlotsStart + 1, this.slots.size(), true);
					if (itemstack.getCount() > 0) {
						te.pushOrDrop(itemstack, selectedTab);
					}
					player.getInventory().setChanged();
				}
			}
		}
	}
}
