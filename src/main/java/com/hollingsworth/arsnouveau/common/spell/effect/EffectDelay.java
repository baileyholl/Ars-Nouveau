package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.event.DelayedSpellEvent;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketClientDelayEffect;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentRandomize;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class EffectDelay extends AbstractEffect {
    public static EffectDelay INSTANCE = new EffectDelay();

    private EffectDelay() {
        super(GlyphLib.EffectDelayID, "Delay");
    }

    public void sendPacket(Level world, HitResult rayTraceResult, @Nullable LivingEntity shooter, SpellContext spellContext, SpellStats spellStats, BlockHitResult blockResult, Entity hitEntity, SpellResolver spellResolver) {
        if (spellContext.getCurrentIndex() >= spellContext.getSpell().size())
            return;
        int duration = GENERIC_INT.get(); // base duration
        int increasedTime = EXTEND_TIME.get() * 20 * spellStats.getBuffCount(AugmentExtendTime.INSTANCE);
        int decreasedTime = DURATION_DOWN_TIME.get() * spellStats.getBuffCount(AugmentDurationDown.INSTANCE);
        duration = duration + increasedTime - decreasedTime;
        if (spellStats.isRandomized()) {
            double randomize = spellStats.getBuffCount(AugmentRandomize.INSTANCE) * RANDOMIZE_CHANCE.get();
            duration = world.random.nextIntBetweenInclusive((int) (duration * (1 - randomize)), (int) (duration * (1 + randomize)));
        }
        var delayEvent = new DelayedSpellEvent(duration, rayTraceResult, world, spellResolver);
        spellContext.delay(delayEvent);

        EventQueue.getServerInstance().addEvent(delayEvent);
        Networking.sendToNearbyClient(world, BlockPos.containing(safelyGetHitPos(rayTraceResult)),
                new PacketClientDelayEffect(duration, shooter, spellContext.getSpell(), spellContext, blockResult, hitEntity));
    }


    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        sendPacket(world, rayTraceResult, shooter, spellContext, spellStats, rayTraceResult, null, resolver);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        sendPacket(world, rayTraceResult, shooter, spellContext, spellStats, null, rayTraceResult.getEntity(), resolver);
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addExtendTimeConfig(builder, 1);
        addDurationDownConfig(builder, 10);
        addGenericInt(builder, 20, "The base duration of the delay effect in ticks.", "base_duration");
        addRandomizeConfig(builder, 0.25f);
    }

    @Override
    public int getDefaultManaCost() {
        return 0;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentRandomize.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Delays the resolution of effects placed to the right of this spell for a few moments. The delay may be increased with the Extend Time augment, or decreased with Duration Down.";
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentExtendTime.INSTANCE, "Increases the duration of the delay.");
        map.put(AugmentDurationDown.INSTANCE, "Decreases the duration of the delay.");
        map.put(AugmentRandomize.INSTANCE, "Randomizes the duration of the delay, increasing or decreasing it by 25% of the base duration.");
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
