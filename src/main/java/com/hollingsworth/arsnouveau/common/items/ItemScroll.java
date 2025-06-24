package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.client.jei.AliasProvider;
import com.hollingsworth.arsnouveau.common.items.data.ItemScrollData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public abstract class ItemScroll extends ModItem implements IScribeable, AliasProvider {

    public ItemScroll() {
        super(ItemsRegistry.defaultItemProperties().component(DataComponentRegistry.ITEM_SCROLL_DATA, new ItemScrollData(List.of())));
    }

    public ItemScroll(Properties properties) {
        super(properties);
    }

    public abstract SortPref getSortPref(ItemStack stackToStore, ItemStack scrollStack, IItemHandler inventory);

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (pUsedHand == InteractionHand.MAIN_HAND && !pLevel.isClientSide) {
            ItemStack thisStack = pPlayer.getItemInHand(pUsedHand);
            ItemStack otherStack = pPlayer.getItemInHand(InteractionHand.OFF_HAND);
            if (!otherStack.isEmpty()) {
                onScribe(pLevel, pPlayer.blockPosition(), pPlayer, InteractionHand.OFF_HAND, thisStack);
                return InteractionResultHolder.success(thisStack);
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    // TODO 1.22: Move this to API.
    public enum SortPref {
        INVALID,
        LOW,
        HIGH,
        HIGHEST
    }

    public static Comparator<SortPref> sortPrefComparator = Comparator.comparingInt(Enum::ordinal);

    @Override
    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack thisStack) {
        ItemStack stackToWrite = player.getItemInHand(handIn);
        ItemScrollData existingList = thisStack.getOrDefault(DataComponentRegistry.ITEM_SCROLL_DATA, new ItemScrollData(List.of()));
        var mutable = existingList.mutable();
        var success = mutable.writeWithFeedback(player, stackToWrite);
        thisStack.set(DataComponentRegistry.ITEM_SCROLL_DATA, mutable.toImmutable());
        return success;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        stack.addToTooltip(DataComponentRegistry.ITEM_SCROLL_DATA, context, tooltip2::add, flagIn);
    }

    @Override
    public Collection<Alias> getAliases() {
        return List.of(
                new Alias("filter", "Filter")
        );
    }
}
