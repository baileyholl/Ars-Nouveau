package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.util.ColorPos;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDetectorTile extends ModdedTile implements ITickable, IWandable, ITooltipProvider {

    public BlockPos connectedPos;
    public boolean isPowered;
    public int neededCount;
    public ItemStack filterStack = ItemStack.EMPTY;


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
        IItemHandler handler = tile.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        if(handler == null){
            return;
        }
        int found = 0;
        for(int i = 0; i < handler.getSlots(); i++){
            ItemStack stack = handler.getStackInSlot(i);
            if (!ItemStack.isSame(stack, filterStack) || !ItemStack.tagMatches(stack, filterStack)) {
                continue;
            }
            found += stack.getCount();
            if(found > neededCount){
                if(!isPowered){
                    isPowered = true;
                    updateBlock();
                    level.updateNeighborsAt(worldPosition, BlockRegistry.ITEM_DETECTOR.get());
                }
                return;
            }
        }
        if(isPowered){
            isPowered = false;
            updateBlock();
            level.updateNeighborsAt(worldPosition, BlockRegistry.ITEM_DETECTOR.get());
        }
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
            connectedPos = storedPos.immutable();
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.item_detector.connected", storedPos.getX(), storedPos.getY(), storedPos.getZ()));
        }
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
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        tooltip.add(Component.translatable("ars_nouveau.item_detector.count", neededCount));
        tooltip.add(Component.translatable("ars_nouveau.item_detector.item", filterStack.getHoverName().getString()));
        tooltip.add(Component.translatable("ars_nouveau.item_detector.powered", isPowered));
    }
}
