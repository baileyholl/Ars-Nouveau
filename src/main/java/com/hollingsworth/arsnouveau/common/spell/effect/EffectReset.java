package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EffectReset extends AbstractEffect implements IContextManipulator{
    public static EffectReset INSTANCE = new EffectReset();

    public static Set<AbstractSpellPart> RESET_LIMITS = ConcurrentHashMap.newKeySet();

    public EffectReset() {
        super("reset", "Reset");
    }

    @Override
    protected int getDefaultManaCost() {
        return 0;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return new HashSet<>();
    }

    @Override
    public SpellContext manipulate(SpellContext context) {
        var remainder = context.getRemainingSpell().mutable();
        int index = remainder.recipe.indexOf(EffectReset.INSTANCE);
        SpellContext newContext = context.clone().withSpell(remainder.setRecipe(new ArrayList<>(remainder.recipe.subList(0, index))).immutable());
        context.setCurrentIndex(context.getCurrentIndex() + index);
        return newContext;
    }

    @Override
    public boolean contextCanceled(SpellContext context) {
        if(context.getCancelReason() == CancelReason.NEW_CONTEXT) {
            context.setCanceled(false);
            Spell remainder = context.getRemainingSpell();
            int index = remainder.indexOf(EffectReset.INSTANCE);
            context.setCurrentIndex(context.getCurrentIndex() + index);
            return false;
        }
        return true;
    }

    @Override
    public Set<AbstractSpellPart> bypassCombinationLimitsFor() {
        return RESET_LIMITS;
    }

    @Override
    public Set<AbstractSpellPart> bypassOccurrenceLimitsFor() {
        return RESET_LIMITS;
    }

    @Override
    public boolean shouldPushContext(SpellContext context) {
        return true;
    }

    @Override
    public String getBookDescription() {
        return "Resets the spell chain to the original target if it was changed by a previous effect. For example, Burst -> Place Block -> Reset -> Break will cause Burst to place blocks, but only Break will apply to the original location. As a result, using Reset will allow you to bypass combination and duplicate limits on glyphs.";
    }
}
