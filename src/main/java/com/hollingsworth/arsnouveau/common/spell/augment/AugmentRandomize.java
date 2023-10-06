package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class AugmentRandomize extends AbstractAugment {

    public static final AugmentRandomize INSTANCE = new AugmentRandomize();

    public AugmentRandomize() {
        super(GlyphLib.AugmentRandomizeID, "Randomize");
    }

    @Override
    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, LivingEntity shooter, SpellContext spellContext) {
        return builder.randomize();
    }

    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }

    @Override
    public int getDefaultManaCost() {
        return 0;
    }

    @Override
    public String getBookDescription() {
        return "Randomize the behavior of some glyphs. If used with spells like Place Block and Exchange, it will randomize the block that is placed between the blocks in your hotbar.";
    }
}
