package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.event.ScribesTableInteractEvent;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AbjurationEssence extends AbstractEssence {

    public AbjurationEssence() {
        super("abjuration");
    }

    public static void onScribesTableInteract(ScribesTableInteractEvent event) {
        if (!(event.stack.getItem() instanceof AbjurationEssence))
            return;
        ItemStack tableStack = event.tile.getStack();
        if (!tableStack.has(DataComponentRegistry.PRESTIDIGITATION))
            return;
        if (!event.level.isClientSide) {
            tableStack.remove(DataComponentRegistry.PRESTIDIGITATION.get());
            event.tile.updateBlock();
            PortUtil.sendMessage(event.player, Component.translatable("ars_nouveau.prestidigitation.prestidigitation_clear"));
        }
        event.setCanceled(true);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return super.onItemUseFirst(stack, context);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        tooltip2.add(Component.translatable("ars_nouveau.abjuration_essence.tooltip").withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
    }
}
