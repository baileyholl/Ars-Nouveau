package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Base class for interfacing with filter glyphs from addons and making your own. Based off TooManyGlyphs.
 */
public abstract class AbstractFilter extends AbstractEffect implements IFilter {

    public AbstractFilter(ResourceLocation registryName, String name) {
        super(registryName, name);
    }

    @Override
    public int getDefaultManaCost() {
        return 0;
    }

    @NotNull
    @Override
    protected Set<AbstractAugment> getCompatibleAugments() {
        return Set.of();
    }

    @Override
    public Integer getTypeIndex() {
        return 7;
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!shouldResolveOnEntity(rayTraceResult, world)) spellContext.setCanceled(true, CancelReason.FILTER_FAILED);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!shouldResolveOnBlock(rayTraceResult, world)) spellContext.setCanceled(true, CancelReason.FILTER_FAILED);
    }


    @Override
    public DocAssets.BlitInfo getTypeIcon() {
        return DocAssets.NA_ICON;
    }
}
