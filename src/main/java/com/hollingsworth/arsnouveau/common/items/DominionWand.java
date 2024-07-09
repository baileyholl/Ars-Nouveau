package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.common.items.data.DominionWandData;
import com.hollingsworth.arsnouveau.common.network.HighlightAreaPacket;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

import java.util.ArrayList;
import java.util.List;

public class DominionWand extends ModItem {
    public DominionWand() {
        super(ItemsRegistry.defaultItemProperties().stacksTo(1).component(DataComponentRegistry.DOMINION_WAND, new DominionWandData()));
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if (!pIsSelected || pLevel.isClientSide || pLevel.getGameTime() % 5 != 0) {
            return;
        }
        DominionWandData data = pStack.getOrDefault(DataComponentRegistry.DOMINION_WAND, new DominionWandData());
        BlockPos pos = data.storedPos();
        if (pos != null) {
            if (pLevel.getBlockEntity(pos) instanceof IWandable wandable) {
                Networking.sendToPlayerClient(new HighlightAreaPacket(wandable.getWandHighlight(new ArrayList<>()), 10), (ServerPlayer) pEntity);
            }
            return;
        }
        if (pLevel.getEntity(data.getStoredEntity()) instanceof IWandable wandable) {
            Networking.sendToPlayerClient(new HighlightAreaPacket(wandable.getWandHighlight(new ArrayList<>()), 10), (ServerPlayer) pEntity);
        }
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack doNotUseStack, Player playerEntity, LivingEntity target, InteractionHand hand) {

        if (playerEntity.level.isClientSide || hand != InteractionHand.MAIN_HAND)
            return InteractionResult.PASS;

        ItemStack stack = playerEntity.getItemInHand(hand);
        DominionWandData data = stack.getOrDefault(DataComponentRegistry.DOMINION_WAND, new DominionWandData());
        if (playerEntity.isShiftKeyDown() && target instanceof IWandable wandable) {
            wandable.onWanded(playerEntity);
            clear(stack, playerEntity);
            return InteractionResult.SUCCESS;
        }
        // If the wand has nothing, store it
        if (!data.hasStoredData()) {
            stack.set(DataComponentRegistry.DOMINION_WAND, data.storeEntity(target.getId()));
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.dominion_wand.stored_entity"));
            return InteractionResult.SUCCESS;
        }
        Level world = playerEntity.getCommandSenderWorld();

        if (data.storedPos() != null && world.getBlockEntity(data.storedPos()) instanceof IWandable wandable) {
            wandable.onFinishedConnectionFirst(data.storedPos(), data.face(), target, playerEntity);
        }

        if (target instanceof IWandable wandable) {
            wandable.onFinishedConnectionLast(data.storedPos(), data.face(), target, playerEntity);
            clear(stack, playerEntity);
        }

        if (playerEntity.isShiftKeyDown() && target instanceof IDecoratable coolBoy) {
            coolBoy.setCosmeticItem(ItemStack.EMPTY);
        }

        return InteractionResult.SUCCESS;
    }

    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return false;
    }

    public void clear(ItemStack stack, Player player) {
        DominionWandData data = stack.getOrDefault(DataComponentRegistry.DOMINION_WAND, new DominionWandData())
                .setFace(null)
                .storeEntity(DominionWandData.NULL_ENTITY)
                .storePos(null);
        stack.set(DataComponentRegistry.DOMINION_WAND, data);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        DominionWandData data = stack.getOrDefault(DataComponentRegistry.DOMINION_WAND, new DominionWandData());
        if (pPlayer.isShiftKeyDown() && !data.hasStoredData()) {
            data = data.toggleMode();
            stack.set(DataComponentRegistry.DOMINION_WAND, data);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide || context.getPlayer() == null)
            return super.useOn(context);
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        Player playerEntity = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        DominionWandData data = stack.getOrDefault(DataComponentRegistry.DOMINION_WAND, new DominionWandData());

        if (playerEntity.isShiftKeyDown() && world.getBlockEntity(pos) instanceof IWandable wandable && !data.hasStoredData()) {
            wandable.onWanded(playerEntity);
            clear(stack, playerEntity);
            return InteractionResult.CONSUME;
        }

        if (!data.hasStoredData()) {
            stack.set(DataComponentRegistry.DOMINION_WAND, data.storePos(pos.immutable()));
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.dominion_wand.position_set"));
            return InteractionResult.SUCCESS;
        }
        if (data.face() == null && data.strict()){
            stack.set(DataComponentRegistry.DOMINION_WAND, data.setFace(context.getClickedFace()));
        }

        if (data.storedPos() != null && world.getBlockEntity(data.storedPos()) instanceof IWandable wandable) {
            wandable.onFinishedConnectionFirst(pos, data.face(), (LivingEntity) world.getEntity(data.storedEntityId()), playerEntity);
        }
        if (world.getBlockEntity(pos) instanceof IWandable wandable) {
            wandable.onFinishedConnectionLast(data.storedPos(), data.face(), (LivingEntity) world.getEntity(data.storedEntityId()), playerEntity);
        }
        if (data.storedEntityId() != DominionWandData.NULL_ENTITY && world.getEntity(data.storedEntityId()) instanceof IWandable wandable) {
            wandable.onFinishedConnectionFirst(pos, data.face(), null, playerEntity);
        }

        clear(stack, playerEntity);
        return super.useOn(context);
    }

    @Override
    public String getDescriptionId(ItemStack pStack) {
        DominionWandData data = pStack.getOrDefault(DataComponentRegistry.DOMINION_WAND, new DominionWandData());
        if (data.strict()) return super.getDescriptionId(pStack) + ".strict";
        return super.getDescriptionId(pStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext world, List<Component> tooltip, TooltipFlag p_77624_4_) {
        DominionWandData data = stack.getOrDefault(DataComponentRegistry.DOMINION_WAND, new DominionWandData());
        if (data.storedEntityId() == DominionWandData.NULL_ENTITY) {
            tooltip.add(Component.translatable("ars_nouveau.dominion_wand.no_entity"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.dominion_wand.entity_stored"));
        }

        if (data.storedPos() == null) {
            tooltip.add(Component.translatable("ars_nouveau.dominion_wand.no_location"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.dominion_wand.position_stored", getPosString(data.storedPos())));
        }

        if (data.strict()) tooltip.add(Component.literal("Side-Sensitive"));
    }

    public static String getPosString(BlockPos pos) {
        return Component.translatable("ars_nouveau.position", pos.getX(), pos.getY(), pos.getZ()).getString();
    }
}
