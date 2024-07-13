package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import com.hollingsworth.arsnouveau.client.container.StoredItemStack;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class CraftingLecternTile extends StorageLecternTile implements GeoBlockEntity {
	private AbstractContainerMenu craftingContainer = new AbstractContainerMenu(MenuType.CRAFTING, 0) {
		@Override
		public boolean stillValid(Player player) {
			return false;
		}

		@Override
		public void slotsChanged(Container inventory) {
			if (level != null && !level.isClientSide) {
				onCraftingMatrixChanged();
			}
		}

		@Override
		public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
			return ItemStack.EMPTY;
		}
	};
	private CraftingRecipe currentRecipe;
	private final TransientCustomContainer craftMatrix = new TransientCustomContainer(craftingContainer, 3, 3);
	private ResultContainer craftResult = new ResultContainer();
	private HashSet<CraftingTerminalMenu> craftingListeners = new HashSet<>();


	public CraftingLecternTile(BlockPos pos, BlockState state) {
		super(BlockRegistry.CRAFTING_LECTERN_TILE.get(), pos, state);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory plInv, Player arg2) {
		return new CraftingTerminalMenu(id, plInv, this);
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
		super.saveAdditional(tag, pRegistries);

		ListTag listnbt = new ListTag();

		for(int i = 0; i < craftMatrix.getContainerSize(); ++i) {
			ItemStack itemstack = craftMatrix.getItem(i);
			if (!itemstack.isEmpty()) {
				CompoundTag compoundnbt = new CompoundTag();
				compoundnbt.putInt("Slot", i);
				compoundnbt.put("item", itemstack.save(pRegistries));
				listnbt.add(compoundnbt);
			}
		}

		tag.put("CraftingTable", listnbt);
	}

	private boolean reading;
	@Override
	protected void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
		super.loadAdditional(compound, pRegistries);
		reading = true;
		ListTag listnbt = compound.getList("CraftingTable", 10);

		for(int i = 0; i < listnbt.size(); ++i) {
			CompoundTag compoundnbt = listnbt.getCompound(i);
			int j = compoundnbt.getInt("Slot");
			if (j >= 0 && j < craftMatrix.getContainerSize()) {
				craftMatrix.setItem(j, ItemStack.parseOptional(pRegistries, compoundnbt.getCompound("item")));
			}
		}
		reading = false;
	}

	public CraftingContainer getCraftingInv() {
		return craftMatrix;
	}

	public ResultContainer getCraftResult() {
		return craftResult;
	}

	public void craftShift(Player player, @Nullable String tab) {
		List<ItemStack> craftedItemsList = new ArrayList<>();
		int amountCrafted = 0;
		ItemStack crafted = craftResult.getItem(0);
		do {
			craft(player, tab);
			craftedItemsList.add(crafted.copy());
			amountCrafted += crafted.getCount();
		} while(ItemStack.isSameItem(crafted, craftResult.getItem(0)) && (amountCrafted+crafted.getCount()) <= crafted.getMaxStackSize());

		for (ItemStack craftedItem : craftedItemsList) {
			if (!player.getInventory().add(craftedItem.copy())) {
				ItemStack is = pushStack(craftedItem, tab);
				if(!is.isEmpty()) {
					Containers.dropItemStack(level, player.getX(), player.getY(), player.getZ(), is);
				}
			}
		}

		crafted.onCraftedBy(player.level, player, amountCrafted);
		var copyStack = crafted.copy();
		copyStack.setCount(amountCrafted);
		EventHooks.firePlayerCraftingEvent(player, copyStack, craftMatrix);
	}

	public void craft(Player thePlayer, @Nullable String tab) {
		if(currentRecipe == null) {
			return;
		}
		NonNullList<ItemStack> remainder = currentRecipe.getRemainingItems(craftMatrix.asCraftInput());
		boolean playerInvUpdate = false;
		for (int i = 0; i < remainder.size(); ++i) {
			ItemStack currentStack = craftMatrix.getItem(i);
			ItemStack oldItem = currentStack.copy();
			ItemStack rem = remainder.get(i);
			if (!currentStack.isEmpty()) {
				craftMatrix.removeItemNoUpdate(i, 1);
				currentStack = craftMatrix.getItem(i);
			}
			if(currentStack.isEmpty() && !oldItem.isEmpty()) {
				StoredItemStack is = pullStack(new StoredItemStack(oldItem), 1, tab);
				if(is == null) {
					for(int j = 0;j<thePlayer.getInventory().getContainerSize();j++) {
						ItemStack st = thePlayer.getInventory().getItem(j);
						if(ItemStack.isSameItem(oldItem, st) && ItemStack.matches(oldItem, st)) {
							st = thePlayer.getInventory().removeItem(j, 1);
							if(!st.isEmpty()) {
								is = new StoredItemStack(st, 1);
								playerInvUpdate = true;
								break;
							}
						}
					}
				}
				if(is != null) {
					craftMatrix.setItemNoUpdate(i, is.getActualStack());
					currentStack = craftMatrix.getItem(i);
				}
			}
			if (rem.isEmpty()) {
				continue;
			}
			if (currentStack.isEmpty()) {
				craftMatrix.setItemNoUpdate(i, rem);
				continue;
			}
			if (ItemStack.isSameItem(currentStack, rem) && ItemStack.matches(currentStack, rem)) {
				rem.grow(currentStack.getCount());
				craftMatrix.setItemNoUpdate(i, rem);
				continue;
			}
			rem = pushStack(rem, tab);
			if(rem.isEmpty() || thePlayer.getInventory().add(rem)){
				continue;
			}
			thePlayer.drop(rem, false);
		}
		if(playerInvUpdate) {
			thePlayer.containerMenu.broadcastChanges();
		}
		onCraftingMatrixChanged();
	}

	public void unregisterCrafting(CraftingTerminalMenu containerCraftingTerminal) {
		craftingListeners.remove(containerCraftingTerminal);
	}

	public void registerCrafting(CraftingTerminalMenu containerCraftingTerminal) {
		craftingListeners.add(containerCraftingTerminal);
	}

	protected void onCraftingMatrixChanged() {
		if (currentRecipe == null || !currentRecipe.matches(craftMatrix.asCraftInput(), level)) {
			var holder = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftMatrix.asCraftInput(), level).orElse(null);
			if(holder != null){
				currentRecipe = holder.value();
			}
		}

		if (currentRecipe == null) {
			craftResult.setItem(0, ItemStack.EMPTY);
		} else {
			craftResult.setItem(0, currentRecipe.assemble(craftMatrix.asCraftInput(), level.registryAccess()));
		}

		craftingListeners.forEach(CraftingTerminalMenu::onCraftMatrixChanged);

		if (!reading) {
			setChanged();
		}
	}

	public void clear(@Nullable String tab) {
		for (int i = 0; i < craftMatrix.getContainerSize(); i++) {
			ItemStack st = craftMatrix.removeItemNoUpdate(i);
			if(!st.isEmpty()) {
				pushOrDrop(st, tab);
			}
		}
		onCraftingMatrixChanged();
	}

	public void transferToGrid(Player player, ItemStack[][] ingredients, @Nullable String tab) {
		clear(tab);
		for (int i = 0; i < 9; i++) {
			ItemStack[] ingredient = ingredients[i];
			if (ingredient == null) {
				continue;
			}
			Map<Item, Long> inv = itemCounts;
			// sort ingredient by the amount of items in the inv map
			ingredient = Arrays.stream(ingredient).filter(Objects::nonNull).sorted(Comparator.comparingLong(a -> inv.getOrDefault(((ItemStack)a).getItem(), 0L)).reversed()).toArray(ItemStack[]::new);

			// Sort ingredient by the amount of items in this inventory
			ItemStack stack = ItemStack.EMPTY;
			for (ItemStack itemStack : ingredient) {
				ItemStack pulled = pullStack(itemStack, tab);
				if (!pulled.isEmpty()) {
					stack = pulled;
					break;
				}
			}
			if (stack.isEmpty()) {
				for (ItemStack itemStack : ingredient) {
					boolean br = false;
					Inventory playerInv = player.getInventory();
					for (int k = 0; k < playerInv.getContainerSize(); k++) {
						if (ItemStack.isSameItem(playerInv.getItem(k), itemStack)) {
							stack = playerInv.removeItem(k, 1);
							br = true;
							break;
						}
					}
					if (br)
						break;
				}
			}
			if (!stack.isEmpty()) {
				craftMatrix.setItem(i, stack);
			}
		}
		onCraftingMatrixChanged();
	}

	private ItemStack pullStack(ItemStack itemStack, @Nullable String tab) {
		StoredItemStack is = pullStack(new StoredItemStack(itemStack), 1, tab);
		if(is == null)return ItemStack.EMPTY;
		else return is.getActualStack();
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "controller", 1, (event -> {
			event.getController().setAnimation(RawAnimation.begin().thenPlay("ledger_float"));
			return PlayState.CONTINUE;
		})));
	}
	AnimatableInstanceCache AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return AnimatableInstanceCache;
	}
}
