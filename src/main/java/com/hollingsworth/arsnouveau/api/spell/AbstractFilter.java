package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.core.LoggerContext;
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
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        boolean succeeded = shouldResolveOnEntity(rayTraceResult, world);
        SpellContext newContext = spellContext.popContext(succeeded);
        SpellResolver resolver1 = resolver.getNewResolver(newContext.clone().withParent(spellContext));
        resolver1.previousResolver = resolver;
        resolver1.onResolveEffect(world, rayTraceResult);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        boolean succeeded = shouldResolveOnBlock(rayTraceResult, world);
        SpellContext newContext = spellContext.popContext(succeeded);
        SpellResolver resolver1 = resolver.getNewResolver(newContext);
        resolver1.previousResolver = resolver;
        resolver1.onResolveEffect(world, rayTraceResult);
    }

}
