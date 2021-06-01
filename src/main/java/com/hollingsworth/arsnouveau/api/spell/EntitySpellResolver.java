package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.entity.LivingEntity;

import java.util.List;

/**
 * A special Spell resolver that ignores player limits such as mana.
 */
public class EntitySpellResolver extends SpellResolver {


    public EntitySpellResolver(AbstractCastMethod cast, List<AbstractSpellPart> spell_recipe, SpellContext context) {
        super(cast, spell_recipe, context);
    }

    public EntitySpellResolver(AbstractSpellPart[] spellParts, SpellContext context) {
        super(spellParts, context);
    }

    public EntitySpellResolver(List<AbstractSpellPart> spell_recipe, SpellContext context) {
        super(spell_recipe, context);
    }

    @Override
    boolean enoughMana(LivingEntity entity) {
        return true;
    }
}
