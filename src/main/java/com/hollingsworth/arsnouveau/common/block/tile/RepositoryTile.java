package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.inv.FilterSet;
import com.hollingsworth.arsnouveau.api.item.inv.FilterableItemHandler;
import com.hollingsworth.arsnouveau.api.item.inv.IMapInventory;
import com.hollingsworth.arsnouveau.api.item.inv.SlotCache;
import com.hollingsworth.arsnouveau.common.block.tile.repository.RepositoryControllerTile;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class RepositoryTile extends RandomizableContainerBlockEntity implements GeoBlockEntity, ITooltipProvider, IMapInventory {
    public static String[][] CONFIGURATIONS = new String[][]{
            {"1","2_3","4_6","7_9","10_12","13_15","16_18","19_21","22_24", "25_27"},
            {"1","2_3","25_27","22_24","19_21","10_12","7_9","4_6","13_15","16_18"},
            {"10_12","13_15","7_9","16_18","4_6","19_21","2_3","22_24","1","25_27"},
            {"1","2_3","4_6","13_15","16_18","25_27","22_24","10_12","19_21","7_9"},
            {"1","25_27","2_3","22_24","4_6","19_21","7_9","16_18","10_12","13_15"},
            {"1","2_3","4_6", "10_12","25_27","22_24","19_21","13_15","7_9","16_18"}
    };


    private NonNullList<ItemStack> items = NonNullList.withSize(54, ItemStack.EMPTY);
    public int fillLevel;
    public int configuration;
    public SlotCache slotCache = new SlotCache(false);
    public FilterSet filterSet = new FilterSet();
    FilterableItemHandler filterableItemHandler;
    InvWrapper invWrapper = new InvWrapper(this);

    public void updateFill(){
        int i = 0;
        float f = 0.0F;

        for(int j = 0; j < getContainerSize(); ++j) {
            ItemStack itemstack = getItem(j);
            if (!itemstack.isEmpty()) {
                f += 1;
                ++i;
            }
        }

        f /= (float)getContainerSize();
        var oldFill = fillLevel;
        fillLevel = Mth.floor(f * 14.0F) + (i > 0 ? 1 : 0);
        if(oldFill != fillLevel)
            updateBlock();
    }

    public RepositoryTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.REPOSITORY_TILE.get(), pos, state);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> pItemStacks) {
        items = pItemStacks;
        initCache();
    }

    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        ItemStack oldItem = getItem(pIndex);
        super.setItem(pIndex, pStack);
        if(pStack.getItem() != oldItem.getItem()){
            slotCache.replaceSlotWithItem(oldItem.getItem(), pStack.getItem(), pIndex);
            System.out.println("replacing slots!");
            System.out.println(slotCache);
        }
        updateFill();
    }

    @Override
    public ItemStack removeItem(int pIndex, int pCount) {
        ItemStack extracted = super.removeItem(pIndex, pCount);
        Item newItem = getItem(pIndex).getItem();
        if(extracted.getItem() != newItem){
            slotCache.replaceSlotWithItem(extracted.getItem(), newItem, pIndex);
            System.out.println("replacing slots!");

        }
        updateFill();
        return extracted;
    }

    @Override
    public ItemStack removeItemNoUpdate(int pIndex) {
        ItemStack extracted = super.removeItemNoUpdate(pIndex);
        Item newItem = getItem(pIndex).getItem();
        if(extracted.getItem() != newItem){
            slotCache.replaceSlotWithItem(extracted.getItem(), newItem, pIndex);
            System.out.println("replacing slots!");
        }
        return extracted;
    }

    protected Component getDefaultName() {
        return Component.translatable("block.ars_nouveau.repository");
    }

    protected AbstractContainerMenu createMenu(int pId, Inventory pPlayer) {
        return ChestMenu.sixRows(pId, pPlayer, this);
    }

    @Override
    public int getContainerSize() {
        return 54;
    }

    public void invalidateNetwork(){
        if(level.isClientSide){
            return;
        }
        Set<BlockPos> visited = new HashSet<>();
        invalidateNetwork(visited);
    }

    protected void invalidateNetwork(Set<BlockPos> visited){
        visited.add(worldPosition);
        for(Direction direction : Direction.values()){
            BlockPos pos = worldPosition.relative(direction);
            if(!visited.contains(pos)){
                visited.add(pos);
                if(!level.isLoaded(pos)){
                    continue;
                }
                BlockEntity neighbor = level.getBlockEntity(pos);
                if(neighbor instanceof RepositoryTile repositoryTile){
                    repositoryTile.invalidateNetwork(visited);
                }else if(neighbor instanceof RepositoryControllerTile controllerTile){
                    controllerTile.invalidateNetwork();
                }
            }
        }
    }

    public void initCache(){
        if(!this.level.isClientSide){
            slotCache = new SlotCache(false);
            for(int i = 0; i < getContainerSize(); i++) {
                ItemStack stack = getItem(i);
                slotCache.getOrCreateSlots(stack.getItem()).add(i);
            }
            filterableItemHandler = new FilterableItemHandler(new InvWrapper(this), filterSet).withSlotCache(slotCache);
        }
    }

    public void attachFilters(){
        this.filterSet = FilterSet.forPosition(level, worldPosition);

        filterableItemHandler = new FilterableItemHandler(new InvWrapper(this), filterSet).withSlotCache(slotCache);
        System.out.println("attaching filters" + filterSet.filters.size());
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        if (!this.trySaveLootTable(pTag)) {
            ContainerHelper.saveAllItems(pTag, this.items, pRegistries);
        }
        pTag.putInt("fillLevel", fillLevel);
        pTag.putInt("configuration", configuration);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(pTag)) {
            ContainerHelper.loadAllItems(pTag, this.items, pRegistries);
        }
        fillLevel = pTag.getInt("fillLevel");
        configuration = pTag.getInt("configuration");
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
        handleUpdateTag(pkt.getTag() == null ? new CompoundTag() : pkt.getTag(), lookupProvider);
    }

    public boolean updateBlock() {
        if(level == null) {
            return false;
        }
        BlockState state = level.getBlockState(worldPosition);
        level.sendBlockUpdated(worldPosition, state, state, 3);
        setChanged();
        return true;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = super.getUpdateTag(pRegistries);
        this.saveAdditional(tag, pRegistries);
        return tag;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        initCache();
        // If item frames are in an adjacent chunk that is unloaded, scheduling one tick later has a chance to catch them
        // when the player joins the world.
        attachFilters();
        level.scheduleTick(worldPosition, BlockRegistry.REPOSITORY.get(), 1);
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {}

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if(hasCustomName()){
            tooltip.add(getCustomName());
        }
    }

    @Override
    public ItemStack insertStack(ItemStack stack, boolean simulate) {
        if(filterableItemHandler == null || !filterableItemHandler.canInsert(stack).valid()){
            return stack;
        }
        return filterableItemHandler.insertItemStacked(stack, simulate);
    }

    @Override
    public boolean hasExistingSlotsForInsertion(ItemStack stack) {
        return slotCache.getIfPresent(stack.getItem()) != null && !slotCache.getIfPresent(stack.getItem()).isEmpty();
    }

    @Override
    public ItemStack extractByItem(Item item, int count, boolean simulate, Predicate<ItemStack> filter) {
        Collection<Integer> slots = slotCache.getIfPresent(item);
        if(slots == null)
            return ItemStack.EMPTY;
        for(Integer slot : slots){
            ItemStack stack = getItem(slot);
            if(!filter.test(stack))
                continue;
            if(simulate) {
               return stack.copy();
            }else{
                return invWrapper.extractItem(slot, count, false);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemScroll.SortPref getInsertionPreference(ItemStack stack) {
        return filterSet.getHighestPreference(stack);
    }
}
