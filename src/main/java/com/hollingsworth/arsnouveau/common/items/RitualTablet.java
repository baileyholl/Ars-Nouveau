package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;

public class RitualTablet extends ModItem {
    public AbstractRitual ritual;

    public RitualTablet(Properties properties) {
        super(properties);
    }

    public RitualTablet(AbstractRitual ritual) {
        super(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ritual.getRegistryName())));
        this.ritual = ritual;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {

        if (!context.getLevel().isClientSide() && context.getLevel().getBlockEntity(context.getClickedPos()) instanceof RitualBrazierTile tile) {
            if (!tile.canTakeAnotherRitual()) {
                context.getPlayer().displayClientMessage(Component.translatable("ars_nouveau.ritual.no_start"), false);
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
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, display, tooltip2, flagIn);
        tooltip2.accept(Component.translatable("tooltip.ars_nouveau.tablet"));
        if (flagIn.hasShiftDown()) {
            tooltip2.accept(Component.translatable(ritual.getDescriptionKey()));
        } else {
            tooltip2.accept(Component.translatable("tooltip.ars_nouveau.hold_shift", Component.keybind("key.sneak")).withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
        }
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        return Component.literal(ritual.getName());
    }
}
