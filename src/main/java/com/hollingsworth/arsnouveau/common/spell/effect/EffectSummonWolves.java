package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.SummonWolf;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.WolfVariants;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
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
        if (!canSummon(shooter) || shooter == null)
            return;
        Vec3 hit = rayTraceResult.getLocation();
        int ticks = (int) (20 * (GENERIC_INT.get() + EXTEND_TIME.get() * spellStats.getDurationMultiplier()));
        if (ticks <= 0) return;
        Holder<Biome> holder = world.getBiome(BlockPos.containing(rayTraceResult.getLocation()));
        var wolfVariant = WolfVariants.getSpawnVariant(world.registryAccess(), holder);

        for (int i = 0; i < 2 + spellStats.getBuffCount(AugmentSplit.INSTANCE); i++) {
            SummonWolf wolf = new SummonWolf(ModEntities.SUMMON_WOLF.get(), world);
            wolf.ticksLeft = ticks;
            wolf.setVariant(wolfVariant);
            wolf.setPos(hit.x(), hit.y(), hit.z());
            wolf.setTarget(shooter.getLastHurtMob() == null ? shooter.getLastHurtByMob() : shooter.getLastHurtMob());
            wolf.setAggressive(true);
            wolf.setTame(true, false);
            wolf.tame((Player) shooter);
            if (spellStats.getAmpMultiplier() > 0) {
                wolf.setBodyArmorItem(Items.WOLF_ARMOR.getDefaultInstance());
                wolf.setDropChance(EquipmentSlot.BODY, 0.0F);
            }
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
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentSplit.INSTANCE, AugmentAmplify.INSTANCE);
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
