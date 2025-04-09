package com.hollingsworth.arsnouveau.common.block.tile;

import com.google.common.collect.EvictingQueue;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.item.inv.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
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
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
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
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class StorageLecternTile extends ModdedTile implements MenuProvider, ITickable, IWandable, ITooltipProvider, ICapabilityProvider<CraftingLecternTile, Direction, IItemHandler> {
    public Map<String, List<HandlerPos>> tabManagerMap = new HashMap<>();
    public Map<String, Map<StoredItemStack, Long>> itemsByTab = new HashMap<>();
    public Map<Item, Long> itemCounts = new HashMap<>();
    public Map<UUID, String> searches = new HashMap<>();
    public boolean updateItems;
    public List<HandlerPos> handlerPosList = new ArrayList<>();

    public SortSettings sortSettings = new SortSettings();
    public BlockPos mainLecternPos;
    public List<UUID> bookwyrmUUIDs = new ArrayList<>();
    public int backoffTicks;
    public int checkPlayerRangeTicks;
    public boolean canCreateTasks = false;
    public static final String TAB_ALL = "8f6fe318-4ca6-4b29-ab63-15ec5289f5c9";
    public boolean invalidateNextTick;
    public Queue<TransferTask> transferTasks = EvictingQueue.create(10);
    IItemHandler lecternInvWrapper;


    public StorageLecternTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.CRAFTING_LECTERN_TILE.get(), pos, state);
    }

    public StorageLecternTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public InventoryManager getInvManager(@Nullable String tab) {
        List<HandlerPos> handlers;
        if (tab == null || tab.isEmpty()) {
            handlers = tabManagerMap.getOrDefault(TAB_ALL, new ArrayList<>());
        } else {
            handlers = tabManagerMap.getOrDefault(tab, new ArrayList<>());
        }

        List<FilterableItemHandler> itemHandlers = new ArrayList<>();
        for (HandlerPos handler : handlers) {
            if (!level.isLoaded(handler.pos)
                    || !isValidInv(handler.pos)
                    || handler.handler == null
                    || handler.handler.getCapability() == null) {
                continue;
            }
            boolean isAnyTab = tab == null || tab.isEmpty();

            if (isAnyTab || (level.getBlockEntity(handler.pos) instanceof Nameable nameable
                    && nameable.hasCustomName()
                    && nameable.getCustomName().getString().trim().equals(tab.trim()))) {
                itemHandlers.add(new FilterableItemHandler(handler.handler.getCapability(), FilterSet.forPosition(level, handler.pos)).withSlotCache(handler.slotCache));
            }
        }
        return new InventoryManager(itemHandlers);
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
        ArrayList<String> tabNames = new ArrayList<>();
        for (HandlerPos handlerPos : this.handlerPosList) {
            BlockPos pos = handlerPos.pos;
            var capCache = handlerPos.handler;
            BlockEntity tile = level.getBlockEntity(pos);
            if (capCache != null && capCache.getCapability() != null
                    && tile instanceof Nameable provider && provider.hasCustomName()) {

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
        if (multiSlotReference.getExtracted().isEmpty() || !canCreateTasks) {
            return;
        }

        for (ExtractedStack extractedStack : multiSlotReference.getSlots()) {
            BlockPos pos = posFromSlotRef(extractedStack);
            if (pos != null) {
                addTransferTask(new TransferTask(pos, getBlockPos(), extractedStack.stack, level.getGameTime()));
            }
        }
    }

    private void addInsertTasks(ItemStack stack, MultiInsertReference reference) {
        if (reference.isEmpty() || stack.isEmpty() || !canCreateTasks) {
            return;
        }
        for (SlotReference extractedStack : reference.getSlots()) {
            BlockPos pos = posFromSlotRef(extractedStack);
            if (pos != null) {
                addTransferTask(new TransferTask(getBlockPos(), pos, stack, level.getGameTime()));
            }
        }
    }

    private BlockPos posFromSlotRef(SlotReference extractedStack) {
        for (HandlerPos handlerPos : handlerPosList) {
            if (handlerPos.handler == null || handlerPos.handler.getCapability() == null)
                continue;
            if (handlerPos.handler.getCapability().equals(extractedStack.getHandler())) {
                if (level.getBlockEntity(handlerPos.pos) instanceof RepositoryCatalogTile controllerTile) {
                    if (controllerTile.connectedRepositories.isEmpty()) {
                        return null;
                    } else if (level.random.nextFloat() > 0.9) {
                        return controllerTile.connectedRepositories.get(level.random.nextInt(controllerTile.connectedRepositories.size())).pos;
                    }
                }
                return handlerPos.pos;
            }
        }
        return null;
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
        BlockEntity tile = level.getBlockEntity(storedPos);
        if (tile instanceof StorageLecternTile newMasterLectern) {
            return;
        }
        if (!isValidInv(storedPos)) {
            playerEntity.sendSystemMessage(Component.translatable("ars_nouveau.lectern_blacklist"));
            return;
        }

        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, storedPos, side);
        if (handler == null) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.no_tile"));
            return;
        }
        if (BlockUtil.distanceFrom(storedPos, worldPosition) > ServerConfig.LECTERN_LINK_RANGE.get()) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.inv_too_far", ServerConfig.LECTERN_LINK_RANGE.get()));
            return;
        }
        if (this.getBlockPos().equals(storedPos)) {
            return;
        }

        if (this.handlerPosList.stream().anyMatch(handlerPos -> handlerPos.pos.equals(storedPos))) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.removed"));
            handlerPosList.removeIf(handlerPos -> handlerPos.pos().equals(storedPos));
            this.invalidateCapabilities();
        } else {
            if (this.handlerPosList.size() >= this.getMaxConnectedInventories()) {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.too_many"));
                return;
            }
            this.addHandlerPos(this, storedPos);
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
        if (BlockUtil.distanceFrom(storedPos, worldPosition) > ServerConfig.LECTERN_LINK_RANGE.get()) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.lectern_too_far", ServerConfig.LECTERN_LINK_RANGE.get()));
            return;
        }
        this.mainLecternPos = storedPos.immutable();
        this.handlerPosList = new ArrayList<>();
        this.invalidateCapabilities();
        PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.lectern_chained", storedPos.getX(), storedPos.getY(), storedPos.getZ()));
        updateBlock();
    }

    @Override
    public List<ColorPos> getWandHighlight(List<ColorPos> list) {
        if (mainLecternPos != null) {
            list.add(ColorPos.centered(mainLecternPos, ParticleColor.TO_HIGHLIGHT));
            return list;
        }
        for (HandlerPos pos : handlerPosList) {
            list.add(ColorPos.centered(pos.pos, ParticleColor.FROM_HIGHLIGHT));
        }
        for (EntityBookwyrm bookwyrm : getBookwyrmEntities()) {
            list.add(ColorPos.centered(bookwyrm.blockPosition(), ParticleColor.GREEN));
        }
        return list;
    }

    @Override
    public void tick() {
        if (level.isClientSide)
            return;
        if (backoffTicks > 0) {
            backoffTicks--;
        }
        if (backoffTicks <= 0 && level.getGameTime() % 20 == 0) {
            insertNearbyItems();
        }
        if (checkPlayerRangeTicks > 0) {
            checkPlayerRangeTicks--;
        }
        if (checkPlayerRangeTicks <= 0) {
            // Turn off bookwyrm tasks if no player is nearby
            checkPlayerRangeTicks = 60 + level.random.nextInt(5);
            canCreateTasks = false;
            ServerLevel serverLevel = (ServerLevel) level;
            for (ServerPlayer serverPlayer : serverLevel.players()) {
                if (BlockUtil.distanceFrom(serverPlayer.position(), this.getBlockPos()) < 40) {
                    canCreateTasks = true;
                    break;
                }
            }
        }

        if (invalidateNextTick) {
            invalidateCapabilities();
            invalidateNextTick = false;
        }

        if (updateItems) {
            updateItems();
            updateItems = false;
        }
    }

    public void updateItems() {
        itemsByTab.clear();
        tabManagerMap.clear();
        Map<String, List<FilterableItemHandler>> mappedFilterables = new HashMap<>();
        itemsByTab.put(TAB_ALL, new HashMap<>());
        for (HandlerPos handlerPos : handlerPosList) {
            BlockPos pos = handlerPos.pos;
            if (!isValidInv(pos) || handlerPos.handler == null) {
                continue;
            }
            IItemHandler handler = handlerPos.handler.getCapability();
            if (handler == null) {
                continue;
            }
            StorageItemHandler storageItemHandler = new StorageItemHandler(handler, FilterSet.forPosition(level, pos), handlerPos.slotCache);
            mappedFilterables.computeIfAbsent(TAB_ALL, s -> new ArrayList<>()).add(storageItemHandler);
            if (level.getBlockEntity(pos) instanceof Nameable nameable && nameable.hasCustomName()) {
                String tabName = nameable.getCustomName().getString();
                mappedFilterables.computeIfAbsent(tabName, s -> new ArrayList<>()).add(storageItemHandler);
                tabManagerMap.computeIfAbsent(tabName, (key) -> new ArrayList<>()).add(handlerPos);
            }
            tabManagerMap.computeIfAbsent(TAB_ALL, (key) -> new ArrayList<>()).add(handlerPos);
        }

        for (String tabName : mappedFilterables.keySet()) {
            itemsByTab.computeIfAbsent(tabName, (key) -> new HashMap<>()).clear();
            for (FilterableItemHandler filterableItemHandler : mappedFilterables.get(tabName)) {
                IItemHandler handler = filterableItemHandler.getHandler();
                if(handler == null)
                    continue;
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack = handler.getStackInSlot(i);
                    if (stack.isEmpty())
                        continue;
                    StoredItemStack storedItemStack = new StoredItemStack(stack);
                    itemsByTab.get(tabName).merge(storedItemStack, storedItemStack.getQuantity(), Long::sum);
                }
            }
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

    public void insertNearbyItems() {
        // Get adjacent inventories
        StorageLecternTile mainLectern = getMainLectern();
        if (mainLectern == null)
            return;
        for (Direction dir : Direction.values()) {
            BlockPos pos = this.worldPosition.relative(dir);
            if (level.getBlockState(pos).is(BlockTagProvider.AUTOPULL_DISABLED) || !isValidInv(pos)) {
                continue;
            }

            if (mainLectern.handlerPosList.stream().anyMatch(p -> p.pos.equals(pos)))
                continue;
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
            if (handler == null || (level.getBlockEntity(pos) instanceof HopperBlockEntity hopperBlockEntity && hopperBlockEntity.getBlockPos().equals(worldPosition.below())))
                continue;
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (stack.isEmpty())
                    continue;
                ItemStack extractedStack = handler.extractItem(i, stack.getMaxStackSize(), false);
                ItemStack remaining = mainLectern.pushStack(extractedStack, null);
                if (!remaining.isEmpty()) {
                    ItemStack remainder = handler.insertItem(i, remaining, false);
                    if (!remainder.isEmpty()) {
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

    public boolean isValidInv(BlockPos pos) {
        return !(level.getBlockEntity(pos) instanceof StorageLecternTile) && !level.getBlockState(pos).is(BlockTagProvider.LECTERN_BLACKLIST);
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        super.saveAdditional(compound, pRegistries);
        compound.put("settings", ANCodecs.encode(SortSettings.CODEC, sortSettings));
        ListTag list = new ListTag();
        for (HandlerPos handlerPos : handlerPosList) {
            BlockPos pos = handlerPos.pos;
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
        handlerPosList.clear();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag c = list.getCompound(i);
            handlerPosList.add(new HandlerPos(new BlockPos(c.getInt("x"), c.getInt("y"), c.getInt("z")), null));
        }
        mainLecternPos = null;
        if (compound.contains("mainLecternPos")) {
            mainLecternPos = BlockPos.of(compound.getLong("mainLecternPos"));
        }
        bookwyrmUUIDs.clear();
        if (compound.contains("bookwyrmUUIDs")) {
            ListTag bookwyrmList = compound.getList("bookwyrmUUIDs", 11);
            for (Tag tag : bookwyrmList) {
                bookwyrmUUIDs.add(NbtUtils.loadUUID(tag));
            }
        }
        updateItems = true;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.initHandlerCache();
    }

    public String getLastSearch(Player player) {
        return searches.getOrDefault(player.getUUID(), "");
    }

    public void setLastSearch(Player sender, String string) {
        searches.put(sender.getUUID(), string);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (mainLecternPos != null) {
            tooltip.add(Component.translatable("ars_nouveau.storage.lectern_chained", mainLecternPos.getX(), mainLecternPos.getY(), mainLecternPos.getZ()));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.storage.num_connected", handlerPosList.size()));
            tooltip.add(Component.translatable("ars_nouveau.storage.num_bookwyrms", bookwyrmUUIDs.size()));
        }
    }

    public List<IItemHandler> getConnectedHandlers() {
        StorageLecternTile lecternTile = this.getMainLectern();
        List<IItemHandler> handlers = new ArrayList<>();
        if (lecternTile == null) {
            return handlers;
        }
        for (HandlerPos handlerPos : lecternTile.handlerPosList) {
            if (handlerPos.handler == null || level.getBlockEntity(handlerPos.pos) instanceof StorageLecternTile)
                continue;
            IItemHandler handler = handlerPos.handler.getCapability();
            if (handler != null) {
                handlers.add(handler);
            }
        }
        return handlers;
    }

    public void addHandlerPos(StorageLecternTile tile, BlockPos pos) {
        BlockCapabilityCache<IItemHandler, Direction> capabilityCache = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, (ServerLevel) level, pos, null, () -> !tile.isRemoved(), () -> {
            this.invalidateNextTick = true;
        });
        if (capabilityCache.getCapability() != null) {
            tile.handlerPosList.add(new HandlerPos(pos.immutable(), capabilityCache));
            tile.invalidateCapabilities();
        }
    }

    // Initialized all existing handlers with their capabilities as they load in null before onLoad
    public void initHandlerCache() {
        if (level.isClientSide)
            return;
        StorageLecternTile lecternTile = this.getMainLectern();
        if (lecternTile == null) {
            return;
        }
        for (HandlerPos handlerPos : lecternTile.handlerPosList) {
            BlockPos pos = handlerPos.pos;
            if (pos.equals(this.getBlockPos())) {
                continue;
            }

            handlerPos.handler = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, (ServerLevel) level, pos, null, () -> !lecternTile.isRemoved(), () -> {
                this.invalidateNextTick = true;
            });

        }
        this.invalidateCapabilities();
    }

    @Override
    public @Nullable IItemHandler getCapability(CraftingLecternTile object, Direction context) {
        StorageLecternTile lecternTile = object.getMainLectern();
        if (lecternTile == null) {
            this.lecternInvWrapper = new LecternInvWrapper(this);
            return this.lecternInvWrapper;
        }
        lecternTile.lecternInvWrapper = new LecternInvWrapper(this, this.getConnectedHandlers().toArray(new IItemHandler[0]));
        return lecternTile.lecternInvWrapper;
    }

    public static class HandlerPos {

        public BlockPos pos;
        public BlockCapabilityCache<? extends IItemHandler, Direction> handler;
        public SlotCache slotCache;

        public HandlerPos(BlockPos pos, BlockCapabilityCache<? extends IItemHandler, Direction> handler) {
            this.pos = pos;
            this.handler = handler;
            this.slotCache = new SlotCache();
        }

        public BlockPos pos() {
            return pos;
        }

        public BlockCapabilityCache<? extends IItemHandler, Direction> handler() {
            return handler;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof HandlerPos handlerPos) {
                return this.pos.equals(handlerPos.pos);
            }
            return false;
        }
    }
}
