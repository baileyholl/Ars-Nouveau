package com.hollingsworth.arsnouveau.api.spell;

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
public abstract class AbstractFilter extends AbstractEffect implements IFilter, IContextManipulator {

    public AbstractFilter(ResourceLocation registryName, String name) {
        super(registryName, name);
    }

    @Override
    public boolean isEscapable() {
        return true;
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
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        spellContext.prepareContextForManipulation();
        SpellContext yesContext = spellContext.getInnerContext();
        SpellContext noContext = spellContext.getConditionalInnerContext();

        if (shouldResolveOnEntity(rayTraceResult)){
            resolver.getNewResolver(yesContext).onResolveEffect(world,rayTraceResult);
        }
        else{
            resolver.getNewResolver(noContext).onResolveEffect(world,rayTraceResult);
        }
        spellContext.setPostContext();
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        spellContext.prepareContextForManipulation();
        SpellContext yesContext = spellContext.getInnerContext();
        SpellContext noContext = spellContext.getConditionalInnerContext();

        if (shouldResolveOnBlock(rayTraceResult)){
            resolver.getNewResolver(yesContext).onResolveEffect(world,rayTraceResult);
        }
        else{
            resolver.getNewResolver(noContext).onResolveEffect(world,rayTraceResult);
        }
        spellContext.setPostContext();
    }

}
