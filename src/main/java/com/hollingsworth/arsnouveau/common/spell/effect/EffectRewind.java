package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.event.timed.IRewindable;
import com.hollingsworth.arsnouveau.common.event.timed.RewindEvent;
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
import net.minecraftforge.common.ForgeConfigSpec;
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
        if(entity instanceof IRewindable rewindable && !rewindable.isRewinding()){
            EventQueue.getServerInstance().addEvent(new RewindEvent(entity, world.getGameTime(), ticksToRewind, spellContext));
            if(rewindable instanceof Player player){
                Networking.sendToNearby(world, player, new PacketClientRewindEffect(ticksToRewind, player));
            }
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolveBlock(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        int ticksToRewind = getRewindTicks(spellStats);
        EventQueue.getServerInstance().addEvent(new RewindEvent(null, world.getGameTime(), ticksToRewind, spellContext));
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
        if(!EffectRewind.INSTANCE.isEnabled()){
            return false;
        }
        return !rewindable.isRewinding();
    }

    public ForgeConfigSpec.IntValue BASE_REWIND_TIME;

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericInt(builder, 60, "Max ticks entities should track for motion and health, etc. Note: Entities ANYWHERE are tracking this, setting this to a high value is not recommended for low-spec machines.", "entityRewindTracking");
        BASE_REWIND_TIME = builder.comment("How many ticks should be rewound before augments").defineInRange("baseRewindTime", 40, 1, 60);
        addExtendTimeTicksConfig(builder, 20);
        addDurationDownConfig(builder, 10);
    }

    @Override
    protected void buildAugmentLimitsConfig(ForgeConfigSpec.Builder builder, Map<ResourceLocation, Integer> defaults) {
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
        return "Rewinds an entity back in time to its previous locations and health. Can revert blocks that were moved with spells back into solid blocks.";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE);
    }

}
