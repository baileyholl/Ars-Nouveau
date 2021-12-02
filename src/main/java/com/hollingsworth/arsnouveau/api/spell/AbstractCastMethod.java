package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractCastMethod extends AbstractSpellPart {

    public abstract void onCast(@Nullable ItemStack stack, LivingEntity playerEntity, Level world, List<AbstractAugment> augments, SpellContext context, SpellResolver resolver);

    public abstract void onCastOnBlock(UseOnContext context, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver);

    public abstract void onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver);

    public abstract void onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver);

    public abstract boolean wouldCastSuccessfully(@Nullable ItemStack stack, LivingEntity playerEntity, Level world, List<AbstractAugment> augments, SpellResolver resolver);

    public abstract boolean wouldCastOnBlockSuccessfully(UseOnContext context, List<AbstractAugment> augments, SpellResolver resolver);

    public abstract boolean wouldCastOnBlockSuccessfully(BlockHitResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellResolver resolver);

    public abstract boolean wouldCastOnEntitySuccessfully(@Nullable ItemStack stack, LivingEntity caster, Entity target, InteractionHand hand, List<AbstractAugment> augments, SpellResolver resolver);

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        super.buildAugmentLimitsConfig(builder, getDefaultAugmentLimits());
    }

    public AbstractCastMethod(String tag, String description){
        super(tag,description);
    }


}
