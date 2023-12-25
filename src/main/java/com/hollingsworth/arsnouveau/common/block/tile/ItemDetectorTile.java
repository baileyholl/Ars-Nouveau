package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.items.ItemScroll;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDetectorTile extends ModdedTile implements ITickable, IWandable, ITooltipProvider {

    public BlockPos connectedPos;
    public boolean isPowered;
    public int neededCount;
    public ItemStack filterStack = ItemStack.EMPTY;
    public boolean inverted;


    public ItemDetectorTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public ItemDetectorTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ITEM_DETECTOR_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        if(level.isClientSide || connectedPos == null || level.getGameTime() % 20 != 0){
            return;
        }
        BlockEntity tile = level.getBlockEntity(connectedPos);
        if(tile == null){
            return;
        }
        IItemHandler handler = tile.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        if(handler == null){
            return;
        }
        int found = 0;
        for(int i = 0; i < handler.getSlots(); i++){
            ItemStack ghostStack = handler.getStackInSlot(i);
            ItemStack extractedStack = handler.extractItem(i, ghostStack.getCount(), true);
            // This falls back to the getStack count if we attempted to extract beyond the max count.
            // This is a workaround for inventories that support > max stack count, as you can only extract the max stack count at a time.
            // and getStackInSlot will return counts for items that wouldn't necessarily be extractable.
            if(ghostStack.getCount() > extractedStack.getCount() && extractedStack.getMaxStackSize() == extractedStack.getCount()){
                found += getCountForStack(ghostStack);
            }else{
                found += getCountForStack(extractedStack);
            }
            if(found > neededCount){
                setReachedCount(true);
                return;
            }
        }
        setReachedCount(false);
    }

    public int getCountForStack(ItemStack stack){
        if(filterStack.getItem() instanceof ItemScroll scroll){
            ItemScroll.SortPref pref = scroll.getSortPref(stack, filterStack, new CombinedInvWrapper());
            if(pref != ItemScroll.SortPref.INVALID){
                return stack.getCount();
            }
        }
        if (!ItemStack.isSameItemSameTags(stack, filterStack)) {
            return 0;
        }
        return (filterStack.isEmpty() && stack.isEmpty()) ? 1 : stack.getCount();
    }

    public void setReachedCount(boolean reachedCount){
        boolean old = isPowered;
        isPowered = reachedCount;
        if(old != isPowered){
            updateBlock();
            level.updateNeighborsAt(worldPosition, BlockRegistry.ITEM_DETECTOR.get());
        }
    }

    public boolean getPoweredState(){
        return inverted != isPowered;
    }

    public void addCount(int count){
        neededCount += count;
        if(neededCount < 0){
            neededCount = 0;
        }
        updateBlock();
    }

    public void setFilterStack(ItemStack stack){
        filterStack = stack.copy();
        updateBlock();
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if(storedPos != null){
            if(level.getBlockEntity(storedPos) == null || !level.getBlockEntity(storedPos).getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent()){
                return;
            }
            if(BlockUtil.distanceFrom(storedPos, worldPosition) > 30){
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.storage.inv_too_far"));
                return;
            }
            connectedPos = storedPos.immutable();
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.item_detector.connected", storedPos.getX(), storedPos.getY(), storedPos.getZ()));
            updateBlock();
        }
    }

    @Override
    public void onWanded(Player playerEntity) {
        inverted = !inverted;
        PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.item_detector.inverted", inverted));
        updateBlock();
        level.updateNeighborsAt(worldPosition, BlockRegistry.ITEM_DETECTOR.get());
    }

    @Override
    public List<ColorPos> getWandHighlight(List<ColorPos> list) {
        if(connectedPos != null){
            list.add(ColorPos.centered(connectedPos, ParticleColor.FROM_HIGHLIGHT));
        }
        return list;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if(connectedPos != null){
            tag.putLong("connectedPos", connectedPos.asLong());
        }
        tag.putInt("neededCount", neededCount);
        if(!filterStack.isEmpty()){
            tag.put("filterStack", filterStack.save(new CompoundTag()));
        }
        tag.putBoolean("isPowered", isPowered);
        tag.putBoolean("inverted", inverted);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if(pTag.contains("connectedPos")){
            connectedPos = BlockPos.of(pTag.getLong("connectedPos"));
        }
        this.neededCount = pTag.getInt("neededCount");
        if(pTag.contains("filterStack")){
            filterStack = ItemStack.of(pTag.getCompound("filterStack"));
        }
        isPowered = pTag.getBoolean("isPowered");
        inverted = pTag.getBoolean("inverted");
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("ars_nouveau.item_detector.count", (inverted ? "< " : "> ") + neededCount));
        if(filterStack.getItem() instanceof ItemScroll && filterStack.hasTag()){
            ItemScroll.ItemScrollData scrollData = new ItemScroll.ItemScrollData(filterStack);
            for (ItemStack s : scrollData.getItems()) {
                tooltip.add(Component.literal(s.getHoverName().getString()).withStyle(ChatFormatting.GOLD));
            }
        }else {
            tooltip.add(Component.translatable("ars_nouveau.item_detector.item", filterStack.getHoverName().getString()).withStyle(ChatFormatting.GOLD));
        }
        tooltip.add(Component.translatable("ars_nouveau.item_detector.powered", getPoweredState()));
    }
}
