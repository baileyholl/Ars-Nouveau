package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityWallSpell;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectWall extends AbstractEffect {
    public static EffectWall INSTANCE = new EffectWall();

    private EffectWall() {
        super(GlyphLib.EffectWallId, "Wall");
        invalidCombinations.add(EffectLinger.INSTANCE.getRegistryName());
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        Vec3 hit = safelyGetHitPos(rayTraceResult);
        EntityWallSpell entityWallSpell = new EntityWallSpell(world, shooter);
        spellContext.setCanceled(true);
        if (spellContext.getCurrentIndex() >= spellContext.getSpell().recipe.size())
            return;

        Spell newSpell = spellContext.getRemainingSpell();
        SpellContext newContext = spellContext.clone().withSpell(newSpell);

        entityWallSpell.setAoe((float) spellStats.getAoeMultiplier());
        entityWallSpell.setSensitive(spellStats.hasBuff(AugmentSensitive.INSTANCE));
        entityWallSpell.setAccelerates((int) spellStats.getAccMultiplier());
        entityWallSpell.extendedTime = spellStats.getDurationMultiplier();
        entityWallSpell.setShouldFall(!spellStats.hasBuff(AugmentDampen.INSTANCE));


        Direction facingDirection = spellContext.getCaster().getFacingDirection();
        entityWallSpell.setDirection(facingDirection.getClockWise());
        entityWallSpell.spellResolver = new SpellResolver(newContext);
        entityWallSpell.setPos(hit.x, hit.y, hit.z);
        entityWallSpell.setColor(spellContext.getColors());
        world.addFreshEntity(entityWallSpell);
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

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", 1, 1, 1);
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
