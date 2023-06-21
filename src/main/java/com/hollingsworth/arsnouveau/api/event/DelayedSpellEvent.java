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
    private final Spell spell;
    private final SpellContext context;
    private final HitResult result;
    private final Level world;
    private final @Nullable LivingEntity shooter;

    public DelayedSpellEvent(int delay, Spell spell, HitResult result, Level world, @Nullable LivingEntity shooter, SpellContext context) {
        this.duration = delay;
        this.spell = spell;
        this.result = result;
        this.world = world;
        this.shooter = shooter;
        this.context = context;
    }

    @Override
    public void tick(boolean serverSide) {
        duration--;
        if (duration <= 0 && serverSide) {
            resolveSpell();
        } else if (!serverSide && result != null) {
            BlockPos hitVec = result instanceof EntityHitResult ? ((EntityHitResult) result).getEntity().blockPosition() : BlockPos.containing(result.getLocation());
            ParticleUtil.spawnTouch((ClientLevel) world, hitVec, context.getColors());
        }
    }

    public void resolveSpell() {
        if (world == null || result instanceof EntityHitResult ehr && ehr.getEntity().isRemoved())
            return;
        //Optional TODO: Resolve the spell in the last position of the entity if it was removed during the delay, instead of cancelling. This would allow to keep the old behaviour without dupes.
        SpellResolver resolver = new SpellResolver(context);
        resolver.onResolveEffect(world, result);
    }

    @Override
    public boolean isExpired() {
        return duration <= 0 || world == null;
    }
}
