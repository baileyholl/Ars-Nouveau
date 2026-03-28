package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.common.items.data.VoidJarData;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class VoidJar extends ModItem implements IScribeable {

    public VoidJar() {
        super(ItemsRegistry.defaultItemProperties().stacksTo(1).component(DataComponentRegistry.VOID_JAR, new VoidJarData()));
    }

    public void toggleStatus(Player playerEntity, ItemStack stack) {
        VoidJarData jarData = stack.getOrDefault(DataComponentRegistry.VOID_JAR, new VoidJarData());
        var newData = jarData.setActive(!jarData.active());
        stack.set(DataComponentRegistry.VOID_JAR, newData);
        if (newData.active()) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.on"));
        } else {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.off"));
        }
    }

    public static boolean tryVoiding(Player player, ItemStack pickingUp) {
        // Inventory.items is private in 1.21.11; iterate via getItem()
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack jar = player.getInventory().getItem(i);
            if (jar.getItem() instanceof VoidJar voidJar) {
                return voidJar.voidStack(player, jar, pickingUp);
            }
        }
        if (player.getOffhandItem().getItem() instanceof VoidJar voidJar) {
            return voidJar.voidStack(player, player.getOffhandItem(), pickingUp);
        }
        return false;
    }

    public boolean voidStack(Player player, ItemStack jarStack, ItemStack stackToVoid) {
        VoidJarData jarData = jarStack.getOrDefault(DataComponentRegistry.VOID_JAR, new VoidJarData());
        if (jarData.active() && jarData.scrollData().containsStack(stackToVoid)) {
            int amount = stackToVoid.getCount();
            preConsume(player, jarStack, stackToVoid, amount);
            stackToVoid.setCount(0);
            return true;
        }
        return false;
    }

    public void preConsume(Player player, ItemStack jar, ItemStack voided, int amount) {
        var mana = CapabilityRegistry.getMana(player);
        if (mana == null) {
            return;
        }
        mana.addMana(5.0 * amount);
    }

    @Override
    public @NotNull InteractionResult use(Level worldIn, @NotNull Player player, @NotNull InteractionHand handIn) {
        if (worldIn.isClientSide())
            return super.use(worldIn, player, handIn);
        ItemStack stack = player.getItemInHand(handIn);
        VoidJarData data = stack.getOrDefault(DataComponentRegistry.VOID_JAR, new VoidJarData());

        if (handIn == InteractionHand.MAIN_HAND) {
            ItemStack stackToWrite = player.getOffhandItem();
            if (player.isShiftKeyDown()) {
                toggleStatus(player, stack);
                return InteractionResult.CONSUME;
            }
            var items = data.scrollData().mutable();
            items.writeWithFeedback(player, stackToWrite);
            stack.set(DataComponentRegistry.VOID_JAR, new VoidJarData(items.toImmutable(), data.active()));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack thisStack) {
        VoidJarData data = thisStack.getOrDefault(DataComponentRegistry.VOID_JAR, new VoidJarData());
        var items = data.scrollData().mutable();
        var written = items.writeWithFeedback(player, player.getItemInHand(handIn));
        thisStack.set(DataComponentRegistry.VOID_JAR, new VoidJarData(items.toImmutable(), data.active()));
        return written;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, display, tooltip2, flagIn);
        stack.addToTooltip(DataComponentRegistry.VOID_JAR, context, display, tooltip2, flagIn);
    }
}
