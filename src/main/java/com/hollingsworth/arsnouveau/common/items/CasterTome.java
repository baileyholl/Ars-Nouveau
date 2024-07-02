package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.mana.IManaDiscountEquipment;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.gui.SpellTooltip;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.NotEnoughManaPacket;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class CasterTome extends ModItem implements ICasterTool, IManaDiscountEquipment {

    public CasterTome(Properties properties) {
        super(properties);
    }

    public CasterTome() {
        super();
    }

    @Override
    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack tableStack) {
        return player.isCreative() && ICasterTool.super.onScribe(world, pos, player, handIn, tableStack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        ISpellCaster caster = getSpellCaster(stack);
        Spell spell = caster.getSpell();
        return caster.castSpell(worldIn, playerIn, handIn, Component.empty(), spell);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, List<Component> tooltip2, TooltipFlag flagIn) {
        if (context == null)
            return;
        ISpellCaster caster = getSpellCaster(stack);

        if (Config.GLYPH_TOOLTIPS.get() || Screen.hasShiftDown()) {
            if (caster.isSpellHidden()) {
                tooltip2.add(Component.literal(caster.getHiddenRecipe()).withStyle(Style.EMPTY.withFont(ResourceLocation.fromNamespaceAndPath("minecraft", "alt")).withColor(ChatFormatting.GOLD)));
            }
            if (!caster.getFlavorText().isEmpty())
                tooltip2.add(Component.literal(caster.getFlavorText()).withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.BLUE)));
        } else getInformation(stack, context, tooltip2, flagIn);

        tooltip2.add(Component.translatable("tooltip.ars_nouveau.caster_tome"));

        super.appendHoverText(stack, context, tooltip2, flagIn);
    }

    @Override
    public int getManaDiscount(ItemStack i, Spell spell) {
        return spell.getCost() / 2;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
        ISpellCaster caster = getSpellCaster(pStack);
        if (!Screen.hasShiftDown() && Config.GLYPH_TOOLTIPS.get() && !caster.isSpellHidden() && !caster.getSpell().isEmpty())
            return Optional.of(new SpellTooltip(caster));
        return Optional.empty();
    }

    /**
     * A Spell resolver that ignores mana player limits.
     */
    public static class TomeSpellCaster extends SpellCaster {
        public TomeSpellCaster(ItemStack stack) {
            super(stack);
        }

        @Override
        public SpellResolver getSpellResolver(SpellContext context, Level worldIn, LivingEntity playerIn, InteractionHand handIn) {
            return new SpellResolver(context) {
                @Override
                protected boolean enoughMana(LivingEntity entity) {
                    int totalCost = getResolveCost();
                    IManaCap manaCap = CapabilityRegistry.getMana(entity).orElse(null);
                    if (manaCap == null)
                        return false;
                    boolean canCast = totalCost <= manaCap.getCurrentMana() || manaCap.getCurrentMana() == manaCap.getMaxMana() || (entity instanceof Player player && player.isCreative());
                    if (!canCast && !entity.getCommandSenderWorld().isClientSide && !silent) {
                        PortUtil.sendMessageNoSpam(entity, Component.translatable("ars_nouveau.spell.no_mana"));
                        if (entity instanceof ServerPlayer serverPlayer)
                            Networking.sendToPlayerClient(new NotEnoughManaPacket(totalCost), serverPlayer);
                    }
                    return canCast;
                }

            };
        }
    }

}
