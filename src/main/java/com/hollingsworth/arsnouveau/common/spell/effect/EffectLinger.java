package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityLingeringSpell;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.validation.ContextSpellValidator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectLinger extends AbstractEffect {
    public static EffectLinger INSTANCE = new EffectLinger();

    private EffectLinger() {
        super(GlyphLib.EffectLingerID, "Linger");
        ContextSpellValidator.RegisterContextCreator(this);
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        Vec3 hit = safelyGetHitPos(rayTraceResult);
        EntityLingeringSpell entityLingeringSpell = new EntityLingeringSpell(world, shooter);
        //spellContext.setCanceled(true);
        if (spellContext.getCurrentIndex() >= spellContext.getSpell().recipe.size())
            return;
        //Spell newSpell = spellContext.getRemainingSpell();
        SpellContext newContext = resolver.spellContext.popContext(true);
        entityLingeringSpell.setAoe((float) spellStats.getAoeMultiplier());
        entityLingeringSpell.setSensitive(spellStats.isSensitive());
        entityLingeringSpell.setAccelerates((int) spellStats.getAccMultiplier());
        entityLingeringSpell.extendedTime = spellStats.getDurationMultiplier();
        entityLingeringSpell.setShouldFall(!spellStats.hasBuff(AugmentDampen.INSTANCE));
        entityLingeringSpell.spellResolver = new SpellResolver(newContext);
        entityLingeringSpell.setPos(hit.x, hit.y, hit.z);
        entityLingeringSpell.setColor(spellContext.getColors());
        world.addFreshEntity(entityLingeringSpell);
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
        //PER_SPELL_LIMIT = builder.comment("The maximum number of times this glyph may appear in a single spell").defineInRange("per_spell_limit", 1, 1, 1);
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @Override
    protected void addDefaultInvalidNestings(Set<ResourceLocation> defaults) {
        defaults.add(EffectLinger.INSTANCE.getRegistryName());
    }
}
