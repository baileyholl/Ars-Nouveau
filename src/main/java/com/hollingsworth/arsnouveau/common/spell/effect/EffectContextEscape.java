package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.IContextEscape;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectContextEscape extends AbstractEffect implements IContextEscape {

    public static EffectContextEscape INSTANCE = new EffectContextEscape();

    private EffectContextEscape() {
        super(GlyphLib.EffectContextEscapeID, "Context Escape");
    }

    //The lack of on resolve is on purpose
    //this glyph is just there to implement the interface

    @Override
    protected int getDefaultManaCost() {
        return 0;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return Set.of();
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }

    @Override
    public String getName() {
        return "Escape Context";
    }

    @Override
    public String getBookDescription() {
        return "Allows you to exit the context made by the most recent context creating glyph, such as linger or burst.";
    }
}
