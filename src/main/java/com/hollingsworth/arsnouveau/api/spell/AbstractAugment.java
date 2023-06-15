package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.item.ISpellModifier;
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
        return 5;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

    @Override
    public abstract int getDefaultManaCost();

    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart, HitResult rayTraceResult, Level world, LivingEntity shooter, SpellContext spellContext) {
        return applyModifiers(builder, spellPart);
    }

    @Deprecated
    public SpellStats.Builder applyModifiers(SpellStats.Builder builder, AbstractSpellPart spellPart){
        return builder;
    }
}
