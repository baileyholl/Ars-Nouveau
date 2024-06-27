package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.common.event.timed.BuildPortalEvent;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class StableWarpScroll extends ModItem{

    public StableWarpScroll(Item.Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        return ItemsRegistry.WARP_SCROLL.get().onEntityItemUpdate(stack, entity);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide) {
            WarpScroll.WarpScrollData scrollData = WarpScroll.WarpScrollData.get(context.getItemInHand());
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
        WarpScroll.WarpScrollData data = WarpScroll.WarpScrollData.get(stack);

        if (!(pLevel instanceof ServerLevel serverLevel))
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
        if (player.isShiftKeyDown() && !data.isValid()) {
            data.setData(player.blockPosition(), player.getCommandSenderWorld().dimension().location().toString(), player.getRotationVector());
            player.sendSystemMessage(Component.translatable("ars_nouveau.warp_scroll.recorded"));
        }else if(player.isShiftKeyDown() && data.isValid()){
            player.sendSystemMessage(Component.translatable("ars_nouveau.warp_scroll.already_recorded"));
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip2, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        WarpScroll.WarpScrollData data = WarpScroll.WarpScrollData.get(stack);
        if (!data.isValid()) {
            tooltip2.add(Component.translatable("ars_nouveau.warp_scroll.no_location"));
            return;
        }
        BlockPos pos = data.getPos();
        tooltip2.add(Component.translatable("ars_nouveau.position", pos.getX(), pos.getY(), pos.getZ()));
        String dimId = data.getDimension();
        if(dimId != null) {
            ResourceLocation resourceLocation = ResourceLocation.tryParse(dimId);
            tooltip2.add(Component.translatable(resourceLocation.getPath() + "." + resourceLocation.getNamespace() + ".name"));
        }
    }

    public static class StableScrollData extends WarpScroll.WarpScrollData{
        public StableScrollData(ItemStack stack) {
            super(stack);
        }

        @Override
        public boolean canTeleportWithDim(String dim) {
            return true;
        }

        @Override
        public boolean canTeleportWithDim(Level level) {
            return true;
        }
    }
}
