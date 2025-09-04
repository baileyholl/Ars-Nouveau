package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityDummy;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class EffectSummonDecoy extends AbstractEffect {
    public static EffectSummonDecoy INSTANCE = new EffectSummonDecoy();

    private EffectSummonDecoy() {
        super(GlyphLib.EffectDecoyID, "Summon Decoy");
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (canSummon(shooter)) {
            Vec3 pos = safelyGetHitPos(rayTraceResult);
            EntityDummy dummy = new EntityDummy(world);
            dummy.ticksLeft = (int) (20 * (GENERIC_INT.get() + spellStats.getDurationMultiplier() * EXTEND_TIME.get()));
            dummy.setPos(pos.x, pos.y + 1, pos.z);
            summonLivingEntity(rayTraceResult, world, shooter, spellStats, spellContext, resolver, dummy);
            world.getEntitiesOfClass(Mob.class, dummy.getBoundingBox().inflate(20, 10, 20)).forEach(l -> l.setTarget(dummy));
            applySummoningSickness(shooter, 1);
        }
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addExtendTimeConfig(builder, 15);
        addGenericInt(builder, 30, "Base duration in seconds", "duration");
    }

    @Override
    public int getDefaultManaCost() {
        return 200;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
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
        return "Summons a decoy of yourself. Upon summoning, the decoy will attract any nearby mobs to attack it.";
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.CONJURATION);
    }
}
