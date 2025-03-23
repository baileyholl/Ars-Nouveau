package com.hollingsworth.arsnouveau.common.block.tile.repository;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.inv.IMapInventory;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.RepositoryBlock;
import com.hollingsworth.arsnouveau.common.block.tile.ModdedTile;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.hollingsworth.arsnouveau.setup.registry.BlockEntityTypeRegistryWrapper;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.stream.Collectors;

public class RepositoryControllerTile extends ModdedTile implements ITooltipProvider, ITickable, Nameable, GeoBlockEntity {

    List<ConnectedRepository> connectedRepositories = new ArrayList<>();
    boolean invalidateNextTick;

    public RepositoryControllerTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.REPOSITORY_CONTROLLER_TILE, pos, state);
    }

    public RepositoryControllerTile(BlockEntityTypeRegistryWrapper<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public void invalidateNetwork(){
        connectedRepositories = new ArrayList<>();
        buildRepositoryNetwork(new HashSet<>(), worldPosition);
        System.out.println(connectedRepositories.size());
        invalidateCapabilities();
    }

    private void buildRepositoryNetwork(Set<BlockPos> visited, BlockPos nextPos) {
        if(!(level instanceof ServerLevel serverLevel)){
            return;
        }
        visited.add(nextPos);
        for(Direction direction : Direction.values()) {
            BlockPos pos = nextPos.relative(direction);
            if (!visited.contains(pos)) {
                visited.add(pos);
                if (!level.isLoaded(pos)) {
                    continue;
                }
                if(level.getBlockState(pos).getBlock() instanceof RepositoryBlock) {
                    var mapCap = BlockCapabilityCache.create(CapabilityRegistry.MAP_INV_CAP, serverLevel, pos, direction, () -> !this.isRemoved(), () -> this.invalidateNextTick = true);
                    if(mapCap.getCapability() != null) {
                        var invCap = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, serverLevel, pos, direction, () -> !this.isRemoved(), () -> this.invalidateNextTick = true);
                        connectedRepositories.add(new ConnectedRepository(pos, mapCap, invCap));
                    }
                    buildRepositoryNetwork(visited, pos);
                }
            }
        }
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if(hasCustomName()){
            tooltip.add(getCustomName());
        }
    }

    @Override
    public void tick() {
        if(invalidateNextTick){
            invalidateCapabilities();
            System.out.println("invalidating");
            invalidateNextTick = false;
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        invalidateNetwork();
    }

    public ControllerInv getControllerInv(){
        List<IItemHandler> handlers = new ArrayList<>();
        for(ConnectedRepository connectedRepository : this.connectedRepositories){
            IItemHandler handler = connectedRepository.itemHandler.getCapability();
            if(handler != null) {
                handlers.add(handler);
            }
        }
        return new ControllerInv(this, handlers.toArray(new IItemHandler[0]));
    }

    public Component name;
    @Override
    public Component getName() {
        return name;
    }

    public void setName(Component name) {
        this.name = name;
    }

    public List<ConnectedRepository> preferredForStack(ItemStack stack, boolean includeInvalid) {
        List<ConnectedRepository> filtered = new ArrayList<>();
        filtered = connectedRepositories.stream()
                .filter(filterableItemHandler ->{
                    IMapInventory mapInventory = filterableItemHandler.capability.getCapability();
                    if(mapInventory == null)
                        return false;
                    return includeInvalid || mapInventory.getInsertionPreference(stack) != ItemScroll.SortPref.INVALID;
                })
                .collect(Collectors.toCollection(ArrayList::new));
        /// Sort by highest pref first
        filtered.sort((o1, o2) -> o2.capability.getCapability().getInsertionPreference(stack).ordinal() - o1.capability.getCapability().getInsertionPreference(stack).ordinal());
        return filtered;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if(tag.contains("name")){
            this.name = Component.literal(tag.getString("name"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if(name != null){
            tag.putString("name", name.getString());
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }
    AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return instanceCache;
    }

    public static class ConnectedRepository{
        BlockPos pos;
        BlockCapabilityCache<IMapInventory, Direction> capability;
        BlockCapabilityCache<IItemHandler, Direction> itemHandler;

        public ConnectedRepository(BlockPos pos, BlockCapabilityCache<IMapInventory, Direction> capability, BlockCapabilityCache<IItemHandler, Direction> itemHandler) {
            this.pos = pos;
            this.capability = capability;
            this.itemHandler = itemHandler;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConnectedRepository that = (ConnectedRepository) o;
            return Objects.equals(pos, that.pos);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(pos);
        }
    }
}
