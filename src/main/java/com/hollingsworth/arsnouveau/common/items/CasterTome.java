package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.mana.IManaDiscountEquipment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.gui.SpellTooltip;
import com.hollingsworth.arsnouveau.common.items.data.TomeCasterData;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CasterTome extends ModItem implements ICasterTool, IManaDiscountEquipment {

    public CasterTome(Properties properties) {
        super(properties);
    }

    public CasterTome() {
        super(ItemsRegistry.defaultItemProperties().component(DataComponentRegistry.TOME_CASTER, new TomeCasterData()));
    }

    @Override
    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack tableStack) {
        return player.isCreative() && ICasterTool.super.onScribe(world, pos, player, handIn, tableStack);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide()) {
            return InteractionResult.PASS;
        }

        var caster = this.getSpellCaster(stack);
        if (caster == null) {
            return InteractionResult.PASS;
        }
        caster.castOnServer(handIn, Component.empty());

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip2, @NotNull TooltipFlag flagIn) {
        AbstractCaster<?> caster = getSpellCaster(stack);

        if (caster != null) {

            // If the caster is hidden, show the hidden recipe

            if (caster.isSpellHidden()) {
                tooltip2.accept(Component.literal(caster.getHiddenRecipe()).withStyle(Style.EMPTY.withFont(new net.minecraft.network.chat.FontDescription.Resource(Identifier.fromNamespaceAndPath("minecraft", "alt"))).withColor(ChatFormatting.GOLD)));
            } else if (Minecraft.getInstance().hasShiftDown() || !Config.GLYPH_TOOLTIPS.get()) {
                getInformation(stack, context, tooltip2, flagIn);
            }

            if (!Minecraft.getInstance().hasShiftDown() && !caster.getFlavorText().isEmpty())
                tooltip2.accept(Component.literal(caster.getFlavorText()).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.BLUE)));

            tooltip2.accept(Component.translatable("tooltip.ars_nouveau.caster_tome"));

        }
        super.appendHoverText(stack, context, display, tooltip2, flagIn);
    }

    @Override
    public int getManaDiscount(ItemStack i, Spell spell) {
        return spell.getCost() / 2;
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack pStack) {
        AbstractCaster<?> caster = getSpellCaster(pStack);
        if (caster != null && !Minecraft.getInstance().hasShiftDown() && Config.GLYPH_TOOLTIPS.get() && !caster.isSpellHidden() && !caster.getSpell().isEmpty())
            return Optional.of(new SpellTooltip(caster));
        return Optional.empty();
    }
}
