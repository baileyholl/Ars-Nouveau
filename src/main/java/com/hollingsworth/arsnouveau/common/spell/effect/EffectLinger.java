package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityLingeringSpell;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class EffectLinger extends AbstractEffect implements IContextManipulator {
    public static EffectLinger INSTANCE = new EffectLinger();

    private EffectLinger() {
        super(GlyphLib.EffectLingerID, "Linger");
        invalidNestings.add(this.getRegistryName());
        invalidNestings.add(EffectPlane.INSTANCE.getRegistryName());
        invalidNestings.add(EffectWall.INSTANCE.getRegistryName());
        invalidNestings.add(EffectBurst.INSTANCE.getRegistryName());
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        Vec3 hit = safelyGetHitPos(rayTraceResult);
        EntityLingeringSpell entityLingeringSpell = new EntityLingeringSpell(world, shooter);
        if (spellContext.getCurrentIndex() >= spellContext.getSpell().recipe.size())
            return;

        Spell newSpell = spellContext.getInContextSpell();



        SpellContext newContext = spellContext.clone().withSpell(newSpell);
        entityLingeringSpell.setAccelerates((int) spellStats.getAccMultiplier());
        entityLingeringSpell.extendedTime = spellStats.getDurationMultiplier();
        entityLingeringSpell.setShouldFall(!spellStats.hasBuff(AugmentDampen.INSTANCE));
        entityLingeringSpell.spellResolver = new SpellResolver(newContext);
        entityLingeringSpell.setPos(hit.x, hit.y, hit.z);
        entityLingeringSpell.setColor(spellContext.getColors());
        world.addFreshEntity(entityLingeringSpell);

        //update spell context past this manipulator
        spellContext.setPostContext();
    }


    @Override
    public String getBookDescription() {
        return "Creates a small lingering cloud that applies spells on nearby entities for a short time. Applying Sensitive will make this spell target blocks instead. Accelerate will cast spells faster, Dampen will ignore gravity, and Extend Time will increase the duration.";
    }

    @Override
    public int getDefaultManaCost() {
        return 300;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf( AugmentAccelerate.INSTANCE, AugmentDecelerate.INSTANCE, AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentDampen.INSTANCE);
    }

    @Override
    protected void buildAugmentLimitsConfig(ForgeConfigSpec.Builder builder, Map<ResourceLocation, Integer> defaults) {
        super.buildAugmentLimitsConfig(builder, defaults);
        defaults.put(AugmentAccelerate.INSTANCE.getRegistryName(),2);
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
