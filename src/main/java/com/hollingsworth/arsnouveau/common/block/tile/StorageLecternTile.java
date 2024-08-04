package com.hollingsworth.arsnouveau.common.block.tile;

import com.google.common.collect.EvictingQueue;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.item.inv.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.InvUtil;
import com.hollingsworth.arsnouveau.client.container.SortSettings;
import com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu;
import com.hollingsworth.arsnouveau.client.container.StoredItemStack;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
import com.hollingsworth.arsnouveau.common.entity.goal.bookwyrm.TransferTask;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class StorageLecternTile extends ModdedTile implements MenuProvider, ITickable, IWandable, ITooltipProvider, ICapabilityProvider<CraftingLecternTile, Direction, IItemHandler> {
    public Map<String, InventoryManager> tabManagerMap = new HashMap<>();
    public Map<String, Map<StoredItemStack, Long>> itemsByTab = new HashMap<>();
    public Map<Item, Long> itemCounts = new HashMap<>();
    public String lastSearch = "";
    public boolean updateItems;
    public List<BlockPos> connectedInventories = new ArrayList<>();
    public List<String> tabNames = new ArrayList<>();
    public List<HandlerPos> handlerPosList = new ArrayList<>();

    public SortSettings sortSettings = new SortSettings();
    public BlockPos mainLecternPos;
    public List<UUID> bookwyrmUUIDs = new ArrayList<>();
    public int backoffTicks;
    public int checkPlayerRangeTicks;
    public boolean canCreateTasks = false;
    public static final String TAB_ALL = "8f6fe318-4ca6-4b29-ab63-15ec5289f5c9";

    public Queue<TransferTask> transferTasks = EvictingQueue.create(10);
    IItemHandler lecternInvWrapper;


    public StorageLecternTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.CRAFTING_LECTERN_TILE.get(), pos, state);
    }

    public StorageLecternTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public InventoryManager getInvManager(@Nullable String tab) {
        if (tab == null || tab.isEmpty())
            return tabManagerMap.getOrDefault(TAB_ALL, new InventoryManager());
        return tabManagerMap.getOrDefault(tab, tabManagerMap.getOrDefault(TAB_ALL, new InventoryManager()));
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory plInv, Player arg2) {
        return new StorageTerminalMenu(id, plInv, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("ars_nouveau.storage_lectern");
    }

    public Map<StoredItemStack, Long> getStacks(@Nullable String tabName) {
        updateItems = true;
        if (tabName == null || tabName.isEmpty()) {
            return itemsByTab.getOrDefault(TAB_ALL, new HashMap<>());
        }
        return itemsByTab.getOrDefault(tabName, itemsByTab.getOrDefault(TAB_ALL, new HashMap<>()));
    }

    public List<String> getTabNames() {
        tabNames = new ArrayList<>();
        for (BlockPos pos : connectedInventories) {
            BlockEntity tile = level.getBlockEntity(pos);
            if (tile instanceof Nameable provider && provider.hasCustomName()) {
                String tabName = provider.getCustomName().getString().trim();
                if (!tabName.isEmpty()) {
                    tabNames.add(provider.getDisplayName().getString());
                }
            }
        }
        return tabNames;
    }

    public StoredItemStack pullStack(StoredItemStack stack, int max, @Nullable String tabName) {
        if (stack == null || max <= 0) {
            return null;
        }
        ItemStack st = stack.getStack();
        MultiExtractedReference pulled = getInvManager(tabName).extractItemFromAll(st, max, true);
        if (pulled.getExtracted().isEmpty()) {
            return null;
        }
        addExtractTasks(pulled);
        return new StoredItemStack(pulled.getExtracted());
    }

    private void addExtractTasks(MultiExtractedReference multiSlotReference) {
        if (multiSlotReference.getExtracted().isEmpty()) {
            return;
        }
        for (ExtractedStack extractedStack : multiSlotReference.getSlots()) {
            BlockPos pos = handlerPosList.stream().filter(handlerPos -> handlerPos.handler().equals(extractedStack.getHandler())).findFirst().map(HandlerPos::pos).orElse(null);
            if (pos != null) {
                addTransferTask(new TransferTask(pos.above(), getBlockPos().above(), extractedStack.stack, level.getGameTime()));
            }
        }
    }

    private void addInsertTasks(ItemStack stack, MultiInsertReference reference) {
        if (reference.isEmpty() || stack.isEmpty()) {
            return;
        }
        for (SlotReference extractedStack : reference.getSlots()) {
            BlockPos pos = handlerPosList.stream().filter(handlerPos -> handlerPos.handler().equals(extractedStack.getHandler())).findFirst().map(HandlerPos::pos).orElse(null);
            if (pos != null) {
                addTransferTask(new TransferTask(getBlockPos().above(), pos.above(), stack, level.getGameTime()));
            }
        }
    }

    public void addTransferTask(TransferTask task) {
        if (!canCreateTasks) {
            return;
        }
        transferTasks.add(task);
    }


    public @Nullable TransferTask getTransferTask() {
        List<TransferTask> staleTasks = new ArrayList<>();
        TransferTask task = null;
        for (TransferTask transferTask : transferTasks) {
            // Remove tasks older than 10 seconds
            if (level.getGameTime() - transferTask.gameTime > 200) {
                staleTasks.add(transferTask);
            }
            task = transferTask;
            staleTasks.add(transferTask);
            break;
        }
        transferTasks.removeAll(staleTasks);
        return task;
    }


    public StoredItemStack pushStack(StoredItemStack stack, @Nullable String tab) {
        if (stack == null) {
            return null;
        }
        ItemStack copyStack = stack.getActualStack().copy();
        MultiInsertReference reference = getInvManager(tab).insertStackWithReference(stack.getActualStack());
        ItemStack remaining = reference.getRemainder();
        if (!reference.isEmpty()) {
            addInsertTasks(copyStack, reference);
        }
        if (remaining.isEmpty()) {
            return null;
        }
        return new StoredItemStack(remaining);
    }

    public ItemStack pushStack(ItemStack itemstack, @Nullable String tab) {
        StorageLecternTile mainLectern = getMainLectern();
        if (mainLectern == null) {
            return itemstack;
        }
        StoredItemStack is = mainLectern.pushStack(new StoredItemStack(itemstack), tab);
        return is == null ? ItemStack.EMPTY : is.getActualStack();
    }

    public void pushOrDrop(ItemStack st, @Nullable String tabName) {
        if (st.isEmpty()) return;
        StoredItemStack st0 = pushStack(new StoredItemStack(st), tabName);
        if (st0 != null) {
            Containers.dropItemStack(level, worldPosition.getX() + .5f, worldPosition.getY() + .5f, worldPosition.getZ() + .5f, st0.getActualStack());
        }
    }

    @Override
    public void onWanded(Player playerEntity) {
        mainLecternPos = null;
        updateBlock();
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable Direction side, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos == null || level == null) {
            return;
        }

        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, storedPos, side);
        if (handler == null) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.no_tile"));
            return;
        }
        if (BlockUtil.distanceFrom(storedPos, worldPosition) > 30) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.inv_too_far"));
            return;
        }

        if (this.connectedInventories.contains(storedPos)) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.removed"));
            this.connectedInventories.remove(storedPos);
        } else {
            if (this.connectedInventories.size() >= this.getMaxConnectedInventories()) {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.too_many"));
                return;
            }
            this.connectedInventories.add(storedPos.immutable());
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.from_set"));
        }
        this.mainLecternPos = null;
        updateBlock();
        updateItems = true;
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable Direction side, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos == null || storedPos.equals(worldPosition) || level == null) {
            return;
        }
        BlockEntity tile = level.getBlockEntity(storedPos);
        if (!(tile instanceof StorageLecternTile)) {
            return;
        }
        if (BlockUtil.distanceFrom(storedPos, worldPosition) > 30) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.lectern_too_far"));
            return;
        }
        this.mainLecternPos = storedPos.immutable();
        this.connectedInventories = new ArrayList<>();
        PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.lectern_chained", storedPos.getX(), storedPos.getY(), storedPos.getZ()));
        updateBlock();
    }

    @Override
    public List<ColorPos> getWandHighlight(List<ColorPos> list) {
        if (mainLecternPos != null) {
            list.add(ColorPos.centered(mainLecternPos, ParticleColor.TO_HIGHLIGHT));
            return list;
        }
        for (BlockPos pos : connectedInventories) {
            list.add(ColorPos.centered(pos, ParticleColor.FROM_HIGHLIGHT));
        }
        for (EntityBookwyrm bookwyrm : getBookwyrmEntities()) {
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
		if(checkPlayerRangeTicks > 0){
			checkPlayerRangeTicks--;
		}
		if(checkPlayerRangeTicks <= 0){
			// Turn off bookwyrm tasks if no player is nearby
			checkPlayerRangeTicks = 60 + level.random.nextInt(5);
			canCreateTasks = false;
			ServerLevel serverLevel = (ServerLevel) level;
			for(ServerPlayer serverPlayer : serverLevel.players()){
				if(BlockUtil.distanceFrom(serverPlayer.position(), this.getBlockPos()) < 40){
					canCreateTasks = true;
					break;
				}
			}
		}
		if(updateItems) {
			updateItems();
			updateItems = false;
		}
	}

    public void updateItems() {
        itemsByTab.clear();
        tabManagerMap.clear();
        this.handlerPosList = new ArrayList<>();
        Map<String, List<FilterableItemHandler>> mappedFilterables = new HashMap<>();
        itemsByTab.put(TAB_ALL, new HashMap<>());
        for (BlockPos pos : connectedInventories) {
            BlockEntity invTile = level.getBlockEntity(pos);
            if (invTile == null) {
                continue;
            }
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
            if (handler == null) {
                continue;
            }
            StorageItemHandler storageItemHandler = new StorageItemHandler(handler, InvUtil.filtersOnTile(invTile));
            mappedFilterables.computeIfAbsent(TAB_ALL, s -> new ArrayList<>()).add(storageItemHandler);
            handlerPosList.add(new HandlerPos(pos, handler));
            if (invTile instanceof Nameable nameable && nameable.hasCustomName()) {
                String tabName = nameable.getCustomName().getString();
                mappedFilterables.computeIfAbsent(tabName, s -> new ArrayList<>()).add(storageItemHandler);
            }
        }
        for (String tabName : mappedFilterables.keySet()) {
            itemsByTab.put(tabName, new HashMap<>());
            for (FilterableItemHandler filterableItemHandler : mappedFilterables.get(tabName)) {
                IItemHandler handler = filterableItemHandler.getHandler();
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack = handler.getStackInSlot(i);
                    if (stack.isEmpty())
                        continue;
                    StoredItemStack storedItemStack = new StoredItemStack(stack);
                    itemsByTab.get(tabName).merge(storedItemStack, storedItemStack.getQuantity(), Long::sum);
                }
            }
            tabManagerMap.put(tabName, new InventoryManager(mappedFilterables.get(tabName)));
        }
        itemCounts = new HashMap<>();
        Map<StoredItemStack, Long> allItems = itemsByTab.get(TAB_ALL);
        for (StoredItemStack stack : allItems.keySet()) {
            itemCounts.put(stack.getStack().getItem(), allItems.get(stack));
        }
    }

    public List<EntityBookwyrm> getBookwyrmEntities() {
        List<EntityBookwyrm> bookwyrmEntities = new ArrayList<>();
        List<UUID> staleUUIDs = new ArrayList<>();
        for (UUID uuid : bookwyrmUUIDs) {
            if (level instanceof ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(uuid);
                if (entity instanceof EntityBookwyrm bookwyrm) {
                    bookwyrmEntities.add(bookwyrm);
                } else {
                    staleUUIDs.add(uuid);
                }
            }
        }
        bookwyrmUUIDs.removeAll(staleUUIDs);
        return bookwyrmEntities;
    }

	public void insertNearbyItems(){
		// Get adjacent inventories
		StorageLecternTile mainLectern = getMainLectern();
		if(mainLectern == null)
			return;
		for(Direction dir : Direction.values()){
			BlockPos pos = this.worldPosition.relative(dir);
			if(level.getBlockState(pos).is(BlockTagProvider.AUTOPULL_DISABLED)){
				continue;
			}
			BlockEntity tile = this.level.getBlockEntity(pos);
			if(tile == null || mainLectern.connectedInventories.contains(pos))
				continue;
			IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
			if(handler == null)
				continue;
			for(int i = 0; i < handler.getSlots(); i++){
				ItemStack stack = handler.getStackInSlot(i);
				if(stack.isEmpty())
					continue;
				ItemStack extractedStack = handler.extractItem(i, stack.getMaxStackSize(), false);
				ItemStack remaining = mainLectern.pushStack(extractedStack, null);
				if(!remaining.isEmpty()){
					ItemStack remainder = handler.insertItem(i, remaining, false);
					if(!remainder.isEmpty()){
						Containers.dropItemStack(level, worldPosition.getX() + .5f, worldPosition.getY() + .5f, worldPosition.getZ() + .5f, remainder);
					}
				}
				return;
			}
		}
		backoffTicks = 100 + level.random.nextInt(20);
	}

    public void removeBookwyrm(EntityBookwyrm bookwyrm) {
        bookwyrmUUIDs.remove(bookwyrm.getUUID());
        updateBlock();
    }

    public boolean canInteractWith(Player player) {
        return !this.isRemoved();
    }

    public boolean openMenu(Player player) {
        StorageLecternTile mainLectern = getMainLectern();
        if (mainLectern == null)
            return false;
        player.openMenu(mainLectern);
        return true;
    }

    public @Nullable StorageLecternTile getMainLectern() {
        return getMainLectern(new ArrayList<>());
    }

    public @Nullable StorageLecternTile getMainLectern(List<BlockPos> visitedPos) {

        if (mainLecternPos == null)
            return this;
        if (visitedPos.contains(mainLecternPos))
            return null;
        visitedPos.add(mainLecternPos);
        if (level.isLoaded(mainLecternPos) &&
            level.getBlockEntity(mainLecternPos) instanceof StorageLecternTile storageTerminalBlockEntity) {
            return storageTerminalBlockEntity.getMainLectern(visitedPos);
        }
        return null;
    }

    public void setSorting(SortSettings sortSettings) {
        this.sortSettings = sortSettings;
        updateBlock();
    }

    public int getMaxConnectedInventories() {
        return getBookwyrmEntities().size() * Config.BOOKWYRM_LIMIT.get();
    }

    public @Nullable EntityBookwyrm addBookwyrm() {
        if (level.isClientSide)
            return null;
        EntityBookwyrm bookwyrm = new EntityBookwyrm(level, this.getBlockPos());
        bookwyrm.setPos(this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 1, this.getBlockPos().getZ() + 0.5);
        level.addFreshEntity(bookwyrm);
        bookwyrmUUIDs.add(bookwyrm.getUUID());
        updateBlock();
        return bookwyrm;
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        super.saveAdditional(compound, pRegistries);
        compound.put("settings", ANCodecs.encode(SortSettings.CODEC, sortSettings));
        ListTag list = new ListTag();
        for (BlockPos pos : connectedInventories) {
            CompoundTag c = new CompoundTag();
            c.putInt("x", pos.getX());
            c.putInt("y", pos.getY());
            c.putInt("z", pos.getZ());
            list.add(c);
        }
        compound.put("invs", list);
        if (mainLecternPos != null) {
            compound.putLong("mainLecternPos", mainLecternPos.asLong());
        }
        ListTag bookwyrmList = new ListTag();
        for (UUID uuid : bookwyrmUUIDs) {
            bookwyrmList.add(NbtUtils.createUUID(uuid));
        }
        compound.put("bookwyrmUUIDs", bookwyrmList);
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        super.loadAdditional(compound, pRegistries);
        if (compound.contains("settings")) {
            sortSettings = ANCodecs.decode(SortSettings.CODEC, compound.getCompound("settings"));
        }
        ListTag list = compound.getList("invs", 10);
        connectedInventories.clear();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag c = list.getCompound(i);
            connectedInventories.add(new BlockPos(c.getInt("x"), c.getInt("y"), c.getInt("z")));
        }
        if (compound.contains("mainLecternPos")) {
            mainLecternPos = BlockPos.of(compound.getLong("mainLecternPos"));
        }
        if (compound.contains("bookwyrmUUIDs")) {
            bookwyrmUUIDs.clear();
            ListTag bookwyrmList = compound.getList("bookwyrmUUIDs", 11);
            for (Tag tag : bookwyrmList) {
                bookwyrmUUIDs.add(NbtUtils.loadUUID(tag));
            }
        }
        updateItems = true;
    }

    public String getLastSearch() {
        return lastSearch;
    }

    public void setLastSearch(String string) {
        lastSearch = string;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (mainLecternPos != null) {
            tooltip.add(Component.translatable("ars_nouveau.storage.lectern_chained", mainLecternPos.getX(), mainLecternPos.getY(), mainLecternPos.getZ()));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.storage.num_connected", connectedInventories.size()));
            tooltip.add(Component.translatable("ars_nouveau.storage.num_bookwyrms", bookwyrmUUIDs.size()));
        }
    }

    @Override
    public @Nullable IItemHandler getCapability(CraftingLecternTile object, Direction context) {
        StorageLecternTile lecternTile = object.getMainLectern();
        if (lecternTile == null) {
            this.lecternInvWrapper = new LecternInvWrapper(this);
            return this.lecternInvWrapper;
        }
        List<IItemHandler> modifiables = new ArrayList<>();
        for (BlockPos pos : lecternTile.connectedInventories) {
            BlockEntity invTile = lecternTile.level.getBlockEntity(pos);
            if (invTile != null) {
                IItemHandler lih = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
                if (lih == null) {
                    continue;
                }
                modifiables.add(lih);
            }
        }
        lecternTile.lecternInvWrapper = new LecternInvWrapper(this, modifiables.toArray(new IItemHandler[0]));
        return lecternTile.lecternInvWrapper;
    }

    public record HandlerPos(BlockPos pos, IItemHandler handler) {
    }
}
