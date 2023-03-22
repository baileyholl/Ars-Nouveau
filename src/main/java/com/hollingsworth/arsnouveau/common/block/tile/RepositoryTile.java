package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class RepositoryTile extends RandomizableContainerBlockEntity implements IAnimatable {
    private NonNullList<ItemStack> items = NonNullList.withSize(54, ItemStack.EMPTY);


    public RepositoryTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.REPOSITORY_TILE, pos, state);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> pItemStacks) {
        items = pItemStacks;
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

    }

    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(pTag)) {
            ContainerHelper.loadAllItems(pTag, this.items);
        }
    }

    @Override
    public void registerControllers(AnimationData data) {

    }
    AnimationFactory factory = GeckoLibUtil.createFactory(this);
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
