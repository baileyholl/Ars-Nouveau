package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.hollingsworth.arsnouveau.common.items.data.ItemScrollData;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDetectorTile extends ModdedTile implements ITickable, IWandable, ITooltipProvider {

    public BlockPos connectedPos;
    public Direction direction = null;
    public boolean isPowered;
    public int neededCount;
    public ItemStack filterStack = ItemStack.EMPTY;
    public boolean inverted;
    public boolean shouldRecount;

    protected BlockCapabilityCache<IItemHandler, Direction> capabilityCache;


    public ItemDetectorTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public ItemDetectorTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ITEM_DETECTOR_TILE.get(), pos, state);
    }

    public void onConnectedInvalidated() {
        this.shouldRecount = true;
    }

    public IItemHandler getConnectedItemHandler() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        if (this.capabilityCache == null) {
            this.capabilityCache = BlockCapabilityCache.create(Capabilities.ItemHandler.BLOCK, serverLevel, connectedPos, direction, () -> !this.isRemoved(), this::onConnectedInvalidated);
        }

        return this.capabilityCache.getCapability();
    }

    @Override
    public void tick() {
        if (level.isClientSide || connectedPos == null || (!shouldRecount && level.getGameTime() % 20 != 0)) {
            return;
        }

        this.shouldRecount = false;

        int found = 0;

        IItemHandler handler = this.getConnectedItemHandler();
        if (handler != null) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack ghostStack = handler.getStackInSlot(i);
                ItemStack extractedStack = handler.extractItem(i, ghostStack.getCount(), true);
                // This falls back to the getStack count if we attempted to extract beyond the max count.
                // This is a workaround for inventories that support > max stack count, as you can only extract the max stack count at a time.
                // and getStackInSlot will return counts for items that wouldn't necessarily be extractable.
                if (ghostStack.getCount() > extractedStack.getCount() && extractedStack.getMaxStackSize() == extractedStack.getCount()) {
                    found += getCountForStack(ghostStack);
                } else {
                    found += getCountForStack(extractedStack);
                }
                if (found > neededCount) {
                    setReachedCount(true);
                    return;
                }
            }
        }

        setReachedCount(found > neededCount);
    }

    public int getCountForStack(ItemStack stack) {
        if (filterStack.getItem() instanceof ItemScroll scroll) {
            ItemScroll.SortPref pref = scroll.getSortPref(stack, filterStack, new CombinedInvWrapper());
            if (pref != ItemScroll.SortPref.INVALID) {
                return stack.getCount();
            }
        }
        if (!ItemStack.isSameItemSameComponents(stack, filterStack)) {
            return 0;
        }
        return (filterStack.isEmpty() && stack.isEmpty()) ? 1 : stack.getCount();
    }

    public void setReachedCount(boolean reachedCount) {
        boolean old = isPowered;
        isPowered = reachedCount;
        if (old != isPowered) {
            updateBlock();
            level.updateNeighborsAt(worldPosition, BlockRegistry.ITEM_DETECTOR.get());
        }
    }

    public boolean getPoweredState() {
        return inverted != isPowered;
    }

    public void addCount(int count) {
        neededCount += count;
        if (neededCount < 0) {
            neededCount = 0;
        }
        updateBlock();
    }

    public void setFilterStack(ItemStack stack) {
        filterStack = stack.copy();
        updateBlock();
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos != null) {
            if (level.getCapability(Capabilities.ItemHandler.BLOCK, storedPos, null) == null) {
                return;
            }
            if (BlockUtil.distanceFrom(storedPos, worldPosition) > 30) {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.inv_too_far"));
                return;
            }
            connectedPos = storedPos.immutable();
            direction = null;
            shouldRecount = true;
            capabilityCache = null;
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.item_detector.connected", storedPos.getX(), storedPos.getY(), storedPos.getZ()));
            updateBlock();
        }
    }

    @Override
    public Result onLastConnection(@Nullable GlobalPos storedPos, @Nullable Direction face, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (level == null || storedPos == null || !storedPos.dimension().equals(level.dimension())) {
            return Result.FAIL;
        }

        if (level.getCapability(Capabilities.ItemHandler.BLOCK, storedPos.pos(), face) == null) {
            return Result.FAIL;
        }

        var pos = storedPos.pos();
        if (BlockUtil.distanceFrom(pos, worldPosition) > 30) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.inv_too_far"));
            return Result.FAIL;
        }

        connectedPos = pos.immutable();
        direction = face;
        shouldRecount = true;
        capabilityCache = null;
        PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.item_detector.connected", pos.getX(), pos.getY(), pos.getZ()));
        updateBlock();

        return Result.SUCCESS;
    }

    @Override
    public void onWanded(Player playerEntity) {
        inverted = !inverted;
        shouldRecount = true;
        PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.item_detector.inverted", inverted));
        updateBlock();
        level.updateNeighborsAt(worldPosition, BlockRegistry.ITEM_DETECTOR.get());
    }

    @Override
    public List<ColorPos> getWandHighlight(List<ColorPos> list) {
        if (connectedPos != null) {
            list.add(ColorPos.centered(connectedPos, ParticleColor.FROM_HIGHLIGHT));
        }
        return list;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        if (connectedPos != null) {
            tag.putLong("connectedPos", connectedPos.asLong());
        }
        if (direction != null) {
            tag.putByte("direction", (byte) direction.get3DDataValue());
        }
        tag.putInt("neededCount", neededCount);
        if (!filterStack.isEmpty()) {
            tag.put("filterStack", filterStack.save(pRegistries));
        }
        tag.putBoolean("isPowered", isPowered);
        tag.putBoolean("inverted", inverted);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if (pTag.contains("connectedPos")) {
            connectedPos = BlockPos.of(pTag.getLong("connectedPos"));
        }
        if (pTag.contains("direction")) {
            direction = Direction.from3DDataValue(pTag.getByte("direction"));
        }
        this.neededCount = pTag.getInt("neededCount");
        filterStack = ItemStack.parseOptional(pRegistries, pTag.getCompound("filterStack"));

        isPowered = pTag.getBoolean("isPowered");
        inverted = pTag.getBoolean("inverted");
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("ars_nouveau.item_detector.count", (inverted ? "< " : "> ") + neededCount));
        if (filterStack.getItem() instanceof ItemScroll) {
            ItemScrollData scrollData = filterStack.getOrDefault(DataComponentRegistry.ITEM_SCROLL_DATA, new ItemScrollData(List.of()));
            for (ItemStack s : scrollData.getItems()) {
                tooltip.add(Component.literal(s.getHoverName().getString()).withStyle(ChatFormatting.GOLD));
            }
        } else {
            tooltip.add(Component.translatable("ars_nouveau.item_detector.item", filterStack.getHoverName().getString()).withStyle(ChatFormatting.GOLD));
        }
        tooltip.add(Component.translatable("ars_nouveau.item_detector.powered", getPoweredState()));
    }
}
