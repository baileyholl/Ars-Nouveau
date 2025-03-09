package com.hollingsworth.arsnouveau.client.container;

import com.hollingsworth.arsnouveau.common.block.tile.StorageLecternTile;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.ServerToClientStoragePacket;
import com.hollingsworth.arsnouveau.common.network.SetTerminalSettingsPacket;
import com.hollingsworth.arsnouveau.common.network.UpdateStorageItemsPacket;
import com.hollingsworth.arsnouveau.setup.registry.MenuRegistry;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
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

import javax.annotation.Nullable;
import java.util.*;

public class StorageTerminalMenu extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
	protected StorageLecternTile te;
	protected int playerSlotsStart;
	protected List<SlotStorage> storageSlotList = new ArrayList<>();
	public List<StoredItemStack> itemList = new ArrayList<>();
    protected Inventory pinv;
	public String search;
	public Map<StoredItemStack, StoredItemStack> itemMap = new HashMap<>();
	boolean sentSettings = false;
	public Map<UUID, String> tabs = new HashMap<>();

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
        int lines = 7;
		boolean shouldAdd = storageSlotList.isEmpty();
		for (int i = 0; i < lines; ++i) {
			for (int j = 0; j < 9;++j) {
				int index = i * 9 + j;
				if(shouldAdd) {
					storageSlotList.add(new SlotStorage(this.te, index, 13 + j * 18, 21 + i * 18, expanded || i < 3));
				}else{
					storageSlotList.get(index).show = expanded || i < 3;
				}
			}
		}
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return te != null && te.canInteractWith(playerIn);
	}

	public final void setSlotContents(int id, StoredItemStack stack) {
		storageSlotList.get(id).setStack(stack);
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
		if(te == null) {
			return;
		}

		Map<StoredItemStack, Long> itemsCount = te.getStacks(tabs.get(pinv.player.getUUID()));
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
		if(!toWrite.isEmpty()) {
			Networking.sendToPlayerClient(new UpdateStorageItemsPacket(toWrite), (ServerPlayer) pinv.player);
		}

		if(!sentSettings) {
			Networking.sendToPlayerClient(new SetTerminalSettingsPacket(te.sortSettings, null), (ServerPlayer) pinv.player);
			Networking.sendToPlayerClient(new ServerToClientStoragePacket(te.getLastSearch(pinv.player), te.getTabNames()), (ServerPlayer) pinv.player);
			sentSettings = true;
		}

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
				StoredItemStack c = te.pushStack(new StoredItemStack(slotStack, slotStack.getCount()), tabs.get(playerIn.getUUID()));
				ItemStack itemstack = c != null ? c.getActualStack() : ItemStack.EMPTY;
				slot.set(itemstack);
				if (!playerIn.level.isClientSide)
					broadcastChanges();
			}
		} else {
			if(playerIn instanceof ServerPlayer serverPlayer) {
				return shiftClickItems(serverPlayer, index);
			}
		}

		return ItemStack.EMPTY;
	}

	protected ItemStack shiftClickItems(ServerPlayer playerIn, int index) {
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
		if(stacks.isEmpty()){
			return;
		}
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

	public final void receiveServerSearchString(String searchString) {
		if(!searchString.isEmpty())
			search = searchString;
	}


	public void receiveClientSearch(ServerPlayer sender, String search) {
		te.setLastSearch(sender, search);
	}

	public void receiveSettings(ServerPlayer sender, SortSettings settings, String selectedTab) {
		this.tabs.put(sender.getUUID(), selectedTab);
		te.setSorting(settings);
	}

	@Override
	public RecipeBookType getRecipeBookType() {
		return RecipeBookType.CRAFTING;
	}

	@Override
	public boolean shouldMoveToInventory(int p_150635_) {
		return false;
	}

	public void onInteract(ServerPlayer player, @Nullable StoredItemStack clicked, SlotAction act, boolean pullOne) {
		player.resetLastActionTime();
		if(act == SlotAction.SPACE_CLICK) {
			for (int i = playerSlotsStart + 1;i < playerSlotsStart + 28;i++) {
				quickMoveStack(player, i);
			}
		} else {
			String selectedTab = tabs.get(player.getUUID());
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
