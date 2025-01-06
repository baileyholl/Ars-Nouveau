package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.common.event.timed.BuildPortalEvent;
import com.hollingsworth.arsnouveau.common.items.data.WarpScrollData;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class StableWarpScroll extends ModItem{

    public StableWarpScroll(Item.Properties properties) {
        super(properties.stacksTo(1).component(DataComponentRegistry.WARP_SCROLL, new WarpScrollData(true)));
    }

    @Override
    public boolean onEntityItemUpdate(@NotNull ItemStack stack, @NotNull ItemEntity entity) {
        return ItemsRegistry.WARP_SCROLL.get().onEntityItemUpdate(stack, entity);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        // A hack to fix the crossDim flag on existing warp scrolls
        // TODO: 1.22 - remove this tick and set the flag or check the stack elsewhere
        WarpScrollData data = stack.get(DataComponentRegistry.WARP_SCROLL);
        if(data != null && !data.crossDim()){
            stack.set(DataComponentRegistry.WARP_SCROLL, data.withCrossDim(true));
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide) {
            WarpScrollData scrollData = context.getItemInHand().get(DataComponentRegistry.WARP_SCROLL);
            if(!scrollData.isValid())
                return InteractionResult.FAIL;
            EventQueue.getServerInstance().addEvent(new BuildPortalEvent(context.getLevel(), context.getClickedPos(), context.getPlayer().getDirection().getClockWise(), scrollData));
            context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.ILLUSIONER_CAST_SPELL, context.getPlayer().getSoundSource(), 1.0F, 1.0F);
        }
        return super.useOn(context);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player player, @NotNull InteractionHand pUsedHand) {
        if(pUsedHand != InteractionHand.MAIN_HAND)
            return InteractionResultHolder.success(player.getItemInHand(pUsedHand));
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        WarpScrollData data = stack.get(DataComponentRegistry.WARP_SCROLL);

        if (!(pLevel instanceof ServerLevel serverLevel))
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        if (player.isShiftKeyDown() && !data.isValid()) {
            stack.set(DataComponentRegistry.WARP_SCROLL, new WarpScrollData(Optional.of(player.blockPosition()), player.getCommandSenderWorld().dimension().location().toString(), player.getRotationVector(), true));
            player.sendSystemMessage(Component.translatable("ars_nouveau.warp_scroll.recorded"));
        }else if(player.isShiftKeyDown() && data.isValid()){
            player.sendSystemMessage(Component.translatable("ars_nouveau.warp_scroll.already_recorded"));
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        stack.addToTooltip(DataComponentRegistry.WARP_SCROLL, context, tooltip2::add, flagIn);
    }
}
