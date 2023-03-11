package com.hollingsworth.arsnouveau.common.block.tile;

import com.google.common.collect.EvictingQueue;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.item.inv.*;
import com.hollingsworth.arsnouveau.api.util.InvUtil;
import com.hollingsworth.arsnouveau.client.container.SortSettings;
import com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu;
import com.hollingsworth.arsnouveau.client.container.StoredItemStack;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.util.ColorPos;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
import com.hollingsworth.arsnouveau.common.entity.goal.bookwyrm.TransferTask;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
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

import java.util.*;
import java.util.stream.IntStream;


public class StorageLecternTile extends ModdedTile implements MenuProvider, ITickable, IWandable, ITooltipProvider {
	private InventoryManager invManager = new InventoryManager(new ArrayList<>());
	private Map<StoredItemStack, Long> items = new HashMap<>();
	private String lastSearch = "";
	public boolean updateItems;
	public List<BlockPos> connectedInventories = new ArrayList<>();
	public List<HandlerPos> handlerPosList = new ArrayList<>();

	public SortSettings sortSettings = new SortSettings();
	public BlockPos mainLecternPos;
	public List<UUID> bookwyrmUUIDs = new ArrayList<>();
	public int backoffTicks;

	public Queue<TransferTask> transferTasks = EvictingQueue.create(10);

	public StorageLecternTile(BlockPos pos, BlockState state) {
		super(BlockRegistry.CRAFTING_LECTERN_TILE.get(), pos, state);
	}

