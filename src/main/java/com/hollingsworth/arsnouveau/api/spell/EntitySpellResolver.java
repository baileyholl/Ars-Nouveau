package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.entity.LivingEntity;

import java.util.List;

/**
 * A special Spell resolver that ignores player limits such as mana.
 */
public class EntitySpellResolver extends SpellResolver {

    @Deprecated
    public EntitySpellResolver(List<AbstractSpellPart> spell_recipe, SpellContext context) {
        super(spell_recipe, context);
    }

    @Override
    boolean enoughMana(LivingEntity entity) {
        return true;
    }
}
