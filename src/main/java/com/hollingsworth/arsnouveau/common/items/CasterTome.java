package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.mana.IManaDiscountEquipment;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.NotEnoughManaPacket;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

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
    public @NotNull ISpellCaster getSpellCaster(ItemStack stack) {
        return new TomeSpellCaster(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        if (worldIn == null)
            return;
        ISpellCaster caster = getSpellCaster(stack);
        Spell spell = caster.getSpell();

        getInformation(stack, worldIn, tooltip2, flagIn);

        tooltip2.add(Component.translatable("tooltip.ars_nouveau.caster_tome"));
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }

    @Override
    public int getManaDiscount(ItemStack i, Spell spell) {
        return spell.getCost() / 2;
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
