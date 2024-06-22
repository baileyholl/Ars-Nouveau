package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.List;

public class RepositoryTile extends RandomizableContainerBlockEntity implements GeoBlockEntity, ITooltipProvider {
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

    private ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        protected void onOpen(Level p_155062_, BlockPos p_155063_, BlockState p_155064_) {
        }

        protected void onClose(Level level, BlockPos p_155073_, BlockState p_155074_) {
            updateFill();
        }

        protected void openerCountChanged(Level p_155066_, BlockPos p_155067_, BlockState p_155068_, int p_155069_, int p_155070_) {
        }

        protected boolean isOwnContainer(Player p_155060_) {
            if (p_155060_.containerMenu instanceof ChestMenu) {
                Container container = ((ChestMenu)p_155060_.containerMenu).getContainer();
                return container == RepositoryTile.this;
            } else {
                return false;
            }
        }
    };

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
    }

    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        super.setItem(pIndex, pStack);
        updateFill();
    }

    @Override
    public ItemStack removeItem(int pIndex, int pCount) {
        ItemStack stack = super.removeItem(pIndex, pCount);
        updateFill();
        return stack;
    }

    @Override
    public void startOpen(Player pPlayer) {
        super.startOpen(pPlayer);
        openersCounter.incrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
    }

    @Override
    public void stopOpen(Player pPlayer) {
        super.stopOpen(pPlayer);
        openersCounter.decrementOpeners(pPlayer, this.getLevel(), this.getBlockPos(), this.getBlockState());
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


    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (!this.trySaveLootTable(pTag)) {
            ContainerHelper.saveAllItems(pTag, this.items);
        }
        pTag.putInt("fillLevel", fillLevel);
        pTag.putInt("configuration", configuration);
    }

    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(pTag)) {
            ContainerHelper.loadAllItems(pTag, this.items);
        }
        fillLevel = pTag.getInt("fillLevel");
        configuration = pTag.getInt("configuration");
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(pkt.getTag() == null ? new CompoundTag() : pkt.getTag());
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
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
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
}
