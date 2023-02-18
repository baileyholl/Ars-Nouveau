package com.hollingsworth.arsnouveau.common.tss.platform;

import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.tss.platform.gui.StorageTerminalMenu;
import com.hollingsworth.arsnouveau.common.tss.platform.util.StoredItemStack;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;


public class StorageTerminalBlockEntity extends BlockEntity implements MenuProvider, ITickable {
	private IItemHandler itemHandler;
	private Map<StoredItemStack, Long> items = new HashMap<>();
	private int sort;
	private String lastSearch = "";
	private boolean updateItems;
	private int beaconLevel;

	public StorageTerminalBlockEntity(BlockPos pos, BlockState state) {
		super(BlockRegistry.STORAGE_TERMINAL_TILE.get(), pos, state);
	}

	public StorageTerminalBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory plInv, Player arg2) {
		return new StorageTerminalMenu(id, plInv, this);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("ts.storage_terminal");
	}

	public Map<StoredItemStack, Long> getStacks() {
		updateItems = true;
		return items;
	}

	public StoredItemStack pullStack(StoredItemStack stack, long max) {
		if(stack != null && itemHandler != null && max > 0) {
			ItemStack st = stack.getStack();
			StoredItemStack ret = null;
			for (int i = itemHandler.getSlots() - 1; i >= 0; i--) {
				ItemStack s = itemHandler.getStackInSlot(i);
				if(ItemStack.isSame(s, st) && ItemStack.tagMatches(s, st)) {
					ItemStack pulled = itemHandler.extractItem(i, (int) max, false);
					if(!pulled.isEmpty()) {
						if(ret == null)ret = new StoredItemStack(pulled);
						else ret.grow(pulled.getCount());
						max -= pulled.getCount();
						if(max < 1)break;
					}
				}
			}
			return ret;
		}
		return null;
	}

	public StoredItemStack pushStack(StoredItemStack stack) {
		if(stack != null && itemHandler != null) {
			ItemStack is = ItemHandlerHelper.insertItemStacked(itemHandler, stack.getActualStack(), false);
			if(is.isEmpty())return null;
			else {
				return new StoredItemStack(is);
			}
		}
		return stack;
	}

	public ItemStack pushStack(ItemStack itemstack) {
		StoredItemStack is = pushStack(new StoredItemStack(itemstack));
		return is == null ? ItemStack.EMPTY : is.getActualStack();
	}

	public void pushOrDrop(ItemStack st) {
		if(st.isEmpty())return;
		StoredItemStack st0 = pushStack(new StoredItemStack(st));
		if(st0 != null) {
			Containers.dropItemStack(level, worldPosition.getX() + .5f, worldPosition.getY() + .5f, worldPosition.getZ() + .5f, st0.getActualStack());
		}
	}

	@Override
	public void tick() {
		if(level.isClientSide)
			return;
		if(updateItems) {
			BlockState st = level.getBlockState(worldPosition);
			Direction d = st.getValue(AbstractStorageTerminalBlock.FACING);
			AbstractStorageTerminalBlock.TerminalPos p = st.getValue(AbstractStorageTerminalBlock.TERMINAL_POS);
			if(p == AbstractStorageTerminalBlock.TerminalPos.UP)d = Direction.UP;
			if(p == AbstractStorageTerminalBlock.TerminalPos.DOWN)d = Direction.DOWN;
			BlockEntity invTile = level.getBlockEntity(worldPosition.relative(d));
			items.clear();
			if(invTile != null) {
				LazyOptional<IItemHandler> lih = invTile.getCapability(ForgeCapabilities.ITEM_HANDLER, d.getOpposite());
				itemHandler = lih.orElse(null);
				if(itemHandler != null) {
					IntStream.range(0, itemHandler.getSlots()).mapToObj(itemHandler::getStackInSlot).filter(s -> !s.isEmpty()).
					map(StoredItemStack::new).forEach(s -> items.merge(s, s.getQuantity(), (a, b) -> a + b));
				}
			}
			updateItems = false;
		}
	}

	public boolean canInteractWith(Player player) {
		return level.getBlockEntity(worldPosition) == this;
	}

	public int getSorting() {
		return sort;
	}

	public void setSorting(int newC) {
		sort = newC;
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		compound.putInt("sort", sort);
	}

	@Override
	public void load(CompoundTag compound) {
		sort = compound.getInt("sort");
		super.load(compound);
	}

	public String getLastSearch() {
		return lastSearch;
	}

	public void setLastSearch(String string) {
		lastSearch = string;
	}

	public int getBeaconLevel() {
		return beaconLevel;
	}
}
