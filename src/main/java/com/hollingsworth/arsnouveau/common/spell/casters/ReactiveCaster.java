package com.hollingsworth.arsnouveau.common.spell.casters;

import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.FakePlayer;

public class ReactiveCaster extends SpellCaster {
    public ReactiveCaster(ItemStack stack) {
        super(stack);
    }

    @Override
    public SpellResolver getSpellResolver(SpellContext context, Level worldIn, LivingEntity playerIn, InteractionHand handIn) {
        if(!(playerIn instanceof Player) || playerIn instanceof FakePlayer){
            return new EntitySpellResolver(context);
        }
        return super.getSpellResolver(context, worldIn, playerIn, handIn);
    }
}
