package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectWall extends AbstractEffect implements IContextManipulator {

    public static EffectWall INSTANCE = new EffectWall();

    private EffectWall() {
        super(GlyphLib.EffectWallID, "Wall");
        invalidNestings.add(EffectPlane.INSTANCE.getRegistryName());
        invalidNestings.add(this.getRegistryName());
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (spellContext.getCurrentIndex() >= spellContext.getSpell().recipe.size())
            return;

        SpellContext newContext = resolver.spellContext.clone().withSpell(spellContext.getInContextSpell());

        BlockPos blockPos;
        if(rayTraceResult instanceof BlockHitResult blockHitResult) {
            blockPos = blockHitResult.getBlockPos();
        }
        else if(rayTraceResult instanceof EntityHitResult entityHitResult){
            blockPos = entityHitResult.getEntity().blockPosition();
        }
        else{
            //update spell context past this manipulator
            spellContext.setPostContext();
            return;
        }
        Direction facingDirection = spellContext.getCaster().getFacingDirection().getClockWise();

        float aoe = (float)spellStats.getAoeMultiplier() + 3;
        int flatAoe = Math.round(aoe);
        BlockPos start = blockPos.offset(flatAoe * facingDirection.getStepX(), 0, flatAoe * facingDirection.getStepZ());
        BlockPos end = blockPos.offset(-flatAoe  * facingDirection.getStepX(), flatAoe, -flatAoe * facingDirection.getStepZ());


        if(spellStats.hasBuff(AugmentSensitive.INSTANCE)) {
            for (BlockPos p : BlockPos.betweenClosed(start,end)) {
                resolver.getNewResolver(newContext.clone()).onResolveEffect(world, new BlockHitResult(new Vec3(p.getX(), p.getY(), p.getZ()), Direction.UP, p, false));
            }
        }
        else{
            float growthFactor = 1;
            AABB aabb = new AABB(start, end);
            if(aabb.maxX == aabb.minX){
                aabb = aabb.inflate(growthFactor, 0, 0);
            }
            if(aabb.maxY == aabb.minY){
                aabb = aabb.inflate(0, growthFactor, 0);
            }
            if(aabb.maxZ == aabb.minZ){
                aabb = aabb.inflate(0, 0, growthFactor);
            }
            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, aabb)) {
                resolver.getNewResolver(newContext.clone()).onResolveEffect(world, new EntityHitResult(entity));
            }
        }

        //update spell context past this manipulator
        spellContext.setPostContext();
    }


    @Override
    public String getBookDescription() {
        return "Effects entities in a large wall. Not compatible with other spread effects like Plane or Linger.";
    }

    @Override
    public int getDefaultManaCost() {
        return 400;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentSensitive.INSTANCE, AugmentAOE.INSTANCE);
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @Override
    public boolean isEscapable() {
        return true;
    }
}
