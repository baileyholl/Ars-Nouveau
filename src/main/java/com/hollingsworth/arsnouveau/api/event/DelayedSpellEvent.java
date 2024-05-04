package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

public class DelayedSpellEvent implements ITimedEvent {
    private int duration;
    private final SpellResolver resolver;
    private final HitResult result;
    private final Level world;

    @Deprecated(forRemoval = true)
    public DelayedSpellEvent(int delay, Spell spell, HitResult result, Level world, @Nullable LivingEntity shooter, SpellContext resolver) {
        this(delay, result, world, new SpellResolver(resolver));
    }

    public DelayedSpellEvent(int delay, HitResult result, Level world, SpellResolver resolver) {
        this.duration = delay;
        this.result = result;
        this.world = world;
        this.resolver = resolver;
    }

    @Override
    public void tick(boolean serverSide) {
        duration--;
        if (duration <= 0 && serverSide) {
            resolveSpell();
        } else if (!serverSide && result != null) {
            BlockPos hitVec = result instanceof EntityHitResult ? ((EntityHitResult) result).getEntity().blockPosition() : BlockPos.containing(result.getLocation());
            ParticleUtil.spawnTouch((ClientLevel) world, hitVec, resolver.spellContext.getColors());
        }
    }

    public void resolveSpell() {
        if (world == null)
            return;
        if (result instanceof EntityHitResult ehr && ehr.getEntity().isRemoved()) {
            return;
        }
        this.resolver.spellContext.setCanceled(false, null);
        resolver.resume(world);
    }

    @Override
    public boolean isExpired() {
        return duration <= 0 || world == null;
    }
}
