package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public interface IFilter {

    @Deprecated(forRemoval = true)
    boolean shouldResolveOnBlock(BlockHitResult target, Level level);

    @Deprecated(forRemoval = true)
    boolean shouldResolveOnEntity(EntityHitResult target, Level level);

    @Deprecated(forRemoval = true)
    default boolean shouldAffect(HitResult rayTraceResult, Level level) {
        if (rayTraceResult instanceof BlockHitResult block) return shouldResolveOnBlock(block, level);
        else if (rayTraceResult instanceof EntityHitResult entity) return shouldResolveOnEntity(entity, level);
        else return false;
    }

    default boolean shouldResolveOnBlock(BlockHitResult target, Level level, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        return shouldResolveOnBlock(target, level);
    }

    default boolean shouldResolveOnEntity(EntityHitResult target, Level level, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        return shouldResolveOnEntity(target, level);
    }

    default boolean shouldAffect(HitResult rayTraceResult, Level level, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult instanceof BlockHitResult block)
            return shouldResolveOnBlock(block, level, spellStats, spellContext, resolver);
        else if (rayTraceResult instanceof EntityHitResult entity)
            return shouldResolveOnEntity(entity, level, spellStats, spellContext, resolver);
        else return false;
    }

}
