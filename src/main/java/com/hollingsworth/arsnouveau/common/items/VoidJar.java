package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.common.items.ItemScroll.ITEM_PREFIX;
import static com.hollingsworth.arsnouveau.common.items.ItemScroll.containsItem;

public class VoidJar extends ModItem implements IScribeable {

    public VoidJar() {
        super(ItemsRegistry.defaultItemProperties().stacksTo(1));
    }

    public void toggleStatus(Player playerEntity, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag.getBoolean("on")) {
            tag.putBoolean("on", false);
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.off"));
        } else {
            tag.putBoolean("on", true);
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.on"));
        }
    }

    public static boolean tryVoiding(Player player, ItemStack pickingUp) {
        NonNullList<ItemStack> list = player.inventory.items;
        boolean voided = false;
        for (int i = 0; i < 9; i++) {
            ItemStack jar = list.get(i);
            if (jar.getItem() == ItemsRegistry.VOID_JAR.get()) {
                if (isActive(jar) && containsItem(pickingUp, jar.getTag())) {
                    CapabilityRegistry.getMana(player).ifPresent(iMana -> iMana.addMana(5.0 * pickingUp.getCount()));
                    pickingUp.setCount(0);
                    voided = true;
                    break;
                }
            }
        }
        return voided;
    }

    public static boolean isActive(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean("on");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player player, InteractionHand handIn) {
        if (worldIn.isClientSide)
            return super.use(worldIn, player, handIn);
        ItemStack stack = player.getItemInHand(handIn);
        CompoundTag tag = stack.getOrCreateTag();

        if (handIn == InteractionHand.MAIN_HAND) {
            ItemStack stackToWrite = player.getOffhandItem();
            if (player.isShiftKeyDown()) {
                toggleStatus(player, stack);
                return InteractionResultHolder.consume(stack);
            }

            if (!stackToWrite.isEmpty()) {
                if (containsItem(stackToWrite, tag)) {
                    PortUtil.sendMessage(player, Component.translatable("ars_nouveau.scribe.item_removed"));
                    ItemScroll.removeItem(stackToWrite, tag);
                    player.startUsingItem(handIn);
                    return InteractionResultHolder.fail(stack);
                }
                PortUtil.sendMessage(player, Component.translatable("ars_nouveau.scribe.item_added"));
                ItemScroll.addItem(stackToWrite, tag);
                player.startUsingItem(handIn);
                return InteractionResultHolder.fail(stack);
            }

        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        CompoundTag tag = stack.getTag();
        if (tag == null)
            return;

        if (tag.getBoolean("on")) {
            tooltip2.add(Component.translatable("ars_nouveau.on"));
        } else {
            tooltip2.add(Component.translatable("ars_nouveau.off"));
        }


        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
        List<ItemStack> stacks = new ArrayList<>();
        for (String s : tag.getAllKeys()) {
            if (s.contains(ITEM_PREFIX)) {
                stacks.add(ItemStack.of(tag.getCompound(s)));
            }
        }
        for (ItemStack s : stacks) {
            tooltip2.add(s.getHoverName());
        }
    }

    @Override
    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack thisStack) {
        return ItemScroll.scribe(world, pos, player, handIn, thisStack);
    }
}
