package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.SummonHorse;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class EffectSummonSteed extends AbstractEffect {
    public static EffectSummonSteed INSTANCE = new EffectSummonSteed();


    private EffectSummonSteed() {
        super(GlyphLib.EffectSummonSteedID, "Summon Steed");
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {

        if (!canSummon(shooter))
            return;

        int ticks = (int) (20 * (GENERIC_INT.get() + EXTEND_TIME.get() * spellStats.getDurationMultiplier()));
        if (ticks <= 0) ticks = 20; //leave at least one second of life, since summon sick doesn't depend on it and short life horses can be useful (?)
        Vec3 hit = rayTraceResult.getLocation();
        for (int i = 0; i < 1 + Math.round(spellStats.getAoeMultiplier()); i++) {
            SummonHorse horse = new SummonHorse(ModEntities.SUMMON_HORSE.get(), world);
            horse.setPos(hit.x(), hit.y(), hit.z());
            horse.ticksLeft = ticks;
            horse.tameWithName((Player) shooter);
            horse.getHorseInventory().setItem(0, new ItemStack(Items.SADDLE));
            horse.setOwnerID(shooter.getUUID());
            horse.setDropChance(EquipmentSlot.CHEST, 0.0F);
            summonLivingEntity(rayTraceResult, world, shooter, spellStats, spellContext, resolver, horse);
        }
        applySummoningSickness(shooter, 30 * 20);
    }


    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addExtendTimeConfig(builder, 120);
        addGenericInt(builder, 300, "Base duration in seconds", "duration");
    }

    @Override
    public int getDefaultManaCost() {
        return 100;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentAOE.INSTANCE
        );
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addSummonAugmentDescriptions(map);
        map.put(AugmentAOE.INSTANCE, "Increases the number of horses summoned.");
    }

    @Override
    public String getBookDescription() {
        return "Summons a saddled horse that will vanish after a few minutes. AOE will increase the amount summoned, while Extend Time will increase the duration of the summon. Applies Summoning Sickness to the caster, and cannot be cast while afflicted by this Sickness.";
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.CONJURATION);
    }
}
