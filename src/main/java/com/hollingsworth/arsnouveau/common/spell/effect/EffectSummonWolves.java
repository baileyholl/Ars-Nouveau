package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.SummonWolf;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class EffectSummonWolves extends AbstractEffect {
    public static EffectSummonWolves INSTANCE = new EffectSummonWolves();

    private EffectSummonWolves() {
        super(GlyphLib.EffectSummonWolvesID, "Summon Wolves");
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!canSummon(shooter))
            return;
        Vec3 hit = rayTraceResult.getLocation();
        int ticks = (int) (20 * (GENERIC_INT.get() + EXTEND_TIME.get() * spellStats.getDurationMultiplier()));
        if (ticks <= 0) return;
        for (int i = 0; i < 2; i++) {
            SummonWolf wolf = new SummonWolf(ModEntities.SUMMON_WOLF.get(), world);
            wolf.ticksLeft = ticks;
            wolf.setPos(hit.x(), hit.y(), hit.z());
            wolf.setTarget(shooter.getLastHurtMob());
            wolf.setAggressive(true);
            wolf.setTame(true, false);
            wolf.tame((Player) shooter);
            summonLivingEntity(rayTraceResult, world, shooter, spellStats, spellContext, resolver, wolf);
        }
        applySummoningSickness(shooter, ticks);
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericInt(builder, 60, "Base duration in seconds", "duration");
        addExtendTimeConfig(builder, 60);
    }

    @Override
    public int getDefaultManaCost() {
        return 100;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        // SummonEvent captures augments, but no uses of that field were found
        return getSummonAugments();
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addSummonAugmentDescriptions(map);
    }

    @Override
    public String getBookDescription() {
        return "Summons two wolves that will fight with you. Extend Time will increase the amount of time on the summons. Applies Summoning Sickness to the caster, preventing other summoning magic.";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.CONJURATION);
    }
}
