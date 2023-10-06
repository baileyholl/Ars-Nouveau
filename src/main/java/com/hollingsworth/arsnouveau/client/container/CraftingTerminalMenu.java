package com.hollingsworth.arsnouveau.client.container;

import com.google.common.collect.Lists;
import com.hollingsworth.arsnouveau.common.menu.MenuRegistry;
import com.hollingsworth.arsnouveau.common.block.tile.CraftingLecternTile;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CraftingTerminalMenu extends StorageTerminalMenu implements IAutoFillTerminal {
	public static class SlotCrafting extends Slot {
		public boolean active;

		public SlotCrafting(Container inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
			active = true;
		}

		@Override
		public boolean isActive() {
			return super.isActive() && active;
		}
	}

	public static class ActiveResultSlot extends ResultSlot{
		public boolean active;

		public ActiveResultSlot(Player pPlayer, CraftingContainer pCraftSlots, Container pContainer, int pSlot, int pXPosition, int pYPosition) {
			super(pPlayer, pCraftSlots, pContainer, pSlot, pXPosition, pYPosition);
			active = true;
		}

		@Override
		public boolean isActive() {
			return active;
		}
	}
	protected List<SlotCrafting> craftSlotList = new ArrayList<>();
	private final CraftingContainer craftMatrix;
	private final ResultContainer craftResult;
	private ActiveResultSlot craftingResultSlot;
	private final List<ContainerListener> listeners = Lists.newArrayList();

	@Override
	public void addSlotListener(ContainerListener listener) {
		super.addSlotListener(listener);
		listeners.add(listener);
	}

	@Override
	public void removeSlotListener(ContainerListener listener) {
		super.removeSlotListener(listener);
		listeners.remove(listener);
	}

	public CraftingTerminalMenu(int id, Inventory inv, CraftingLecternTile te) {
		super(MenuRegistry.STORAGE.get(), id, inv, te);
		craftMatrix = te.getCraftingInv();
		craftResult = te.getCraftResult();
		init();
		this.addPlayerSlots(inv, 13, 157);
		te.registerCrafting(this);
	}

	public CraftingTerminalMenu(int id, Inventory inv) {
		super(MenuRegistry.STORAGE.get(), id, inv);
		craftMatrix = new CraftingContainer(this, 3, 3);
		craftResult = new ResultContainer();
		init();
		this.addPlayerSlots(inv, 13, 157);
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		if(te instanceof CraftingLecternTile craftingLecternTile) {
			craftingLecternTile.unregisterCrafting(this);
		}
	}

	private void init() {
		int x = -4;
		int y = 70;
		this.addSlot(craftingResultSlot = new ActiveResultSlot(pinv.player, craftMatrix, craftResult, 0, x + 130, y + 37) {
			@Override
			public void onTake(Player thePlayer, ItemStack stack) {
				if (thePlayer.level.isClientSide)
					return;
				this.checkTakeAchievements(stack);
				if (!pinv.player.getCommandSenderWorld().isClientSide) {
					((CraftingLecternTile) te).craft(thePlayer, selectedTab);
				}
			}
		});
		if(this.terminalData == null || !terminalData.expanded) {
			for (int i = 0; i < 3; ++i) {
				for (int j = 0; j < 3; ++j) {
					SlotCrafting slot = new SlotCrafting(craftMatrix, j + i * 3, x + 36 + j * 18,  89 + i * 18);
					this.addSlot(slot);
					this.craftSlotList.add(slot);
				}
			}
		}
	}

	@Override
	protected void addStorageSlots() {
		addStorageSlots(13, 21);
	}

	@Override
	public void addStorageSlots(int slotOffsetX, int slotOffsetY) {
		super.addStorageSlots(slotOffsetX, slotOffsetY);
		if(craftSlotList != null && terminalData != null){
			for(SlotCrafting slot : craftSlotList){
				slot.active = !terminalData.expanded;
			}
			craftingResultSlot.active = !terminalData.expanded;
		}
	}

	@Override
	public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
		return slotIn.container != craftResult && super.canTakeItemForPickAll(stack, slotIn);
	}

	@Override
	public ItemStack shiftClickItems(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index == 0) {
				if(te == null)return ItemStack.EMPTY;
				((CraftingLecternTile) te).craftShift(playerIn, selectedTab);
				if (!playerIn.level.isClientSide)
					broadcastChanges();
				return ItemStack.EMPTY;
			} else if (index > 0 && index < 10) {
				if(te == null)return ItemStack.EMPTY;
				ItemStack stack = te.pushStack(itemstack, selectedTab);
				slot.set(stack);
				if (!playerIn.level.isClientSide)
					broadcastChanges();
			}
			slot.onTake(playerIn, itemstack1);
		}
		return ItemStack.EMPTY;
	}

	public void onCraftMatrixChanged() {
		for (int i = 0; i < slots.size(); ++i) {
			Slot slot = slots.get(i);

			if (slot instanceof SlotCrafting || slot == craftingResultSlot) {
				for (ContainerListener listener : listeners) {
					if (listener instanceof ServerPlayer) {
						((ServerPlayer) listener).connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), i, slot.getItem()));
					}
				}
			}
		}
	}

	@Override
	public boolean clickMenuButton(Player playerIn, int id) {
		if(te != null && id == 0)
			((CraftingLecternTile) te).clear(selectedTab);
		else super.clickMenuButton(playerIn, id);
		return false;
	}

	@Override
	public void fillCraftSlotsStackedContents(StackedContents itemHelperIn) {
		this.craftMatrix.fillStackedContents(itemHelperIn);
	}

	@Override
	public void clearCraftingContent() {
		this.craftMatrix.clearContent();
		this.craftResult.clearContent();
	}

	@Override
	public boolean recipeMatches(Recipe<? super CraftingContainer> recipeIn) {
		return recipeIn.matches(this.craftMatrix, this.pinv.player.level);
	}

	@Override
	public int getResultSlotIndex() {
		return 0;
	}

	@Override
	public int getGridWidth() {
		return this.craftMatrix.getWidth();
	}

	@Override
	public int getGridHeight() {
		return this.craftMatrix.getHeight();
	}

	@Override
	public int getSize() {
		return 10;
	}

	@Override
	public List<RecipeBookCategories> getRecipeBookCategories() {
		return Lists.newArrayList(RecipeBookCategories.CRAFTING_SEARCH, RecipeBookCategories.CRAFTING_EQUIPMENT, RecipeBookCategories.CRAFTING_BUILDING_BLOCKS, RecipeBookCategories.CRAFTING_MISC, RecipeBookCategories.CRAFTING_REDSTONE);
	}

	public class TerminalRecipeItemHelper extends StackedContents {
		@Override
		public void clear() {
			super.clear();
			itemList.forEach(e -> {
				accountSimpleStack(e.getActualStack());
			});
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void handlePlacement(boolean p_217056_1_, Recipe<?> p_217056_2_, ServerPlayer p_217056_3_) {
		(new ServerPlaceRecipe(this) {
			{
				stackedContents = new TerminalRecipeItemHelper();
			}



			@Override
			public void addItemToSlot(Iterator pIngredients, int pSlot, int pMaxAmount, int pY, int pX) {
				Slot slot = this.menu.getSlot(pSlot);
				ItemStack itemstack = StackedContents.fromStackingIndex((Integer) pIngredients.next());
				if (!itemstack.isEmpty()) {
					for(int i = 0; i < pMaxAmount; ++i) {
						this.moveItemToGrid(slot, itemstack);
					}
				}
			}

			@Override
			protected void moveItemToGrid(Slot slotToFill, ItemStack ingredientIn) {
				int i = this.inventory.findSlotMatchingUnusedItem(ingredientIn);
				if (i != -1) {
					ItemStack itemstack = this.inventory.getItem(i).copy();
					if (!itemstack.isEmpty()) {
						if (itemstack.getCount() > 1) {
							this.inventory.removeItem(i, 1);
						} else {
							this.inventory.removeItemNoUpdate(i);
						}

						itemstack.setCount(1);
						if (slotToFill.getItem().isEmpty()) {
							slotToFill.set(itemstack);
						} else {
							slotToFill.getItem().grow(1);
						}

					}
				} else if(te != null) {
					StoredItemStack st = te.pullStack(new StoredItemStack(ingredientIn), 1, selectedTab);
					if(st != null) {
						if (slotToFill.getItem().isEmpty()) {
							slotToFill.set(st.getActualStack());
						} else {
							slotToFill.getItem().grow(1);
						}
					}
				}
			}

			@Override
			protected void clearGrid(boolean bool) {
				((CraftingLecternTile) te).clear(selectedTab);
				this.menu.clearCraftingContent();
			}
		}).recipeClicked(p_217056_3_, p_217056_2_, p_217056_1_);
	}

	@Override
	public void receive(CompoundTag message) {
		super.receive(message);
		if(message.contains("i")) {
			ItemStack[][] stacks = new ItemStack[9][];
			ListTag list = message.getList("i", 10);
			for (int i = 0;i < list.size();i++) {
				CompoundTag nbttagcompound = list.getCompound(i);
				byte slot = nbttagcompound.getByte("s");
				byte l = nbttagcompound.getByte("l");
				stacks[slot] = new ItemStack[l];
				for (int j = 0;j < l;j++) {
					CompoundTag tag = nbttagcompound.getCompound("i" + j);
					stacks[slot][j] = ItemStack.of(tag);
				}
			}
			((CraftingLecternTile) te).transferToGrid(pinv.player, stacks, selectedTab);
		}
	}

	@Override
	public List<StoredItemStack> getStoredItems() {
		return itemList;
	}
}
