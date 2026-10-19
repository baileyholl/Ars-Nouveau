package com.hollingsworth.arsnouveau.common.block.tile;

import com.google.common.collect.EvictingQueue;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.item.inv.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import com.hollingsworth.arsnouveau.client.container.SortSettings;
import com.hollingsworth.arsnouveau.client.container.StoredItemStack;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
import com.hollingsworth.arsnouveau.common.entity.goal.bookwyrm.TransferTask;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.SetTerminalSettingsPacket;
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
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import net.neoforged.neoforge.items.wrapper.ForwardingItemHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.function.Function;

public class CraftingLecternTile extends ModdedTile implements MenuProvider, ITickable, IWandable, ITooltipProvider, ICapabilityProvider<CraftingLecternTile, Direction, IItemHandler>, GeoBlockEntity {
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
    private @Nullable BlockCapabilityCache<IItemHandler, Direction> mainLecternHandler;
    private final IItemHandler chainedHandler = new ForwardingItemHandler(() -> {
        CraftingLecternTile mainLectern = getMainLectern();
        if (mainLectern == null || mainLectern == this || isRemoved() || !(level instanceof ServerLevel serverLevel)) {
            return EmptyItemHandler.INSTANCE;
        }
        if (mainLecternHandler == null || !mainLecternHandler.pos().equals(mainLectern.getBlockPos())) {
            mainLecternHandler = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, serverLevel, mainLectern.getBlockPos(), null);
        }
        IItemHandler handler = mainLecternHandler.getCapability();
        return handler == null ? EmptyItemHandler.INSTANCE : handler;
    });
    public Queue<TransferTask> transferTasks = EvictingQueue.create(10);
    public boolean powered;
    private Function<UUID, AbstractContainerMenu> craftingContainer = (uuid) -> new AbstractContainerMenu(MenuType.CRAFTING, 0) {
        @Override
        public boolean stillValid(Player player) {
            return false;
        }

        @Override
        public void slotsChanged(Container inventory) {
            if (level != null && !level.isClientSide) {
                onCraftingMatrixChanged(uuid);
            }
        }

        @Override
        public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
            return ItemStack.EMPTY;
        }
    };
    public final Map<UUID, TransientCustomContainer> craftingMatrices = new HashMap<>();
    private final Map<UUID, ResultContainer> craftingResults = new HashMap<>();


    public CraftingLecternTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.CRAFTING_LECTERN_TILE.get(), pos, state);
    }

    public InventoryManager getInvManager(@Nullable String tab) {
        boolean isAnyTab = tab == null || tab.isEmpty();
        List<HandlerPos> handlers = tabManagerMap.getOrDefault(isAnyTab ? TAB_ALL : tab, List.of());

        List<FilterableItemHandler> itemHandlers = new ArrayList<>();
        for (HandlerPos handler : handlers) {
            if (!level.isLoaded(handler.pos)
                    || !isValidInv(handler.pos)
                    || handler.handler == null
                    || handler.handler.getCapability() == null) {
                continue;
            }

            if (isAnyTab || (level.getBlockEntity(handler.pos) instanceof Nameable nameable
                    && nameable.hasCustomName()
                    && nameable.getCustomName().getString().trim().equals(tab.trim()))) {
                itemHandlers.add(new FilterableItemHandler(handler.handler.getCapability(), FilterSet.forPosition(level, handler.pos)).withSlotCache(handler.slotCache));
            }
        }
        return new InventoryManager(itemHandlers);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("ars_nouveau.storage_lectern");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory plInv, Player arg2) {
        updateCraftResult(arg2.getUUID());
        return new CraftingTerminalMenu(id, plInv, this);
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
        MultiExtractedReference pulled = getInvManager(tabName).extractItemFromAll(stack.getStack(), max, true);
        if (pulled.getExtracted().isEmpty()) {
            return null;
        }
        if (canCreateTasks) {
            for (ExtractedStack extractedStack : pulled.getSlots()) {
                BlockPos pos = posFromSlotRef(extractedStack);
                if (pos != null) {
                    transferTasks.add(new TransferTask(pos, getBlockPos(), extractedStack.stack, level.getGameTime()));
                }
            }
        }
        return new StoredItemStack(pulled.getExtracted());
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

    public @Nullable TransferTask getTransferTask() {
        return transferTasks.poll();
    }


    public StoredItemStack pushStack(StoredItemStack stack, @Nullable String tab) {
        if (stack == null) {
            return null;
        }
        ItemStack copyStack = stack.getActualStack();
        MultiInsertReference reference = getInvManager(tab).insertStackWithReference(stack.getActualStack());
        if (canCreateTasks) {
            for (SlotReference slotReference : reference.getSlots()) {
                BlockPos pos = posFromSlotRef(slotReference);
                if (pos != null) {
                    transferTasks.add(new TransferTask(getBlockPos(), pos, copyStack, level.getGameTime()));
                }
            }
        }
        ItemStack remaining = reference.getRemainder();
        if (remaining.isEmpty()) {
            return null;
        }
        return new StoredItemStack(remaining);
    }

    public ItemStack pushStack(ItemStack itemstack, @Nullable String tab) {
        CraftingLecternTile mainLectern = getMainLectern();
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
        if (tile instanceof CraftingLecternTile) {
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
        if (BlockUtil.distanceFrom(level, storedPos, worldPosition) > ServerConfig.LECTERN_LINK_RANGE.get()) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.inv_too_far", ServerConfig.LECTERN_LINK_RANGE.get()));
            return;
        }

        if (handlerPosList.removeIf(handlerPos -> handlerPos.pos.equals(storedPos))) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.removed"));
            this.invalidateCapabilities();
        } else {
            if (!playerEntity.isCreative() && this.handlerPosList.size() >= getBookwyrmEntities().size() * Config.BOOKWYRM_LIMIT.get()) {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.too_many"));
                return;
            }
            this.addHandlerPos(storedPos);
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
        if (!(tile instanceof CraftingLecternTile)) {
            return;
        }
        if (BlockUtil.distanceFrom(level, storedPos, worldPosition) > ServerConfig.LECTERN_LINK_RANGE.get()) {
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
        if (!powered && backoffTicks <= 0 && level.getGameTime() % 20 == 0) {
            insertNearbyItems();
        }
        if (checkPlayerRangeTicks > 0) {
            checkPlayerRangeTicks--;
        }
        if (checkPlayerRangeTicks <= 0 && !bookwyrmUUIDs.isEmpty()) {
            // Turn off bookwyrm tasks if no player is nearby
            checkPlayerRangeTicks = 60 + level.random.nextInt(5);
            canCreateTasks = false;
            ServerLevel serverLevel = (ServerLevel) level;
            for (ServerPlayer serverPlayer : serverLevel.players()) {
                if (BlockUtil.distanceFrom(level, serverPlayer.position(), this.getBlockPos()) < 40) {
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
        Map<StoredItemStack, Long> allItems = new HashMap<>();
        itemsByTab.put(TAB_ALL, allItems);
        for (HandlerPos handlerPos : handlerPosList) {
            BlockPos pos = handlerPos.pos;
            if (!isValidInv(pos) || handlerPos.handler == null) {
                continue;
            }
            IItemHandler handler = handlerPos.handler.getCapability();
            if (handler == null) {
                continue;
            }
            Map<StoredItemStack, Long> tabItems = null;
            if (level.getBlockEntity(pos) instanceof Nameable nameable && nameable.hasCustomName()) {
                String tabName = nameable.getCustomName().getString();
                tabItems = itemsByTab.computeIfAbsent(tabName, (key) -> new HashMap<>());
                tabManagerMap.computeIfAbsent(tabName, (key) -> new ArrayList<>()).add(handlerPos);
            }
            tabManagerMap.computeIfAbsent(TAB_ALL, (key) -> new ArrayList<>()).add(handlerPos);

            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (stack.isEmpty())
                    continue;
                StoredItemStack storedItemStack = new StoredItemStack(stack);
                allItems.merge(storedItemStack, storedItemStack.getQuantity(), Long::sum);
                if (tabItems != null) {
                    tabItems.merge(storedItemStack, storedItemStack.getQuantity(), Long::sum);
                }
            }
        }

        itemCounts = new HashMap<>();
        allItems.forEach((stack, count) -> itemCounts.put(stack.getStack().getItem(), count));
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
        CraftingLecternTile mainLectern = getMainLectern();
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

    public boolean openMenu(Player player) {
        CraftingLecternTile mainLectern = getMainLectern();
        if (mainLectern == null)
            return false;
        player.openMenu(mainLectern);
        return true;
    }

    public @Nullable CraftingLecternTile getMainLectern() {
        return getMainLectern(new ArrayList<>());
    }

    public @Nullable CraftingLecternTile getMainLectern(List<BlockPos> visitedPos) {

        if (mainLecternPos == null)
            return this;
        if (visitedPos.contains(mainLecternPos))
            return null;
        visitedPos.add(mainLecternPos);
        if (level.isLoaded(mainLecternPos) &&
                level.getBlockEntity(mainLecternPos) instanceof CraftingLecternTile storageTerminalBlockEntity) {
            return storageTerminalBlockEntity.getMainLectern(visitedPos);
        }
        return null;
    }

    public void setSorting(SortSettings sortSettings) {
        this.sortSettings = sortSettings;
        updateBlock();
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
        return !(level.getBlockEntity(pos) instanceof CraftingLecternTile) && !level.getBlockState(pos).is(BlockTagProvider.LECTERN_BLACKLIST);
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
        compound.putBoolean("powered", powered);

        ListTag matrices = new ListTag();
        for (Map.Entry<UUID, TransientCustomContainer> entry : craftingMatrices.entrySet()) {
            TransientCustomContainer craftingMatrix = entry.getValue();
            ListTag table = new ListTag();
            for (int i = 0; i < craftingMatrix.getContainerSize(); ++i) {
                ItemStack itemstack = craftingMatrix.getItem(i);
                if (!itemstack.isEmpty()) {
                    CompoundTag itemSlot = new CompoundTag();
                    itemSlot.putInt("Slot", i);
                    itemSlot.put("item", itemstack.save(pRegistries));
                    table.add(itemSlot);
                }
            }
            CompoundTag matrix = new CompoundTag();

            matrix.putUUID("UUID", entry.getKey());
            matrix.put("CraftingTable", table);
            matrices.add(matrix);
        }

        compound.put("CraftingMatrices", matrices);
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
        powered = compound.getBoolean("powered");

        ListTag matrices = compound.getList("CraftingMatrices", Tag.TAG_COMPOUND);

        for (int i = 0; i < matrices.size(); i++) {
            CompoundTag entry = matrices.getCompound(i);
            UUID uuid = entry.getUUID("UUID");
            TransientCustomContainer container = getCraftingInv(uuid);

            ListTag table = entry.getList("CraftingTable", Tag.TAG_COMPOUND);
            for (int j = 0; j < table.size(); j++) {
                CompoundTag itemSlot = table.getCompound(j);
                int slot = itemSlot.getInt("Slot");
                if (slot >= 0 && slot < container.getContainerSize()) {
                    container.setItem(slot, ItemStack.parseOptional(pRegistries, itemSlot.getCompound("item")));
                }
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.initHandlerCache();
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
        CraftingLecternTile lecternTile = this.getMainLectern();
        List<IItemHandler> handlers = new ArrayList<>();
        if (lecternTile == null) {
            return handlers;
        }
        for (HandlerPos handlerPos : lecternTile.handlerPosList) {
            if (handlerPos.handler == null || level.getBlockEntity(handlerPos.pos) instanceof CraftingLecternTile)
                continue;
            IItemHandler handler = handlerPos.handler.getCapability();
            if (handler != null) {
                handlers.add(handler);
            }
        }
        return handlers;
    }

    public void addHandlerPos(BlockPos pos) {
        BlockCapabilityCache<IItemHandler, Direction> capabilityCache = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, (ServerLevel) level, pos, null, () -> !this.isRemoved(), () -> {
            this.invalidateNextTick = true;
        });
        if (capabilityCache.getCapability() != null) {
            handlerPosList.add(new HandlerPos(pos.immutable(), capabilityCache));
            invalidateCapabilities();
        }
    }

    // Initialized all existing handlers with their capabilities as they load in null before onLoad
    public void initHandlerCache() {
        if (level.isClientSide)
            return;
        for (HandlerPos handlerPos : handlerPosList) {
            BlockPos pos = handlerPos.pos;
            if (pos.equals(this.getBlockPos())) {
                continue;
            }

            handlerPos.handler = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, (ServerLevel) level, pos, null, () -> !this.isRemoved(), () -> {
                this.invalidateNextTick = true;
            });

        }
        this.invalidateCapabilities();
    }

    @Override
    public @Nullable IItemHandler getCapability(CraftingLecternTile object, Direction context) {
        if (mainLecternPos == null) {
            return new LecternInvWrapper(this, getConnectedHandlers().toArray(new IItemHandler[0]));
        }
        return chainedHandler;
    }

    public TransientCustomContainer getCraftingInv(Player player) {
        return getCraftingInv(player.getUUID());
    }

    public TransientCustomContainer getCraftingInv(UUID uuid) {
        return craftingMatrices.computeIfAbsent(uuid, (key) -> new TransientCustomContainer(craftingContainer.apply(uuid), 3, 3));
    }

    public ResultContainer getCraftResult(Player player) {
        return getCraftResult(player.getUUID());
    }

    public ResultContainer getCraftResult(UUID uuid) {
        return craftingResults.computeIfAbsent(uuid, (key) -> new ResultContainer());
    }

    public void craftShift(ServerPlayer player, @Nullable String tab) {
        ResultContainer craftResult = getCraftResult(player);
        TransientCustomContainer craftMatrix = getCraftingInv(player);

        List<ItemStack> craftedItemsList = new ArrayList<>();
        int amountCrafted = 0;
        ItemStack crafted = craftResult.getItem(0);
        craftResult.awardUsedRecipes(player, craftMatrix.getItems());
        do {
            craft(player, tab);
            craftedItemsList.add(crafted.copy());
            amountCrafted += crafted.getCount();
        } while (ItemStack.isSameItem(crafted, craftResult.getItem(0)) && (amountCrafted + crafted.getCount()) <= crafted.getMaxStackSize());

        for (ItemStack craftedItem : craftedItemsList) {
            player.getInventory().add(craftedItem);
            if (!craftedItem.isEmpty()) {
                ItemStack is = pushStack(craftedItem, tab);
                if (!is.isEmpty()) {
                    Containers.dropItemStack(level, player.getX(), player.getY(), player.getZ(), is);
                }
            }
        }

        crafted.onCraftedBy(player.level, player, amountCrafted);
        var copyStack = crafted.copy();
        copyStack.setCount(amountCrafted);
        EventHooks.firePlayerCraftingEvent(player, copyStack, craftMatrix);
    }

    public void craft(ServerPlayer thePlayer, @Nullable String tab) {
        TransientCustomContainer craftMatrix = getCraftingInv(thePlayer);
        CraftingInput.Positioned positioned = craftMatrix.asPositionedCraftInput();
        CraftingInput input = positioned.input();
        var holder = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level).orElse(null);
        List<ItemStack> remainingItems = holder == null ? List.of() : holder.value().getRemainingItems(input);
        boolean playerInvUpdate = false;
        for (int row = 0; row < input.height(); row++) {
            for (int col = 0; col < input.width(); col++) {
                int i = col + positioned.left() + (row + positioned.top()) * craftMatrix.getWidth();
                int remIndex = col + row * input.width();
                ItemStack currentStack = craftMatrix.getItem(i);
                ItemStack oldItem = currentStack.copy();
                ItemStack rem = remainingItems.size() > remIndex ? remainingItems.get(remIndex) : ItemStack.EMPTY;
                if (!currentStack.isEmpty()) {
                    craftMatrix.removeItemNoUpdate(i, 1);
                    currentStack = craftMatrix.getItem(i);
                }
                if (currentStack.isEmpty() && !oldItem.isEmpty()) {
                    StoredItemStack is = null;
                    int invSlot = thePlayer.getInventory().findSlotMatchingItem(oldItem);
                    if (invSlot != -1) {
                        is = new StoredItemStack(thePlayer.getInventory().removeItem(invSlot, 1), 1);
                        playerInvUpdate = true;
                    }

                    if (is == null) {
                        is = pullStack(new StoredItemStack(oldItem), 1, tab);
                    }

                    if (is != null) {
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
                if (rem.isEmpty() || thePlayer.getInventory().add(rem)) {
                    continue;
                }
                thePlayer.drop(rem, false);
            }
        }
        if (playerInvUpdate) {
            thePlayer.containerMenu.broadcastChanges();
        }
        onCraftingMatrixChanged(thePlayer.getUUID());
    }

    protected void onCraftingMatrixChanged(UUID uuid) {
        if (sortSettings.expanded()) {
            setSorting(sortSettings.setExpanded(false));
            Player player = level.getPlayerByUUID(uuid);
            if (player instanceof ServerPlayer serverPlayer) {
                Networking.sendToPlayerClient(new SetTerminalSettingsPacket(sortSettings, null), serverPlayer);
            }
        }
        updateCraftResult(uuid);
        setChanged();
    }

    private void updateCraftResult(UUID uuid) {
        ResultContainer craftResult = getCraftResult(uuid);
        CraftingInput input = getCraftingInv(uuid).asCraftInput();

        var holder = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level).orElse(null);
        craftResult.setRecipeUsed(holder);
        if (holder == null) {
            craftResult.setItem(0, ItemStack.EMPTY);
        } else {
            craftResult.setItem(0, holder.value().assemble(input, level.registryAccess()));
        }
    }

    public void clear(Player player, @Nullable String tab) {
        TransientCustomContainer craftMatrix = getCraftingInv(player);

        for (int i = 0; i < craftMatrix.getContainerSize(); i++) {
            ItemStack st = craftMatrix.removeItemNoUpdate(i);
            if (!st.isEmpty()) {
                pushOrDrop(st, tab);
            }
        }
        onCraftingMatrixChanged(player.getUUID());
    }

    public void transferToGrid(Player player, ItemStack[][] ingredients, @Nullable String tab) {
        clear(player, tab);
        TransientCustomContainer craftMatrix = getCraftingInv(player);
        for (int i = 0; i < 9; i++) {
            ItemStack[] ingredient = ingredients[i];
            if (ingredient == null) {
                continue;
            }

            ItemStack stack = ItemStack.EMPTY;
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
                if (br) {
                    break;
                }
            }

            if (stack.isEmpty()) {
                // sort ingredient by the amount of items in the inv map
                ingredient = Arrays.stream(ingredient).filter(Objects::nonNull).sorted(Comparator.comparingLong((ItemStack a) -> itemCounts.getOrDefault(a.getItem(), 0L)).reversed()).toArray(ItemStack[]::new);

                // Sort ingredient by the amount of items in this inventory
                for (ItemStack itemStack : ingredient) {
                    StoredItemStack pulled = pullStack(new StoredItemStack(itemStack), 1, tab);
                    if (pulled != null) {
                        stack = pulled.getActualStack();
                        break;
                    }
                }
            }

            if (!stack.isEmpty()) {
                craftMatrix.setItemNoUpdate(i, stack);
            }
        }
        onCraftingMatrixChanged(player.getUUID());
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

    public static class HandlerPos {

        public BlockPos pos;
        public BlockCapabilityCache<? extends IItemHandler, Direction> handler;
        public SlotCache slotCache;

        public HandlerPos(BlockPos pos, BlockCapabilityCache<? extends IItemHandler, Direction> handler) {
            this.pos = pos;
            this.handler = handler;
            this.slotCache = new SlotCache();
        }
    }
}
