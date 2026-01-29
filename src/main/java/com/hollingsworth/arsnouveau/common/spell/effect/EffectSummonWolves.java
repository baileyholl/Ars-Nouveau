package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.entity.SummonWolf;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import com.hollingsworth.arsnouveau.common.util.HolderHelper;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.WolfVariants;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.hollingsworth.arsnouveau.common.spell.effect.EffectWololo.vanillaColors;

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
        int ticks = (int) (20 * (GENERIC_INT.get() * (1 + spellStats.getDurationMultiplier())));
        if (spellStats.hasBuff(AugmentExtendTime.INSTANCE)) ticks = -1; // Infinite duration
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
                var armor = Items.WOLF_ARMOR.getDefaultInstance();
                if (spellStats.getAmpMultiplier() >= 2) {
                    armor.enchant(HolderHelper.unwrap(world, Enchantments.UNBREAKING), (int) (spellStats.getAmpMultiplier() - 1));
                }
                wolf.setBodyArmorItem(Items.WOLF_ARMOR.getDefaultInstance());
                wolf.setDropChance(EquipmentSlot.BODY, 0.0F);
            }
            wolf.getAttribute(Attributes.SCALE).setBaseValue(1.0 + spellStats.getAoeMultiplier() * 0.1);
            ParticleColor spellColor = spellContext.getParticleTimeline(ParticleTimelineRegistry.WOLVES_TIMELINE.get()).getColor();
            if (!Objects.equals(spellColor, ParticleColor.defaultParticleColor())) {
                ParticleColor targetColor = vanillaColors.keySet().stream().min(Comparator.comparingDouble(d -> d.euclideanDistance(spellColor))).orElse(ParticleColor.RED);
                wolf.setCollarColor(((DyeItem) vanillaColors.get(targetColor)).getDyeColor());
            }
            summonLivingEntity(rayTraceResult, world, shooter, spellStats, spellContext, resolver, wolf);
        }
        //applySummoningSickness(shooter, ticks);
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericInt(builder, 60, "Base duration in seconds", "duration");
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 1);
    }

    @Override
    public int getDefaultManaCost() {
        return 100;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentSplit.INSTANCE, AugmentAmplify.INSTANCE, AugmentAOE.INSTANCE);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addSummonAugmentDescriptions(map);
        map.put(AugmentAmplify.INSTANCE, "Gives summoned wolves armor.");
        map.put(AugmentAOE.INSTANCE, "Increases the size of the summoned wolves.");
    }

    @Override
    public String getBookDescription() {
        return "Summons two wolves that will fight with you. Extend Time will increase the amount of time on the summons. Amplify to give them armor, AoE to increase their size. Reserves a chunk of mana while the summon is alive.";
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
