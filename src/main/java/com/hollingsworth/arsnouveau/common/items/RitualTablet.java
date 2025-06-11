package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RitualTablet extends ModItem {
    public AbstractRitual ritual;

    public RitualTablet(Properties properties) {
        super(properties);
    }

    public RitualTablet(AbstractRitual ritual) {
        super(ItemsRegistry.defaultItemProperties());
        this.ritual = ritual;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {

        if (!context.getLevel().isClientSide() && context.getLevel().getBlockEntity(context.getClickedPos()) instanceof RitualBrazierTile tile) {
            if (!tile.canTakeAnotherRitual()) {
                context.getPlayer().sendSystemMessage(Component.translatable("ars_nouveau.ritual.no_start"));
                return InteractionResult.PASS;
            }

            tile.setRitual(ritual.getRegistryName());
            if (!context.getPlayer().hasInfiniteMaterials())
                context.getItemInHand().shrink(1);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip2, flagIn);
        tooltip2.add(Component.translatable("tooltip.ars_nouveau.tablet"));
        if (flagIn.hasShiftDown()) {
            tooltip2.add(Component.translatable(ritual.getDescriptionKey()));
        } else {
            tooltip2.add(Component.translatable("tooltip.ars_nouveau.hold_shift", Component.keybind("key.sneak")).withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
        }
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        return Component.literal(ritual.getName());
    }
}
