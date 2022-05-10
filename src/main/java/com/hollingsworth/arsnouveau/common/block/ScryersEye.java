package com.hollingsworth.arsnouveau.common.block;

import com.hollingsworth.arsnouveau.client.gui.RadialMenu.GuiRadialMenu;
import com.hollingsworth.arsnouveau.client.gui.RadialMenu.RadialMenu;
import com.hollingsworth.arsnouveau.client.gui.RadialMenu.RadialMenuSlot;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.hollingsworth.arsnouveau.common.block.tile.ArcanePedestalTile;
import com.hollingsworth.arsnouveau.common.block.tile.ScryersEyeTile;
import com.hollingsworth.arsnouveau.common.items.ScryerScroll;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketMountCamera;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ScryersEye extends TickableModBlock{
    public ScryersEye(Properties properties, String registry) {
        super(properties, registry);
    }

    public ScryersEye(String registryName) {
        this(defaultProperties().noOcclusion(), registryName);
    }

    public ScryersEye(Properties properties) {
        super(properties);
    }



    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(pHand == InteractionHand.MAIN_HAND && pLevel.isClientSide){
            openMenu(pLevel, pPos, pPlayer);
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }


    @OnlyIn(Dist.CLIENT)
    public void openMenu(Level pLevel, BlockPos pPos, Player pPlayer) {
        List<RadialMenuSlot<Item>> slots = new ArrayList<>();
        List<ItemStack> stackList = new ArrayList<>();
        int offset = 3;
        for (BlockPos b : BlockPos.betweenClosed(pPos.offset(offset, -offset, offset), pPos.offset(-offset, offset, -offset))) {
            if (pLevel.getBlockEntity(b) instanceof ArcanePedestalTile tile && tile.stack.is(ItemsRegistry.SCRYER_SCROLL)) {
                slots.add(new RadialMenuSlot<>(tile.stack.getHoverName().getString(), tile.stack.getItem(), new ArrayList<>()));
                stackList.add(tile.stack);
            }
        }
        if(slots.isEmpty()){
            return;
        }
        Minecraft.getInstance().setScreen(new GuiRadialMenu(new RadialMenu<>((int scroll) -> {
            ScryerScroll.ScryerScrollData data = new ScryerScroll.ScryerScrollData(stackList.get(scroll - 1));
            if(data.pos == null){
                PortUtil.sendMessage(pPlayer, new TranslatableComponent("ars_nouveau.scryers_eye.no_pos"));
                return;
            }
            Networking.INSTANCE.sendToServer(new PacketMountCamera(data.pos));
        }, slots, RenderUtils::drawItemAsIcon, 3)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ScryersEyeTile(pPos ,pState);
    }
}
