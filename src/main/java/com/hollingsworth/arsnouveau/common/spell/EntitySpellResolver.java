package com.hollingsworth.arsnouveau.common.spell;

import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A special Spell resolver that ignores player limits such as mana.
 */
public class EntitySpellResolver extends SpellResolver {

    public EntitySpellResolver(AbstractCastMethod cast, ArrayList<AbstractSpellPart> spell_recipe) {
        super(cast, spell_recipe);
    }

    public EntitySpellResolver(AbstractSpellPart[] spellParts) {
        super(spellParts);
    }

    public EntitySpellResolver(ArrayList<AbstractSpellPart> spell_recipe) {
        super(spell_recipe);
    }

    @Override
    boolean enoughMana(LivingEntity entity) {
        return true;
    }



}
