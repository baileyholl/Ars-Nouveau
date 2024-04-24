package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.event.timed.IRewindable;
import com.hollingsworth.arsnouveau.common.event.timed.RewindEvent;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketClientRewindEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class EffectRewind extends AbstractEffect {
    public static EffectRewind INSTANCE = new EffectRewind();

    public EffectRewind() {
        super("rewind", "");
    }

    public EffectRewind(ResourceLocation tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        super.onResolveEntity(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        if(rayTraceResult.getEntity() instanceof IRewindable rewindable){
            if(!rewindable.isRewinding()){
                EventQueue.getServerInstance().addEvent(new RewindEvent(rayTraceResult.getEntity(), 100, spellContext));
                if(rewindable instanceof Player player){
                    Networking.sendToNearby(world, player, new PacketClientRewindEffect(100, player));
                }
            }
        }
    }

    public static boolean shouldAllowMovement(IRewindable rewindable){
        return !rewindable.isRewinding();
    }

    public static boolean shouldRecordData(Entity entity, IRewindable rewindable){
        if(entity.level.isClientSide && !(entity instanceof Player)){
            return false;
        }
        return !rewindable.isRewinding();
    }

    @Override
    protected int getDefaultManaCost() {
        return 0;
    }

    @Override
    protected @NotNull Set<AbstractAugment> getCompatibleAugments() {
        return new HashSet<>();
    }

}
