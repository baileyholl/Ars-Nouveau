package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.nbt.ItemstackData;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.network.HighlightAreaPacket;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DominionWand extends ModItem {
    public DominionWand() {
        super(ItemsRegistry.defaultItemProperties().stacksTo(1));
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if (!pIsSelected || pLevel.isClientSide || pLevel.getGameTime() % 5 != 0) {
            return;
        }
        DominionData data = new DominionData(pStack);
        BlockPos pos = data.storedPos;
        if (pos != null) {
            if (pLevel.getBlockEntity(pos) instanceof IWandable wandable) {
                Networking.sendToPlayerClient(new HighlightAreaPacket(wandable.getWandHighlight(new ArrayList<>()), 10), (ServerPlayer) pEntity);
            }
            return;
        }
        if (data.getEntity(pLevel) instanceof IWandable wandable) {
            Networking.sendToPlayerClient(new HighlightAreaPacket(wandable.getWandHighlight(new ArrayList<>()), 10), (ServerPlayer) pEntity);
        }
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack doNotUseStack, Player playerEntity, LivingEntity target, InteractionHand hand) {

        if (playerEntity.level.isClientSide || hand != InteractionHand.MAIN_HAND)
            return InteractionResult.PASS;

        ItemStack stack = playerEntity.getItemInHand(hand);
        DominionData data = new DominionData(stack);
        if (playerEntity.isShiftKeyDown() && target instanceof IWandable wandable) {
            wandable.onWanded(playerEntity);
            clear(stack, playerEntity);
            return InteractionResult.SUCCESS;
        }
        // If the wand has nothing, store it
        if (!data.hasStoredData()) {
            data.setStoredEntityID(target.getId());
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.dominion_wand.stored_entity"));
            return InteractionResult.SUCCESS;
        }
        Level world = playerEntity.getCommandSenderWorld();

        if (data.getStoredPos() != null && world.getBlockEntity(data.getStoredPos()) instanceof IWandable wandable) {
            wandable.onFinishedConnectionFirst(data.getStoredPos(), data.getFace(), target, playerEntity);
        }

        if (target instanceof IWandable wandable) {
            wandable.onFinishedConnectionLast(data.getStoredPos(), data.getFace(), target, playerEntity);
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
        DominionData data = new DominionData(stack);
        data.setStoredPos(null);
        data.setStoredEntityID(-1);
        data.setFacing(null);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        DominionData data = new DominionData(stack);
        if (pPlayer.isShiftKeyDown() && !data.hasStoredData()) {
            data.changeMode();
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide || context.getPlayer() == null)
            return super.useOn(context);
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
        Level world = context.getLevel();
        Player playerEntity = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        DominionData data = new DominionData(stack);

        if (playerEntity.isShiftKeyDown() && world.getBlockEntity(pos) instanceof IWandable wandable && !data.hasStoredData()) {
            wandable.onWanded(playerEntity);
            clear(stack, playerEntity);
            return InteractionResult.CONSUME;
        }

        if (!data.hasStoredData()) {
            data.setStoredPos(pos.immutable());
            if (data.strict) data.setFacing(face);
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.dominion_wand.position_set"));
            return InteractionResult.SUCCESS;
        }

        if (data.getStoredPos() != null && world.getBlockEntity(data.getStoredPos()) instanceof IWandable wandable) {
            wandable.onFinishedConnectionFirst(pos, data.getFace(), (LivingEntity) world.getEntity(data.getStoredEntityID()), playerEntity);
        }
        if (world.getBlockEntity(pos) instanceof IWandable wandable) {
            wandable.onFinishedConnectionLast(data.getStoredPos(), data.getFace(), (LivingEntity) world.getEntity(data.getStoredEntityID()), playerEntity);
        }
        if (data.getStoredEntityID() != -1 && world.getEntity(data.getStoredEntityID()) instanceof IWandable wandable) {
            wandable.onFinishedConnectionFirst(pos, data.strict ? face : null, null, playerEntity);
        }

        clear(stack, playerEntity);
        return super.useOn(context);
    }

    @Override
    public String getDescriptionId(ItemStack pStack) {
        DominionData data = new DominionData(pStack);
        if (data.strict) return super.getDescriptionId(pStack) + ".strict";
        return super.getDescriptionId(pStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag p_77624_4_) {
        DominionData data = new DominionData(stack);
        if (data.getStoredEntityID() == -1) {
            tooltip.add(Component.translatable("ars_nouveau.dominion_wand.no_entity"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.dominion_wand.entity_stored"));
        }

        if (data.getStoredPos() == null) {
            tooltip.add(Component.translatable("ars_nouveau.dominion_wand.no_location"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.dominion_wand.position_stored", getPosString(data.getStoredPos())));
        }

        if (data.strict) tooltip.add(Component.literal("Side-Sensitive"));
    }

    public static String getPosString(BlockPos pos) {
        return Component.translatable("ars_nouveau.position", pos.getX(), pos.getY(), pos.getZ()).getString();
    }

    public static class DominionData extends ItemstackData {
        private BlockPos storedPos;

        public @Nullable Direction getFace() {
            return facing;
        }

        private @Nullable Direction facing = null;

        boolean strict = false;

        private int storedEntityID;

        public DominionData(ItemStack stack) {
            super(stack);
            CompoundTag tag = getItemTag(stack);
            if (tag == null) {
                return;
            }
            storedPos = NBTUtil.getNullablePos(tag, "stored");
            storedEntityID = tag.getInt("entityID");
            facing = tag.contains("facing") ? Direction.from3DDataValue(tag.getInt("facing")) : null;
            strict = tag.getBoolean("mode");
        }

        public boolean hasStoredData() {
            return getStoredPos() != null || getStoredEntityID() != -1;
        }

        public @Nullable BlockPos getStoredPos() {
            return storedPos == BlockPos.ZERO || storedPos == null ? null : storedPos.immutable();
        }

        public int getStoredEntityID() {
            return storedEntityID == 0 ? -1 : storedEntityID;
        }

        public @Nullable Entity getEntity(Level world) {
            return world.getEntity(storedEntityID);
        }

        public void setStoredPos(@Nullable BlockPos pos) {
            storedPos = pos;
            writeItem();
        }

        public void setStoredEntityID(int id) {
            storedEntityID = id;
            writeItem();
        }

        public void setFacing(Direction facing) {
            this.facing = facing;
            writeItem();
        }

        public void changeMode() {
            strict = !strict;
            writeItem();
        }

        @Override
        public String getTagString() {
            return "an_dominion_wand";
        }

        @Override
        public void writeToNBT(CompoundTag tag) {
            if (storedPos != null) {
                NBTUtil.storeBlockPos(tag, "stored", storedPos);
            }
            if (facing != null) tag.putInt("facing", facing.ordinal());
            tag.putInt("entityID", storedEntityID);
            tag.putBoolean("mode", strict);
        }
    }

}
