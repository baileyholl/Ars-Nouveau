package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;

public class DelayedSpellEvent implements ITimedEvent {
    private int duration;
    private final SpellContext context;
    private final HitResult result;
    private final Level world;
    private final @Nullable LivingEntity shooter;

    @Deprecated(forRemoval = true)
    public DelayedSpellEvent(int delay, Spell spell, HitResult result, Level world, @Nullable LivingEntity shooter, SpellContext context) {
        this.duration = delay;
        this.result = result;
        this.world = world;
        this.shooter = shooter;
        this.context = context;
    }

    public DelayedSpellEvent(int delay, HitResult result, Level world, @Nullable LivingEntity shooter, SpellContext context) {
        this.duration = delay;
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
        if (world == null)
            return;
        HitResult newResult = result;
        if (result instanceof EntityHitResult ehr && ehr.getEntity().isRemoved())
            newResult = new BlockHitResult(ehr.getLocation(), Direction.UP, ehr.getEntity().getOnPos(), true);
        SpellResolver resolver = new SpellResolver(context);
        resolver.onResolveEffect(world, newResult);
    }

    @Override
    public boolean isExpired() {
        return duration <= 0 || world == null;
    }
}
