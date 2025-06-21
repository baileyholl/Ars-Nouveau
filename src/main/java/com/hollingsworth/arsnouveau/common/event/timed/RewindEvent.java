package com.hollingsworth.arsnouveau.common.event.timed;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.ITimedEvent;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.spell.rewind.IRewindCallback;
import com.hollingsworth.arsnouveau.common.spell.rewind.RewindAttachment;
import com.hollingsworth.arsnouveau.common.spell.rewind.RewindEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import javax.annotation.Nullable;
import java.util.List;

public class RewindEvent implements ITimedEvent {

    public @Nullable Entity entity;
    public boolean doneRewinding;
    public int rewindTicks;
    public int ticksToRewind;
    public boolean respectsGravity;
    public @Nullable SpellContext context;
    public boolean serverSide;
    public long startGameTime;
    public boolean registeredEvents;

    public RewindEvent(long gameTime, int ticksToRewind, @Nullable SpellContext spellContext) {
        this.startGameTime = gameTime;
        this.ticksToRewind = ticksToRewind;
        this.context = spellContext;
    }

    public RewindEvent(@Nullable Entity entity, long gameTime, int ticksToRewind) {
        this(gameTime, ticksToRewind, null);
        this.entity = entity;
        respectsGravity = entity != null && !entity.isNoGravity();
    }

    public RewindEvent(@Nullable Entity entity, long gameTime, int ticksToRewind, @Nullable SpellContext context) {
        this(entity, gameTime, ticksToRewind);
        this.context = context;
    }

    @Override
    public void tick(boolean serverSide) {
        this.serverSide = serverSide;
        if (!this.registeredEvents && this.serverSide) {
            if (entity instanceof ServerPlayer) {
                NeoForge.EVENT_BUS.addListener(this::onEntityRemoved);
            }
            this.registeredEvents = true;
        }
        long eventGameTime = startGameTime - this.rewindTicks;
        if (entity instanceof IRewindable rewindable) {
            rewindable.setRewinding(true);
            if (!rewindable.getMotions().empty()) {
                RewindEntityData data = rewindable.getMotions().pop();
                data.onRewind(this);
            }
        }
        if (context != null) {
            RewindAttachment rewindAttachment = RewindAttachment.get(context);
            List<IRewindCallback> contextData = rewindAttachment.getForTime(eventGameTime);
            // Lock and prevent adding rewind events for the current tick. Otherwise you get CME or infinte loops
            rewindAttachment.setLockedTime(eventGameTime);
            if (contextData != null) {
                for (IRewindCallback callback : contextData) {
                    callback.onRewind(this);
                }
            }
            rewindAttachment.setLockedTime(-1);
        }
        rewindTicks++;
        if (rewindTicks >= ticksToRewind) {
            stop();
        }
    }

    public void stop() {
        doneRewinding = true;
        if (entity instanceof IRewindable rewindable) {
            rewindable.setRewinding(false);
            entity.setDeltaMovement(Vec3.ZERO);
            this.removeWeightlessness();
        }
    }

    @Override
    public void onServerStopping() {
        this.removeWeightlessness();
    }

    public void onEntityRemoved(PlayerEvent.PlayerLoggedOutEvent event) {
        if (entity == null) {
            NeoForge.EVENT_BUS.unregister(this);
            return;
        }

        if (entity == event.getEntity()) {
            this.removeWeightlessness();
            NeoForge.EVENT_BUS.unregister(this);
        }
    }

    public void removeWeightlessness() {
        if (!respectsGravity) {
            return;
        }

        if (entity instanceof LivingEntity le) {
            var weight = le.getAttribute(PerkAttributes.WEIGHT);
            if (weight != null) {
                weight.removeModifier(ArsNouveau.prefix("rewind"));
            }
        } else if (entity != null) {
            entity.setNoGravity(false);
        }
    }

    @Override
    public boolean isExpired() {
        return doneRewinding;
    }
}
