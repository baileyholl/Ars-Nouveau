package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
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


    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide && level.getBlockEntity(context.getClickedPos()) instanceof ScribesTile scribesTile) {
            if (scribesTile.getStack().has(DataComponentRegistry.PRESTIDIGITATION)) {
                scribesTile.getStack().remove(DataComponentRegistry.PRESTIDIGITATION.get());
                scribesTile.setChanged();
                context.getPlayer().sendSystemMessage(Component.translatable("ars_nouveau.prestidigitation_clear"));
            }
        }
        return super.useOn(context);
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
