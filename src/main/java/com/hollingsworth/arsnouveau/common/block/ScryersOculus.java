package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.client.gui.radial_menu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenu;
import com.hollingsworth.arsnouveau.client.gui.radial_menu.RadialMenuSlot;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.block.tile.ScryersOculusTile;
import com.hollingsworth.arsnouveau.common.items.data.ScryPosData;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketMountCamera;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ScryersOculus extends TickableModBlock {

    public ScryersOculus() {
        this(defaultProperties().noOcclusion());
    }

    public ScryersOculus(Properties properties) {
        super(properties);
    }


    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pHand == InteractionHand.MAIN_HAND && pLevel.isClientSide) {
            openMenu(pLevel, pPos, pPlayer);
        }
        return super.useItemOn(stack, pState, pLevel, pPos, pPlayer, pHand, pHit);
    }


    @OnlyIn(Dist.CLIENT)
    public void openMenu(Level pLevel, BlockPos pPos, Player pPlayer) {
        List<RadialMenuSlot<Item>> slots = new ArrayList<>();
        List<ItemStack> stackList = new ArrayList<>();
        int offset = 3;
        for (BlockPos b : BlockPos.betweenClosed(pPos.offset(offset, -offset, offset), pPos.offset(-offset, offset, -offset))) {
            if (pLevel.getBlockEntity(b) instanceof ArcanePedestalTile tile && tile.getStack().is(ItemsRegistry.SCRYER_SCROLL.get())) {
                slots.add(new RadialMenuSlot<>(tile.getStack().getHoverName().getString(), tile.getStack().getItem(), new ArrayList<>()));
                stackList.add(tile.getStack());
            }
        }
        if (slots.isEmpty()) {
            PortUtil.sendMessage(pPlayer, Component.translatable("ars_nouveau.scryers_eye.no_scrolls"));
            return;
        }
        Minecraft.getInstance().setScreen(new GuiRadialMenu<>(new RadialMenu<>((int scroll) -> {
            ScryPosData data = stackList.get(scroll).get(DataComponentRegistry.SCRY_DATA);
            if (data == null || data.pos().isEmpty()) {
                PortUtil.sendMessage(pPlayer, Component.translatable("ars_nouveau.scryers_oculus.no_pos"));
                return;
            }
            Networking.sendToServer(new PacketMountCamera(data.pos().orElse(null)));
        }, slots, (slotData, posestack, positionx, posy, size, transparent) -> RenderUtils.drawItemAsIcon(slotData.getDefaultInstance(), posestack, positionx, posy, size, transparent), 3)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ScryersOculusTile(pPos, pState);
    }

    @Override
    public boolean isPathfindable(BlockState pState, PathComputationType pType) {
        return false;
    }
}
