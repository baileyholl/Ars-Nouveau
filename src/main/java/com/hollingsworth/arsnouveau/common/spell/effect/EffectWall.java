package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityWallSpell;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

import static net.minecraft.core.Direction.DOWN;
import static net.minecraft.core.Direction.UP;

public class EffectWall extends AbstractEffect {
    public static EffectWall INSTANCE = new EffectWall();

    private EffectWall() {
        super(GlyphLib.EffectWallId, "Wall");
        EffectReset.RESET_LIMITS.add(this);
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Vec3 hit = safelyGetHitPos(rayTraceResult);
        EntityWallSpell entityWallSpell = new EntityWallSpell(world, shooter);
        if (spellContext.getCurrentIndex() >= spellContext.getSpell().size())
            return;

        SpellContext newContext = spellContext.makeChildContext();
        spellContext.setCanceled(true);


        entityWallSpell.setAoe((float) spellStats.getAoeMultiplier());
        entityWallSpell.setSensitive(spellStats.isSensitive());
        entityWallSpell.setAccelerates((int) spellStats.getAccMultiplier());
        entityWallSpell.extendedTime = spellStats.getDurationMultiplier();
        entityWallSpell.setShouldFall(!spellStats.hasBuff(AugmentDampen.INSTANCE));


        Direction facingDirection = spellContext.getCaster().getFacingDirection();
        entityWallSpell.setDirection(facingDirection == UP || facingDirection == DOWN ? facingDirection : facingDirection.getClockWise());
        entityWallSpell.setResolver(resolver.getNewResolver(newContext));
        entityWallSpell.setPos(hit.x, hit.y, hit.z);
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
    protected void addDefaultInvalidCombos(Set<ResourceLocation> defaults) {
        defaults.add(EffectLinger.INSTANCE.getRegistryName());
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        super.addDefaultAugmentLimits(defaults);
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 1);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentAOE.INSTANCE, "Increases the target area.");
        map.put(AugmentAccelerate.INSTANCE, "Casts spells faster.");
        map.put(AugmentDecelerate.INSTANCE, "Casts spells slower.");
        map.put(AugmentExtendTime.INSTANCE, "Increases the duration of the effect.");
        map.put(AugmentDurationDown.INSTANCE, "Decreases the duration of the effect.");
        map.put(AugmentDampen.INSTANCE, "Ignores gravity.");
        map.put(AugmentSensitive.INSTANCE, "Targets blocks instead of entities.");
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", 1, 1, 1);
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
