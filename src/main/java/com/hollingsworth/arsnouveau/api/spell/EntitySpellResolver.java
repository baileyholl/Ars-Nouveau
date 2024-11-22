package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * A special Spell resolver that ignores player limits such as mana.
 */
public class EntitySpellResolver extends SpellResolver {

    public EntitySpellResolver(SpellContext context) {
        super(context);
    }

    public void onCastOnEntity(LivingEntity target) {
        super.onCastOnEntity(ItemStack.EMPTY, target, InteractionHand.MAIN_HAND);
    }

    @Override
    protected boolean enoughMana(LivingEntity entity) {
        return true;
    }
}
