package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.inv.FilterSet;
import com.hollingsworth.arsnouveau.api.item.inv.IFiltersetProvider;
import com.hollingsworth.arsnouveau.api.item.inv.IMapInventory;
import com.hollingsworth.arsnouveau.api.spell.IResolveListener;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.RepositoryBlock;
import com.hollingsworth.arsnouveau.common.block.RepositoryCatalog;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.hollingsworth.arsnouveau.common.items.data.ItemScrollData;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOneShotAnimation;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectName;
import com.hollingsworth.arsnouveau.setup.registry.BlockEntityTypeRegistryWrapper;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class RepositoryCatalogTile extends ModdedTile implements ITooltipProvider, ITickable, Nameable, GeoBlockEntity, IFiltersetProvider, IAnimationListener {

    public List<ConnectedRepository> connectedRepositories = new ArrayList<>();
    public ItemStack scrollStack = ItemStack.EMPTY;
    boolean invalidateNextTick;
    private int openDrawer = 0;
    private int drawerTicks = 0;

    public RepositoryCatalogTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.REPOSITORY_CONTROLLER_TILE, pos, state);
    }

    public RepositoryCatalogTile(BlockEntityTypeRegistryWrapper<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public ItemStack setNewScroll(ItemStack stack) {
        ItemStack oldStack = this.scrollStack.copy();
        this.scrollStack = stack.copy();
        updateBlock();
        return oldStack;
    }

    public FilterSet proxyFilters = new ProxyFilterSet(this);

    @Override
    public FilterSet getFilterSet() {
        if (scrollStack.isEmpty()) {
            return proxyFilters;
        }
        FilterSet.ListSet listSet = new FilterSet.ListSet();
        listSet.addFilterScroll(scrollStack, this.getControllerInv());
        return new FilterSet.Composite().withFilter(listSet).withFilter(proxyFilters);
    }

    public void invalidateNetwork() {
        connectedRepositories = new ArrayList<>();
        buildRepositoryNetwork(new HashSet<>(), worldPosition);
        invalidateCapabilities();
    }

    private void buildRepositoryNetwork(Set<BlockPos> visited, BlockPos nextPos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        visited.add(nextPos);
        for (Direction direction : Direction.values()) {
            BlockPos pos = nextPos.relative(direction);
            if (!visited.contains(pos)) {
                visited.add(pos);
                if (level.getBlockState(pos).getBlock() instanceof RepositoryBlock) {
                    var mapCap = BlockCapabilityCache.create(CapabilityRegistry.MAP_INV_CAP, serverLevel, pos, direction, () -> !this.isRemoved(), () -> this.invalidateNextTick = true);
                    var invCap = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, serverLevel, pos, direction, () -> !this.isRemoved(), () -> this.invalidateNextTick = true);
                    connectedRepositories.add(new ConnectedRepository(pos, mapCap, invCap));

                    buildRepositoryNetwork(visited, pos);
                }
            }
        }
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return name;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (hasCustomName()) {
            tooltip.add(getCustomName());
        }
        if (!scrollStack.isEmpty()) {
            tooltip.add(scrollStack.getHoverName().copy().withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
            ItemScrollData scrollData = scrollStack.get(DataComponentRegistry.ITEM_SCROLL_DATA);
            if (scrollData != null) {
                scrollData.addToTooltip(Item.TooltipContext.EMPTY, tooltip::add, TooltipFlag.NORMAL);
            }
        }
    }

    @Override
    public void tick() {
        if (invalidateNextTick) {
            invalidateNetwork();
            invalidateNextTick = false;
        }
        if (drawerTicks > 0) {
            drawerTicks--;
            if (drawerTicks == 38) {
                level.playSound(null, worldPosition, SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.5f, 1.6f + (float) ParticleUtil.inRange(-0.2, 0.2));
            }
            if (drawerTicks == 15) {
                level.playSound(null, worldPosition, SoundEvents.BARREL_CLOSE, SoundSource.BLOCKS, 0.5f, 1.6f + (float) ParticleUtil.inRange(-0.2, 0.2));
            }
            if (drawerTicks == 38 && !level.isClientSide) {
                Direction direction = level.getBlockState(worldPosition).getValue(RepositoryCatalog.FACING);
                for (Entity entity : level.getEntities(null, AABB.unitCubeFromLowerCorner(worldPosition.relative(direction).getBottomCenter()))) {
                    entity.push(direction.getStepX() * 0.5, 0.1, direction.getStepZ() * 0.5);
                    entity.hurtMarked = true;
                }
            }
        } else {
            openDrawer = 0;
        }
    }

    public void openRandomDrawer() {

        openDrawer = level.random.nextIntBetweenInclusive(1, 6);
        drawerTicks = 60;
        if (!level.isClientSide) {
            Networking.sendToNearbyClient(level, worldPosition, new PacketOneShotAnimation(worldPosition));
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        invalidateNextTick = true;
    }

    public ControllerInv getControllerInv() {
        List<IItemHandler> handlers = new ArrayList<>();
        for (ConnectedRepository connectedRepository : this.connectedRepositories) {
            IItemHandler handler = connectedRepository.itemHandler.getCapability();
            if (handler != null) {
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

    public IResolveListener getSpellListener() {
        return (world, shooter, result, spell, spellContext, resolveEffect, spellStats, spellResolver) -> {
            if (resolveEffect instanceof EffectName && spell.name() != null) {
                setName(Component.literal(spell.name()));
            }
        };
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("name")) {
            this.name = Component.literal(tag.getString("name"));
        }
        this.scrollStack = ItemStack.parseOptional(registries, tag.getCompound("scrollStack"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (name != null) {
            tag.putString("name", name.getString());
        }
        tag.put("scrollStack", scrollStack.saveOptional(registries));
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.name = componentInput.get(DataComponents.CUSTOM_NAME);
    }


    List<AnimationController> animControllers = new ArrayList<>();

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        for (int index = 1; index < 7; index++) {
            int finalIndex = index;
            AnimationController controller = new AnimationController<>(this, "drawer" + index, 20, event -> {
                if (openDrawer == finalIndex) {
                    event.getController().setAnimation(RawAnimation.begin().thenPlay("drawer" + finalIndex));
                    return PlayState.CONTINUE;
                } else {
                    event.getController().forceAnimationReset();
                    return PlayState.STOP;
                }
            });
            animControllers.add(controller);
            controllers.add(controller);
        }
    }

    AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return instanceCache;
    }

    @Override
    public void startAnimation(int arg) {
        for (var controller : animControllers) {
            controller.forceAnimationReset();
        }
        openRandomDrawer();
    }

    /**
     * Returns the highest preference from the connected inventories ignoring this controllers own scroll.
     */
    public static class ProxyFilterSet extends FilterSet {
        RepositoryCatalogTile tile;

        public ProxyFilterSet(RepositoryCatalogTile tile) {
            this.tile = tile;
        }

        @Override
        public ItemScroll.SortPref getHighestPreference(ItemStack stack) {
            var preferencedStacks = tile.getControllerInv().preferredForStack(stack, false);
            if (preferencedStacks.isEmpty()) {
                return ItemScroll.SortPref.INVALID;
            }
            return preferencedStacks.peek().sortPref();
        }
    }

    public static class ConnectedRepository {
        public BlockPos pos;
        public BlockCapabilityCache<IMapInventory, Direction> capability;
        public BlockCapabilityCache<IItemHandler, Direction> itemHandler;

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
