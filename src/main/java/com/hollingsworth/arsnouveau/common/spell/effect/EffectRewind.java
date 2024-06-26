package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.event.DelayedSpellEvent;
import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.event.timed.IRewindable;
import com.hollingsworth.arsnouveau.common.event.timed.RewindEvent;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketClientRewindEffect;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDurationDown;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class EffectRewind extends AbstractEffect {
    public static EffectRewind INSTANCE = new EffectRewind();

    public EffectRewind() {
        super("rewind", "Rewind");
    }

    public EffectRewind(ResourceLocation tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolveEntity(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        int ticksToRewind = getRewindTicks(spellStats);
        Entity entity = rayTraceResult.getEntity();
        if(!entity.getType().is(EntityTags.REWIND_BLACKLIST) && entity instanceof IRewindable rewindable && !rewindable.isRewinding()){
            var delayEvent = new DelayedSpellEvent(ticksToRewind, rayTraceResult, world, resolver);
            spellContext.delay(delayEvent);
            EventQueue.getServerInstance().addEvent(new RewindEvent(entity, world.getGameTime(), ticksToRewind, spellContext));
            if(rewindable instanceof Player player){
                Networking.sendToNearbyClient(world, player, new PacketClientRewindEffect(ticksToRewind, player));
            }

            EventQueue.getServerInstance().addEvent(delayEvent);
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolveBlock(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        int ticksToRewind = getRewindTicks(spellStats);
        var delayEvent = new DelayedSpellEvent(ticksToRewind, rayTraceResult, world, resolver);
        spellContext.delay(delayEvent);
        EventQueue.getServerInstance().addEvent(new RewindEvent(null, world.getGameTime(), ticksToRewind, spellContext));

        EventQueue.getServerInstance().addEvent(delayEvent);
    }

    public int getRewindTicks(SpellStats spellStats){
        double multiplier = spellStats.getDurationMultiplier();
        int ticksToRewind = BASE_REWIND_TIME.get();
        if(multiplier < 0){
            ticksToRewind = (int) (ticksToRewind + EXTEND_TIME.get() * multiplier);
        }else if(multiplier > 0){
            ticksToRewind = (int) (ticksToRewind - DURATION_DOWN_TIME.get() * multiplier);
        }
        return ticksToRewind;
    }

    public static boolean shouldAllowMovement(IRewindable rewindable){
        return !rewindable.isRewinding();
    }

    public static boolean shouldRecordData(Entity entity, IRewindable rewindable){
        if(entity.level.isClientSide && !(entity instanceof Player)){
            return false;
        }
        if(!EffectRewind.INSTANCE.isEnabled() || entity.getType().is(EntityTags.REWIND_BLACKLIST)){
            return false;
        }
        return !rewindable.isRewinding();
    }

    public ModConfigSpec.IntValue BASE_REWIND_TIME;

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericInt(builder, 60, "Max ticks entities should track for motion and health, etc. Note: Entities ANYWHERE are tracking this, setting this to a high value is not recommended for low-spec machines.", "entityRewindTracking");
        BASE_REWIND_TIME = builder.comment("How many ticks should be rewound before augments").defineInRange("baseRewindTime", 40, 1, 60);
        addExtendTimeConfig(builder, 20);
        addDurationDownConfig(builder, 10);
    }

    @Override
    protected void buildAugmentLimitsConfig(ModConfigSpec.Builder builder, Map<ResourceLocation, Integer> defaults) {
        super.buildAugmentLimitsConfig(builder, defaults);
        defaults.put(AugmentExtendTime.INSTANCE.getRegistryName(), 1);
        defaults.put(AugmentDurationDown.INSTANCE.getRegistryName(), 5);
    }

    public int getEntityMaxTrackingTicks(){
        return GENERIC_INT.get();
    }

    @Override
    protected int getDefaultManaCost() {
        return 100;
    }


    @Override
    public String getBookDescription() {
        return "Rewinds an entity back in time to its previous locations and health. Can revert blocks that were moved with spells back into solid blocks. Glyphs that come after Rewind will be cast at the end of the rewind, as if they were Delayed.";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE);
    }

    @Override
    protected @NotNull Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
