package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class CasterTome extends ModItem implements ICasterTool {

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
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        if (worldIn == null)
            return;
        ISpellCaster caster = getSpellCaster(stack);
        Spell spell = caster.getSpell();

        getInformation(stack, worldIn, tooltip2, flagIn);

        tooltip2.add(Component.translatable("tooltip.ars_nouveau.caster_tome"));
        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }
}
