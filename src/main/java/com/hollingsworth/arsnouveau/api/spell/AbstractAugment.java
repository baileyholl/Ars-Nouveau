package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.item.ISpellModifier;
import com.hollingsworth.arsnouveau.common.util.SpellPartConfigUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class AbstractAugment extends AbstractSpellPart implements ISpellModifier {

    public AbstractAugment(String tag, String description) {
        super(tag, description);
    }

    public AbstractAugment(ResourceLocation tag, String description) {
        super(tag, description);
    }

    @Override
    public Integer getTypeIndex() {
        return 10;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

    /**
     * Warning: semi-confusing.
     * This method accesses the *other* parts AugmentCosts map instead of its own.
     * This is because Methods and Effects store Augment Costs for the cost of an augment applied to it.
     * This is done for config reasons, so augment costs for Break are found in break.
     * Therefore, this method fetches the 'Break' part's AugmentCosts map and gets the cost for this augment if it is applied to the 'Break' part.
     */
    public int getCostForPart(AbstractSpellPart spellPart) {
        if (spellPart instanceof AbstractAugment)
            return this.getCastingCost();
        SpellPartConfigUtil.AugmentCosts augmentCosts = spellPart.augmentCosts;
        if (augmentCosts == null) {
            return this.getCastingCost();
        }
        return augmentCosts.getAugmentCost(spellPart.getRegistryName(), this.getCastingCost());
    }

    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, LivingEntity shooter, SpellContext spellContext) {
        return applyModifiers(builder, spellPart);
    }

    @Deprecated
    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart) {
        return builder;
    }

    @Override
    public Component getTypeName() {
        return Component.translatable("ars_nouveau.spell_book_gui.augment");
    }

    @Override
    public DocAssets.BlitInfo getTypeIcon() {
        return DocAssets.AUGMENT_ICON;
    }
}
