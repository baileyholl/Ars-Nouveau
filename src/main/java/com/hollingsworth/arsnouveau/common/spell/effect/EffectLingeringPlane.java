package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityLingeringPlane;
import com.hollingsworth.arsnouveau.common.entity.EntityLingeringSpell;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectLingeringPlane  extends AbstractEffect {
    public static EffectLingeringPlane INSTANCE = new EffectLingeringPlane();

    private EffectLingeringPlane() {
        super(GlyphLib.EffectLingeringPlaneID, "Lingering Plane");
        invalidNestings.add(EffectPlane.INSTANCE.getRegistryName());
        invalidNestings.add(EffectWall.INSTANCE.getRegistryName());
        invalidNestings.add(EffectBurst.INSTANCE.getRegistryName());
        invalidNestings.add(this.getRegistryName());
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        Vec3 hit = safelyGetHitPos(rayTraceResult);
        EntityLingeringPlane entityLingeringPlane = new EntityLingeringPlane(world, shooter);
        spellContext.setCanceled(true);
        if (spellContext.getCurrentIndex() >= spellContext.getSpell().recipe.size())
            return;
        Spell newSpell = spellContext.getRemainingSpell();
        SpellContext newContext = spellContext.clone().withSpell(newSpell);
        entityLingeringPlane.setAoe((float) spellStats.getAoeMultiplier());
        entityLingeringPlane.setSensitive(spellStats.isSensitive());
        entityLingeringPlane.setAccelerates((int) spellStats.getAccMultiplier());
        entityLingeringPlane.extendedTime = spellStats.getDurationMultiplier();
        entityLingeringPlane.setShouldFall(!spellStats.hasBuff(AugmentDampen.INSTANCE));
        entityLingeringPlane.spellResolver = new SpellResolver(newContext);
        entityLingeringPlane.setPos(hit.x, hit.y, hit.z);
        entityLingeringPlane.setColor(spellContext.getColors());
        world.addFreshEntity(entityLingeringPlane);
    }


    @Override
    public String getBookDescription() {
        return "Creates a lingering field that applies spells on nearby entities for a short time. Applying Sensitive will make this spell target blocks instead. AOE will expand the effective range, Accelerate will cast spells faster, Dampen will ignore gravity, and Extend Time will increase the duration.";
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
