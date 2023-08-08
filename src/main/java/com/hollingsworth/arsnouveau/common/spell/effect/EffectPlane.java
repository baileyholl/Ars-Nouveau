package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectPlane extends AbstractEffect implements IContextManipulator {

    public static EffectPlane INSTANCE = new EffectPlane();

    private EffectPlane() {
        super(GlyphLib.EffectPlaneID, "Plane");
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

        int ceilAoe = (int) Math.ceil(spellStats.getAoeMultiplier()) + 1;
        int floorAoe = (int) Math.floor(spellStats.getAoeMultiplier()) + 1;

        if(spellStats.hasBuff(AugmentSensitive.INSTANCE)) {
            for (BlockPos p : BlockPos.betweenClosed(blockPos.east(ceilAoe).north(floorAoe), blockPos.west(floorAoe).south(ceilAoe))) {
                resolver.getNewResolver(newContext.clone()).onResolveEffect(world, new BlockHitResult(new Vec3(p.getX(), p.getY(), p.getZ()), Direction.UP, p, false));
            }
        }
        else{
            for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, new AABB(blockPos).inflate(floorAoe,3,floorAoe))) {
                resolver.getNewResolver(newContext.clone()).onResolveEffect(world, new EntityHitResult(entity));
            }
        }

        //update spell context past this manipulator
        spellContext.setPostContext();
    }


    @Override
    public String getBookDescription() {
        return "Effects entities in a large plane. Not compatible with other spread effects like Wall or Linger.";
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
