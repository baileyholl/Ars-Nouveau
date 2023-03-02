package com.hollingsworth.arsnouveau.common.tss.platform;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.item.inv.*;
import com.hollingsworth.arsnouveau.api.util.InvUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.tss.platform.gui.StorageTerminalMenu;
import com.hollingsworth.arsnouveau.common.tss.platform.util.StoredItemStack;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;


public class StorageTerminalBlockEntity extends ModdedTile implements MenuProvider, ITickable, IWandable, ITooltipProvider {
	private InventoryManager invManager = new InventoryManager(new ArrayList<>());
	private Map<StoredItemStack, Long> items = new HashMap<>();
	private String lastSearch = "";
	private boolean updateItems;
	private List<BlockPos> connectedInventories = new ArrayList<>();
	private List<HandlerPos> handlerPosList = new ArrayList<>();
	private int numBookwyrms;

	public SortSettings sortSettings = new SortSettings();

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
		return Component.translatable("ars_nouveau.storage_lectern");
	}

	public Map<StoredItemStack, Long> getStacks() {
		updateItems = true;
		return items;
	}

	public StoredItemStack pullStack(StoredItemStack stack, int max) {
		if (stack == null || max <= 0) {
			return null;
		}
		ItemStack st = stack.getStack();
		MultiExtractedReference pulled = invManager.extractItemFromAll(st, max, true);
		if(pulled.getExtracted().isEmpty()) {
			return null;
		}
		spawnEffects(pulled);
		return new StoredItemStack(pulled.getExtracted());
	}

	private void spawnEffects(MultiExtractedReference multiSlotReference){
		if(multiSlotReference.getExtracted().isEmpty()){
			return;
		}
		for(ExtractedStack extractedStack : multiSlotReference.getSlots()){
			BlockPos pos = handlerPosList.stream().filter(handlerPos -> handlerPos.handler().equals(extractedStack.getHandler())).findFirst().map(HandlerPos::pos).orElse(null);
			if(pos != null){
				EntityFlyingItem entityFlyingItem = new EntityFlyingItem(level,
						pos,
						getBlockPos().above()).setStack(extractedStack.stack);
				level.addFreshEntity(entityFlyingItem);
			}
		}

	}

	public StoredItemStack pushStack(StoredItemStack stack) {
		if(stack == null){
			return null;
		}
		ItemStack remaining = invManager.insertStack(stack.getActualStack());
		if(remaining.isEmpty()){
			return null;
		}
		return new StoredItemStack(remaining);
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
	public void onWanded(Player playerEntity) {
		this.connectedInventories = new ArrayList<>();
		PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.cleared"));
		updateBlock();
		updateItems = true;
	}

	@Override
	public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
		if(storedPos != null) {
			BlockEntity tile = level.getBlockEntity(storedPos);
			if(tile == null){
				PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.no_tile"));
				return;
			}
			IItemHandler handler = tile.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
			if(handler == null){
				PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.no_tile"));
				return;
			}
			if(this.connectedInventories.size() >= this.getMaxConnectedInventories()){
				PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.too_many"));
				return;
			}
			this.connectedInventories.add(storedPos.immutable());
			PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.from_set"));
			updateBlock();
			updateItems = true;
		}
	}

	@Override
	public void tick() {
		if(level.isClientSide)
			return;
		if(updateItems) {
			items.clear();
			List<FilterableItemHandler> handlers = new ArrayList<>();
			List<IItemHandlerModifiable> modifiables = new ArrayList<>();
			this.handlerPosList = new ArrayList<>();
			for(BlockPos pos : connectedInventories) {
				BlockEntity invTile = level.getBlockEntity(pos);
				if(invTile != null) {
					LazyOptional<IItemHandler> lih = invTile.getCapability(ForgeCapabilities.ITEM_HANDLER, null);
					lih.ifPresent(i -> {
						if(i instanceof IItemHandlerModifiable handlerModifiable) {
							handlers.add(new StorageItemHandler(handlerModifiable, InvUtil.filtersOnTile(invTile)));
							modifiables.add(handlerModifiable);
							handlerPosList.add(new HandlerPos(pos, i));
						}
					});
				}
			}
			invManager = new InventoryManager(handlers);
			CombinedInvWrapper itemHandler = new CombinedInvWrapper(modifiables.toArray(new IItemHandlerModifiable[0]));
			IntStream.range(0, itemHandler.getSlots())
					.mapToObj(itemHandler::getStackInSlot)
					.filter(s -> !s.isEmpty())
					.map(StoredItemStack::new)
					.forEach(s -> items.merge(s, s.getQuantity(), Long::sum));
			updateItems = false;
		}
	}

	public boolean canInteractWith(Player player) {
		return true;
	}

	public void setSorting(SortSettings sortSettings) {
		this.sortSettings = sortSettings;
		updateBlock();
	}

	public int getMaxConnectedInventories() {
		return 4 + numBookwyrms * 4;
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		compound.put("sortSettings", sortSettings.toTag());
		ListTag list = new ListTag();
		for (BlockPos pos : connectedInventories) {
			CompoundTag c = new CompoundTag();
			c.putInt("x", pos.getX());
			c.putInt("y", pos.getY());
			c.putInt("z", pos.getZ());
			list.add(c);
		}
		compound.put("invs", list);
		compound.putInt("numBookwyrms", numBookwyrms);
	}

	@Override
	public void load(CompoundTag compound) {
		if(compound.contains("sortSettings")) {
			sortSettings = SortSettings.fromTag(compound.getCompound("sortSettings"));
		}
		ListTag list = compound.getList("invs", 10);
		connectedInventories.clear();
		for (int i = 0; i < list.size(); i++) {
			CompoundTag c = list.getCompound(i);
			connectedInventories.add(new BlockPos(c.getInt("x"), c.getInt("y"), c.getInt("z")));
		}
		numBookwyrms = compound.getInt("numBookwyrms");
		super.load(compound);
	}

	public String getLastSearch() {
		return lastSearch;
	}

	public void setLastSearch(String string) {
		lastSearch = string;
	}

	@Override
	public void getTooltip(List<Component> tooltip) {
		tooltip.add(Component.translatable("ars_nouveau.storage.num_connected", connectedInventories.size(), getMaxConnectedInventories()));
	}

	public record HandlerPos(BlockPos pos, IItemHandler handler){
		public static @Nullable HandlerPos fromLevel(Level level, BlockPos pos){
			BlockEntity tile = level.getBlockEntity(pos);
			if(tile == null){
				return null;
			}
			IItemHandler handler = tile.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
			if(handler == null){
				return null;
			}
			return new HandlerPos(pos, handler);
		}
	}
}
