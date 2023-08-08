package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityLingeringWall;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectLingeringWall extends AbstractEffect implements IContextManipulator {
    public static EffectLingeringWall INSTANCE = new EffectLingeringWall();

    private EffectLingeringWall() {
        super(GlyphLib.EffectLingeringWallID, "Lingering Wall");
        invalidNestings.add(EffectPlane.INSTANCE.getRegistryName());
        invalidNestings.add(EffectWall.INSTANCE.getRegistryName());
        invalidNestings.add(EffectBurst.INSTANCE.getRegistryName());
        invalidNestings.add(EffectLingeringPlane.INSTANCE.getRegistryName());
        invalidNestings.add(this.getRegistryName());
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        Vec3 hit = safelyGetHitPos(rayTraceResult);
        EntityLingeringWall entityWallSpell = new EntityLingeringWall(world, shooter);
        if (spellContext.getCurrentIndex() >= spellContext.getSpell().recipe.size())
            return;

        Spell newSpell = spellContext.getInContextSpell();
        SpellContext newContext = spellContext.clone().withSpell(newSpell);

        entityWallSpell.setAoe((float) spellStats.getAoeMultiplier());
        entityWallSpell.setSensitive(spellStats.isSensitive());
        entityWallSpell.setAccelerates((int) spellStats.getAccMultiplier());
        entityWallSpell.extendedTime = spellStats.getDurationMultiplier();
        entityWallSpell.setShouldFall(!spellStats.hasBuff(AugmentDampen.INSTANCE));


        Direction facingDirection = spellContext.getCaster().getFacingDirection();
        entityWallSpell.setDirection(facingDirection.getClockWise());
        entityWallSpell.spellResolver = new SpellResolver(newContext);
        entityWallSpell.setPos(hit.x, hit.y, hit.z);
        entityWallSpell.setColor(spellContext.getColors());
        world.addFreshEntity(entityWallSpell);

        //update spell context past this manipulator
        spellContext.setPostContext();
    }


    @Override
    public String getBookDescription() {
        return "Creates a lingering wall that applies spells on nearby entities for a short time. Applying Sensitive will make this spell target blocks instead. AOE will expand the effective range, Accelerate will cast spells faster, Dampen will ignore gravity, and Extend Time will increase the duration.";
    }

    @Override
    public int getDefaultManaCost() {
        return 500;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentSensitive.INSTANCE, AugmentAOE.INSTANCE, AugmentAccelerate.INSTANCE, AugmentDecelerate.INSTANCE, AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentDampen.INSTANCE);
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