	public StorageLecternTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
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
		addExtractTasks(pulled);
		return new StoredItemStack(pulled.getExtracted());
	}

	private void addExtractTasks(MultiExtractedReference multiSlotReference){
		if(multiSlotReference.getExtracted().isEmpty()){
			return;
		}
		for(ExtractedStack extractedStack : multiSlotReference.getSlots()){
			BlockPos pos = handlerPosList.stream().filter(handlerPos -> handlerPos.handler().equals(extractedStack.getHandler())).findFirst().map(HandlerPos::pos).orElse(null);
			if(pos != null){
				addTransferTask(new TransferTask(pos.above(), getBlockPos().above(), extractedStack.stack, level.getGameTime()));
			}
		}
	}

	private void addInsertTasks(ItemStack stack, MultiInsertReference reference){
		if(reference.isEmpty() || stack.isEmpty()){
			return;
		}
		for(SlotReference extractedStack : reference.getSlots()){
			BlockPos pos = handlerPosList.stream().filter(handlerPos -> handlerPos.handler().equals(extractedStack.getHandler())).findFirst().map(HandlerPos::pos).orElse(null);
			if(pos != null){
				addTransferTask(new TransferTask(getBlockPos().above(), pos.above(), stack, level.getGameTime()));
			}
		}
	}

	public void addTransferTask(TransferTask task){
		transferTasks.add(task);
	}


	public @Nullable TransferTask getTransferTask(){
		List<TransferTask> staleTasks = new ArrayList<>();
		TransferTask task = null;
		for(TransferTask transferTask : transferTasks){
			// Remove tasks older than 10 seconds
			if(level.getGameTime() - transferTask.gameTime > 200){
				staleTasks.add(transferTask);
			}
			task = transferTask;
			staleTasks.add(transferTask);
			break;
		}
		transferTasks.removeAll(staleTasks);
		return task;
	}


	public StoredItemStack pushStack(StoredItemStack stack) {
		if(stack == null){
			return null;
		}
		ItemStack copyStack = stack.getActualStack().copy();
		MultiInsertReference reference = invManager.insertStackWithReference(stack.getActualStack());
		ItemStack remaining = reference.getRemainder();
		if(!reference.isEmpty()){
			addInsertTasks(copyStack, reference);
		}
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
		updateItems = true;
		mainLecternPos = null;
		updateBlock();
	}

	@Override
	public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
		if(storedPos == null) {
			return;
		}
		BlockEntity tile = level.getBlockEntity(storedPos);
		if(tile instanceof StorageLecternTile){
			return;
		}
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
		this.mainLecternPos = null;
		updateBlock();
		updateItems = true;
	}

	@Override
	public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
		if(storedPos == null || storedPos.equals(worldPosition)){
			return;
		}
		BlockEntity tile = level.getBlockEntity(storedPos);
		if(!(tile instanceof StorageLecternTile storageTerminalBlockEntity)){
			PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.not_lectern"));
			return;
		}
		this.mainLecternPos = storedPos.immutable();
		this.connectedInventories = new ArrayList<>();
		PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.lectern_chained"));
		updateBlock();
	}

	@Override
	public List<ColorPos> getWandHighlight(List<ColorPos> list) {
		if(mainLecternPos != null){
			list.add(ColorPos.centered(mainLecternPos, ParticleColor.TO_HIGHLIGHT));
			return list;
		}
		for(BlockPos pos : connectedInventories){
			list.add(ColorPos.centered(pos, ParticleColor.FROM_HIGHLIGHT));
		}
		for(EntityBookwyrm bookwyrm : getBookwyrmEntities()){
			list.add(ColorPos.centered(bookwyrm.blockPosition(), ParticleColor.GREEN));
		}
		return list;
	}

	@Override
	public void tick() {
		if(level.isClientSide)
			return;
		if(backoffTicks > 0){
			backoffTicks--;
		}
		if(backoffTicks <= 0 && level.getGameTime() % 20 == 0){
			insertNearbyItems();
		}
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

	public List<EntityBookwyrm> getBookwyrmEntities(){
		List<EntityBookwyrm> bookwyrmEntities = new ArrayList<>();
		List<UUID> staleUUIDs = new ArrayList<>();
		for(UUID uuid : bookwyrmUUIDs){
			if(level instanceof ServerLevel serverLevel) {
				Entity entity = serverLevel.getEntity(uuid);
				if (entity instanceof EntityBookwyrm bookwyrm) {
					bookwyrmEntities.add(bookwyrm);
				}else{
					staleUUIDs.add(uuid);
				}
			}
		}
		bookwyrmUUIDs.removeAll(staleUUIDs);
		return bookwyrmEntities;
	}

	public void insertNearbyItems(){
		// Get adjacent inventories
		for(Direction dir : Direction.values()){
			BlockPos pos = worldPosition.relative(dir);
			BlockEntity tile = level.getBlockEntity(pos);
			if(tile == null || connectedInventories.contains(pos))
				continue;
			IItemHandler handler = tile.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
			if(handler == null)
				continue;
			for(int i = 0; i < handler.getSlots(); i++){
				ItemStack stack = handler.getStackInSlot(i);
				if(stack.isEmpty())
					continue;
				ItemStack extractedStack = handler.extractItem(i, stack.getMaxStackSize(), false);
				ItemStack remaining = this.pushStack(extractedStack);
				if(!remaining.isEmpty()){
					handler.insertItem(i, remaining, false);
				}
				return;
			}

		}
		backoffTicks = 100 + level.random.nextInt(20);
	}

	public void removeBookwyrm(EntityBookwyrm bookwyrm){
		bookwyrmUUIDs.remove(bookwyrm.getUUID());
		updateBlock();
	}

	public boolean canInteractWith(Player player) {
		return !this.isRemoved();
	}

	public boolean openMenu(Player player, List<BlockPos> visitedPos){
		if(mainLecternPos == null){
			player.openMenu(this);
			return true;
		}else {
			if (visitedPos.contains(mainLecternPos))
				return false;
			BlockEntity blockEntity = level.getBlockEntity(mainLecternPos);
			if (blockEntity instanceof StorageLecternTile storageTerminalBlockEntity) {
				visitedPos.add(mainLecternPos);
				return storageTerminalBlockEntity.openMenu(player, visitedPos);
			}
		}
		return false;
	}

	public void setSorting(SortSettings sortSettings) {
		this.sortSettings = sortSettings;
		updateBlock();
	}

	public int getMaxConnectedInventories() {
		return getBookwyrmEntities().size() * 6;
	}

	public @Nullable EntityBookwyrm addBookwyrm(){
		if(level.isClientSide)
			return null;
		EntityBookwyrm bookwyrm = new EntityBookwyrm(level, this.getBlockPos());
		bookwyrm.setPos(this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 1, this.getBlockPos().getZ() + 0.5);
		level.addFreshEntity(bookwyrm);
		bookwyrmUUIDs.add(bookwyrm.getUUID());
		updateBlock();
		return bookwyrm;
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
		if(mainLecternPos != null){
			compound.putLong("mainLecternPos", mainLecternPos.asLong());
		}
		ListTag bookwyrmList = new ListTag();
		for(UUID uuid : bookwyrmUUIDs){
			bookwyrmList.add(NbtUtils.createUUID(uuid));
		}
		compound.put("bookwyrmUUIDs", bookwyrmList);
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
		if(compound.contains("mainLecternPos")){
			mainLecternPos = BlockPos.of(compound.getLong("mainLecternPos"));
		}
		if(compound.contains("bookwyrmUUIDs")){
			bookwyrmUUIDs.clear();
			ListTag bookwyrmList = compound.getList("bookwyrmUUIDs", 11);
			for (Tag tag : bookwyrmList) {
				bookwyrmUUIDs.add(NbtUtils.loadUUID(tag));
			}
		}
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
		if(mainLecternPos != null){
			tooltip.add(Component.translatable("ars_nouveau.storage.lectern_chained", mainLecternPos.getX(), mainLecternPos.getY(), mainLecternPos.getZ()));
		}else {
			tooltip.add(Component.translatable("ars_nouveau.storage.num_connected", connectedInventories.size(), getMaxConnectedInventories()));
			tooltip.add(Component.translatable("ars_nouveau.storage.num_bookwyrms", bookwyrmUUIDs.size()));
		}
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
