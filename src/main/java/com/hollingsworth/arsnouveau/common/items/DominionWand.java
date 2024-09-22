package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.entity.IDecoratable;
import com.hollingsworth.arsnouveau.api.item.IRadialProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenuSlot;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.common.items.data.DominionWandData;
import com.hollingsworth.arsnouveau.common.network.HighlightAreaPacket;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateDominionWand;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DominionWand extends ModItem implements IRadialProvider {
    public DominionWand() {
        super(ItemsRegistry.defaultItemProperties().stacksTo(1).component(DataComponentRegistry.DOMINION_WAND, new DominionWandData()));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if (!pIsSelected || pLevel.isClientSide || pLevel.getGameTime() % 5 != 0) {
            return;
        }
        DominionWandData data = pStack.getOrDefault(DataComponentRegistry.DOMINION_WAND, new DominionWandData());

        if (data.storedPos().isPresent()) {
            if (pLevel.getBlockEntity(data.storedPos().get()) instanceof IWandable wandable) {
                Networking.sendToPlayerClient(new HighlightAreaPacket(wandable.getWandHighlight(new ArrayList<>()), 10), (ServerPlayer) pEntity);
            }
            return;
        }
        if (pLevel.getEntity(data.getStoredEntity()) instanceof IWandable wandable) {
            Networking.sendToPlayerClient(new HighlightAreaPacket(wandable.getWandHighlight(new ArrayList<>()), 10), (ServerPlayer) pEntity);
        }
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack doNotUseStack, Player playerEntity, @NotNull LivingEntity target, @NotNull InteractionHand hand) {

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

        if (data.storedPos().isPresent() && world.getBlockEntity(data.storedPos().get()) instanceof IWandable wandable) {
            wandable.onFinishedConnectionFirst(data.storedPos().orElse(null), data.face().orElse(null), target, playerEntity);
        }

        if (target instanceof IWandable wandable) {
            wandable.onFinishedConnectionLast(data.storedPos().orElse(null), data.face().orElse(null), target, playerEntity);
            clear(stack, playerEntity);
        }

        if (playerEntity.isShiftKeyDown() && target instanceof IDecoratable coolBoy) {
            coolBoy.setCosmeticItem(ItemStack.EMPTY);
        }

        return InteractionResult.SUCCESS;
    }

    public boolean doesSneakBypassUse(@NotNull ItemStack stack, @NotNull LevelReader world, @NotNull BlockPos pos, @NotNull Player player) {
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
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide || context.getPlayer() == null)
            return super.useOn(context);
        BlockPos pos = context.getClickedPos();
        Direction face = context.getClickedFace();
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
            data = data.storePos(pos.immutable());
            if (data.strict()) data = data.setFace(face);
            stack.set(DataComponentRegistry.DOMINION_WAND, data);
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.dominion_wand.position_set"));
            return InteractionResult.SUCCESS;
        }

        BlockPos storedPos = data.storedPos().orElse(null);
        Direction storedDirection = data.face().orElse(null);
        if (storedPos != null && world.getBlockEntity(storedPos) instanceof IWandable wandable) {
            wandable.onFinishedConnectionFirst(pos, storedDirection, (LivingEntity) world.getEntity(data.storedEntityId()), playerEntity);
        }
        if (world.getBlockEntity(pos) instanceof IWandable wandable) {
            wandable.onFinishedConnectionLast(storedPos, storedDirection, (LivingEntity) world.getEntity(data.storedEntityId()), playerEntity);
        }
        if (data.storedEntityId() != DominionWandData.NULL_ENTITY && world.getEntity(data.storedEntityId()) instanceof IWandable wandable) {
            wandable.onFinishedConnectionFirst(pos, data.strict() ? face : null, null, playerEntity);
        }

        clear(stack, playerEntity);
        return super.useOn(context);
    }

    @Override
    public @NotNull String getDescriptionId(ItemStack pStack) {
        DominionWandData data = pStack.getOrDefault(DataComponentRegistry.DOMINION_WAND, new DominionWandData());
        if (data.strict()) return super.getDescriptionId(pStack) + ".strict";
        return super.getDescriptionId(pStack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext world, @NotNull List<Component> tooltip, @NotNull TooltipFlag p_77624_4_) {
        DominionWandData data = stack.getOrDefault(DataComponentRegistry.DOMINION_WAND, new DominionWandData());
        if (data.storedEntityId() == DominionWandData.NULL_ENTITY) {
            tooltip.add(Component.translatable("ars_nouveau.dominion_wand.no_entity"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.dominion_wand.entity_stored"));
        }

        if (data.storedPos().isEmpty()) {
            tooltip.add(Component.translatable("ars_nouveau.dominion_wand.no_location"));
        } else {
            tooltip.add(Component.translatable("ars_nouveau.dominion_wand.position_stored", getPosString(data.getValidPos())));
        }

        if (data.strict()) tooltip.add(Component.literal("Side-Sensitive"));
    }

    public static String getPosString(BlockPos pos) {
        return Component.translatable("ars_nouveau.position", pos.getX(), pos.getY(), pos.getZ()).getString();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onRadialKeyPressed(ItemStack stack, Player player) {
        Minecraft.getInstance().setScreen(new GuiRadialMenu<>(getRadialMenuProviderForDominion(stack)));
    }

    public RadialMenu<String> getRadialMenuProviderForDominion(ItemStack stack) {
        return new RadialMenu<>((int slot) ->
                Networking.sendToServer(new PacketUpdateDominionWand(slot)),
                getRadialMenuSlotsForDominion(stack),
                RenderUtils::drawString,
                0);
    }

    public enum DominionSlots {
        CLEAR("ars_nouveau.dominion_wand.clear"),
        NORMAL("ars_nouveau.dominion_wand.normal"),
        STRICT("ars_nouveau.dominion_wand.strict");

        public final String key;

        DominionSlots(String key) {
            this.key = key;
        }

        public Component translatable() {
            return Component.translatable(key);
        }
    }

    public List<RadialMenuSlot<String>> getRadialMenuSlotsForDominion(ItemStack stack) {
        List<RadialMenuSlot<String>> radialMenuSlots = new ArrayList<>();
        radialMenuSlots.add(new RadialMenuSlot<>(DominionSlots.CLEAR.translatable().getString(), DominionSlots.CLEAR.key));
        radialMenuSlots.add(new RadialMenuSlot<>(DominionSlots.NORMAL.translatable().getString(), DominionSlots.NORMAL.key));
        radialMenuSlots.add(new RadialMenuSlot<>(DominionSlots.STRICT.translatable().getString(), DominionSlots.STRICT.key));
        return radialMenuSlots;
    }

}
