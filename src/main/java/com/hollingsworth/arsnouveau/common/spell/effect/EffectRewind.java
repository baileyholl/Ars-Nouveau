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
import org.jetbrains.annotations.NotNull;

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
        if(rayTraceResult.getEntity() instanceof IRewindable rewindable && !rewindable.isRewinding()){
            EventQueue.getServerInstance().addEvent(new RewindEvent(rayTraceResult.getEntity(), world.getGameTime(), 100, spellContext));
            if(rewindable instanceof Player player){
                Networking.sendToNearby(world, player, new PacketClientRewindEffect(100, player));
            }
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolveBlock(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        EventQueue.getServerInstance().addEvent(new RewindEvent(null, world.getGameTime(), 100, spellContext));
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
