package com.hollingsworth.arsnouveau.client.container;

import com.hollingsworth.arsnouveau.common.block.tile.StorageLecternTile;
import com.hollingsworth.arsnouveau.setup.registry.MenuRegistry;
import com.hollingsworth.arsnouveau.common.network.ClientToServerStoragePacket;
import com.hollingsworth.arsnouveau.common.network.Networking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StorageTerminalMenu extends RecipeBookMenu<CraftingContainer> {
	protected StorageLecternTile te;
	protected int playerSlotsStart;
	protected List<SlotStorage> storageSlotList = new ArrayList<>();
	public List<StoredItemStack> itemList = new ArrayList<>();
	public List<StoredItemStack> itemListClient = new ArrayList<>();
	public List<StoredItemStack> itemListClientSorted = new ArrayList<>();
	public TerminalSyncManager sync = new TerminalSyncManager();
	private int lines;
	protected Inventory pinv;
	public Runnable onPacket;
	public SortSettings terminalData = null;
	public String search;
	public boolean noSort;
	public List<String> tabNames = new ArrayList<>();
	public String selectedTab = null;

	public StorageTerminalMenu(int id, Inventory inv, StorageLecternTile te) {
		this(MenuRegistry.STORAGE.get(), id, inv, te);
		this.addPlayerSlots(inv, 8, 120);
	}

	public StorageTerminalMenu(MenuType<?> type, int id, Inventory inv, StorageLecternTile te) {
		super(type, id);
		this.te = te;
		this.pinv = inv;
		addStorageSlots();
	}

	public StorageTerminalMenu(MenuType<?> type, int id, Inventory inv) {
		this(type, id, inv, null);
	}

	protected void addStorageSlots() {
		addStorageSlots(8, 18);
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

	public void addStorageSlots(int x, int y) {
		storageSlotList.clear();
		lines = this.terminalData == null || !terminalData.expanded ? 3 : 7;
		for (int i = 0;i < lines;++i) {
			for (int j = 0;j < 9;++j) {
				this.addSlotToContainer(new SlotStorage(this.te, i * 9 + j, x + j * 18, y + i * 18));
			}
		}
		scrollTo(0.0F);
	}

	protected final void addSlotToContainer(SlotStorage slotStorage) {
		storageSlotList.add(slotStorage);
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return te != null && te.canInteractWith(playerIn);
	}

	public final void scrollTo(float p_148329_1_) {
		int i = (this.itemListClientSorted.size() + 9 - 1) / 9 - lines;
		int j = (int) (p_148329_1_ * i + 0.5D);

		if (j < 0) {
			j = 0;
		}

		for (int k = 0;k < lines;++k) {
			for (int l = 0;l < 9;++l) {
				int i1 = l + (k + j) * 9;

				if (i1 >= 0 && i1 < this.itemListClientSorted.size()) {
					setSlotContents(l + k * 9, this.itemListClientSorted.get(i1));
				} else {
					setSlotContents(l + k * 9, null);
				}
			}
		}
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

	@Override
	public void broadcastChanges() {
		if(te == null){
			return;
		}
		Map<StoredItemStack, Long> itemsCount = te.getStacks(selectedTab);
		sync.update(itemsCount, (ServerPlayer) pinv.player, tag -> {
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
			tag.put("sortSettings", te.sortSettings.toTag());
		});
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
	public boolean recipeMatches(Recipe<? super CraftingContainer> recipeIn) {
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

	public void sendMessage(CompoundTag compound) {
		Networking.sendToServer(new ClientToServerStoragePacket(compound));
	}

	public final void receiveClientNBTPacket(CompoundTag message) {
		if(sync.receiveUpdate(message)) {
			itemList = sync.getAsList();
			if(noSort) {
				itemListClient.forEach(s -> s.setCount(sync.getAmount(s)));
			} else {
				itemListClient = new ArrayList<>(itemList);
			}
			pinv.setChanged();
		}
		if(message.contains("search"))
			search = message.getString("search");
		if(message.contains("sortSettings")) {
			boolean isExpanded = terminalData != null && terminalData.expanded;
			terminalData = SortSettings.fromTag(message.getCompound("sortSettings"));
			if(isExpanded != terminalData.expanded) {
				addStorageSlots();
			}
		}
		if(message.contains("tabs")){
			ListTag tabs = message.getList("tabs", 10);
			tabNames = new ArrayList<>();
			for(int i = 0;i < tabs.size();i++){
				tabNames.add(tabs.getCompound(i).getString("name"));
			}
			Collections.sort(tabNames);
		}
		if(onPacket != null)onPacket.run();
	}

	public void receive(CompoundTag message) {
		if(pinv.player.isSpectator())return;
		if(message.contains("search")) {
			te.setLastSearch(message.getString("search"));
		}
		sync.receiveInteract(message, this);
		if(message.contains("termData")) {
			CompoundTag d = message.getCompound("termData");
			te.setSorting(SortSettings.fromTag(d.getCompound("sortSettings")));
			selectedTab = null;
			if(d.contains("selectedTab")){
				selectedTab = d.getString("selectedTab");
			}
		}
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
