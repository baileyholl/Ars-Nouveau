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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class StableWarpScroll extends ModItem{

    public StableWarpScroll(Item.Properties properties) {
        super(properties.stacksTo(1).component(DataComponentRegistry.WARP_SCROLL, new WarpScrollData(null, null, null, true)));
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        return ItemsRegistry.WARP_SCROLL.get().onEntityItemUpdate(stack, entity);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide) {
            WarpScrollData scrollData = context.getItemInHand().getOrDefault(DataComponentRegistry.WARP_SCROLL, new WarpScrollData(null, null, null, true));
            if(!scrollData.isValid())
                return InteractionResult.FAIL;
            EventQueue.getServerInstance().addEvent(new BuildPortalEvent(context.getLevel(), context.getClickedPos(), context.getPlayer().getDirection().getClockWise(), scrollData));
            context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.ILLUSIONER_CAST_SPELL, context.getPlayer().getSoundSource(), 1.0F, 1.0F);
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player player, InteractionHand pUsedHand) {
        if(pUsedHand != InteractionHand.MAIN_HAND)
            return InteractionResultHolder.success(player.getItemInHand(pUsedHand));
        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        WarpScrollData data = stack.getOrDefault(DataComponentRegistry.WARP_SCROLL, new WarpScrollData(null, null, null, true));

        if (!(pLevel instanceof ServerLevel serverLevel))
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        if (player.isShiftKeyDown() && !data.isValid()) {
            stack.set(DataComponentRegistry.WARP_SCROLL, new WarpScrollData(player.blockPosition(), player.getCommandSenderWorld().dimension().location().toString(), player.getRotationVector(), true));
            player.sendSystemMessage(Component.translatable("ars_nouveau.warp_scroll.recorded"));
        }else if(player.isShiftKeyDown() && data.isValid()){
            player.sendSystemMessage(Component.translatable("ars_nouveau.warp_scroll.already_recorded"));
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }
}
