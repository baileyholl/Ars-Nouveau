package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;

import java.util.List;

/**
 * A special Spell resolver that ignores player limits such as mana.
 */
public class EntitySpellResolver extends SpellResolver {

    @Deprecated
    public EntitySpellResolver(List<AbstractSpellPart> spell_recipe, SpellContext context) {
        super(spell_recipe, context);
    }

    public EntitySpellResolver(SpellContext context){
        super(context);
    }


    public void onCastOnEntity(LivingEntity target){
        super.onCastOnEntity(ItemStack.EMPTY, spellContext.caster, target, InteractionHand.MAIN_HAND);
    }

    @Override
    boolean enoughMana(LivingEntity entity) {
        return true;
    }
}
