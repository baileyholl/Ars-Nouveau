package com.hollingsworth.arsnouveau.api.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * This interface is intended to set a common standard between addons to make and use glyphs that acts as filters. The main mod never checks for filters.
 */
public interface IFilter {

    /**
     * Whether the filter should allow the block hit
     */
    boolean shouldResolveOnBlock(BlockHitResult target, Level level);

    /**
     * Default method that wraps the position/direction to call the actual method
     */
    default boolean shouldResolveOnBlock(BlockPos pos, Direction direction, Level level) {
        return shouldResolveOnBlock(
                new BlockHitResult(new Vec3(pos.getX(), pos.getY(), pos.getZ()), direction, pos, false)
                , level);
    }

    /**
     * Whether the filter should allow the entity hit
     */
    boolean shouldResolveOnEntity(EntityHitResult target, Level level);

    /**
     * Default method that wraps an entity to call the actual method
     */
    default boolean shouldResolveOnEntity(Entity entity, Level level) {
        return shouldResolveOnEntity(new EntityHitResult(entity), level);
    }

    /**
     * @param rayTraceResult block or entity about to be affected by the spell
     * @return true if the glyph filter pass, false if the filter blocks
     */
    default boolean shouldAffect(HitResult rayTraceResult, Level level) {
        if (rayTraceResult instanceof BlockHitResult block) return shouldResolveOnBlock(block, level);
        else if (rayTraceResult instanceof EntityHitResult entity) return shouldResolveOnEntity(entity, level);
        else return false;
    }

}
