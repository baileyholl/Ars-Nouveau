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
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VoidJar extends ModItem implements IScribeable {

    public VoidJar() {
        super(ItemsRegistry.defaultItemProperties().stacksTo(1).component(DataComponentRegistry.VOID_JAR, new VoidJarData()));
    }

    public void toggleStatus(Player playerEntity, ItemStack stack) {
        VoidJarData jarData = stack.getOrDefault(DataComponentRegistry.VOID_JAR, new VoidJarData());
        var newData = stack.set(DataComponentRegistry.VOID_JAR, jarData.setActive(!jarData.active()));
        if (newData.active()) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.on"));
        } else {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.off"));
        }
    }

    public static boolean tryVoiding(Player player, ItemStack pickingUp) {
        NonNullList<ItemStack> list = player.inventory.items;
        for (ItemStack jar : list) {
            if (jar.getItem() instanceof VoidJar voidJar) {
                return voidJar.voidStack(player, jar, pickingUp);
            }
        }
        if (player.getOffhandItem().getItem() instanceof  VoidJar voidJar) {
            return voidJar.voidStack(player, player.getOffhandItem(), pickingUp);
        }
        return false;
    }

    public boolean voidStack(Player player, ItemStack jarStack, ItemStack stackToVoid){
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
        var mana =  CapabilityRegistry.getMana(player).orElse(null);
        if(mana == null){
            return;
        }
        mana.removeMana(5.0 * amount);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player player, InteractionHand handIn) {
        if (worldIn.isClientSide)
            return super.use(worldIn, player, handIn);
        ItemStack stack = player.getItemInHand(handIn);
        VoidJarData data = stack.getOrDefault(DataComponentRegistry.VOID_JAR, new VoidJarData());

        if (handIn == InteractionHand.MAIN_HAND) {
            ItemStack stackToWrite = player.getOffhandItem();
            if (player.isShiftKeyDown()) {
                toggleStatus(player, stack);
                return InteractionResultHolder.consume(stack);
            }
            var items = data.scrollData().mutable();
            items.writeWithFeedback(player, stackToWrite);
            stack.set(DataComponentRegistry.VOID_JAR, new VoidJarData(items.toImmutable(), data.active()));
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack thisStack) {
        VoidJarData data = thisStack.getOrDefault(DataComponentRegistry.VOID_JAR, new VoidJarData());
        var items = data.scrollData().mutable();
        var written = items.writeWithFeedback(player, player.getItemInHand(handIn));
        thisStack.set(DataComponentRegistry.VOID_JAR, new VoidJarData(items.toImmutable(), data.active()));
        return written;
    }

}
