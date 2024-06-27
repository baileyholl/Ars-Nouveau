package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.camera.ICameraMountable;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import javax.annotation.Nullable;
import java.util.List;

public class ScryerScroll extends ModItem {

    public ScryerScroll() {
        super();
        withTooltip(Component.translatable("tooltip.ars_nouveau.scryer_scroll"));
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().isClientSide)
            return super.useOn(pContext);
        if (pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof ICameraMountable) {
            ScryerScrollData data = new ScryerScrollData(pContext.getItemInHand());
            data.setPos(pContext.getClickedPos(), pContext.getItemInHand());
            PortUtil.sendMessage(pContext.getPlayer(), Component.translatable("ars_nouveau.scryer_scroll.bound", pContext.getClickedPos().getX() + ", " + pContext.getClickedPos().getY() + ", " + pContext.getClickedPos().getZ()));
        }
        return super.useOn(pContext);
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip2, TooltipFlag flagIn) {
        ScryerScrollData data = new ScryerScrollData(stack);
        if (data.pos != null) {
            tooltip2.add(Component.translatable("ars_nouveau.scryer_scroll.bound", data.pos.getX() + ", " + data.pos.getY() + ", " + data.pos.getZ()));
        } else {
            tooltip2.add(Component.translatable("ars_nouveau.scryer_scroll.craft"));
        }
        super.appendHoverText(stack, context, tooltip2, flagIn);
    }

    public static class ScryerScrollData {
        @Nullable
        public BlockPos pos;

        public ScryerScrollData(CompoundTag tag) {
            pos = NBTUtil.hasBlockPos(tag, "pos") ? NBTUtil.getBlockPos(tag, "pos") : null;
        }

        public ScryerScrollData(ItemStack stack) {
            this(stack.getOrCreateTag().getCompound("scryer_scroll_data"));
        }

        public CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();
            NBTUtil.storeBlockPos(tag, "pos", pos);
            return tag;
        }

        public void setPos(BlockPos pos, ItemStack stack) {
            this.pos = pos;
            stack.getOrCreateTag().put("scryer_scroll_data", toTag());
        }
    }
}
