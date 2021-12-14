package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

public class AugmentExtendTime extends AbstractAugment {
    public static AugmentExtendTime INSTANCE = new AugmentExtendTime();

    private AugmentExtendTime() {
        super(GlyphLib.AugmentExtendTimeID, "Extend Time");
    }

    @Override
    public int getManaCost() {
        return 10;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.CLOCK;
    }

    @Override
    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart) {
        builder.addDurationModifier(1.0);
        return super.applyModifiers(builder, spellPart);
    }

    @Override
    public String getBookDescription() {
        return "Extends the time that spells last, including buffs, fangs, and summons";
    }
}
