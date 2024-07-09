package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.common.items.data.ItemScrollData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;

public abstract class ItemScroll extends ModItem implements IScribeable {

    public ItemScroll() {
        super(ItemsRegistry.defaultItemProperties().component(DataComponentRegistry.ITEM_SCROLL_DATA, new ItemScrollData(List.of())));
    }

    public ItemScroll(Properties properties) {
        super(properties);
    }

    public abstract SortPref getSortPref(ItemStack stackToStore, ItemStack scrollStack, IItemHandler inventory);

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(pUsedHand == InteractionHand.MAIN_HAND && !pLevel.isClientSide){
            ItemStack thisStack = pPlayer.getItemInHand(pUsedHand);
            ItemStack otherStack = pPlayer.getItemInHand(InteractionHand.OFF_HAND);
            if(!otherStack.isEmpty()){
                onScribe(pLevel, pPlayer.blockPosition(), pPlayer, InteractionHand.OFF_HAND , thisStack);
                return InteractionResultHolder.success(thisStack);
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
    // TODO: Move this to API.
    public enum SortPref {
        INVALID,
        LOW,
        HIGH,
        HIGHEST
    }

    @Override
    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack thisStack) {
        ItemStack stackToWrite = player.getItemInHand(handIn);
        ItemScrollData existingList = thisStack.getOrDefault(DataComponentRegistry.ITEM_SCROLL_DATA, new ItemScrollData(List.of()));
        var mutable = existingList.mutable();
        var success = mutable.writeWithFeedback(player, stackToWrite);
        stackToWrite.set(DataComponentRegistry.ITEM_SCROLL_DATA, mutable.toImmutable());
        return success;
    }
}
