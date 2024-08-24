package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class LingerSpellEvent implements ITimedEvent {
    public int duration;
    public final SpellResolver resolver;
    public final HitResult result;
    public final Level world;

    public LingerSpellEvent(int delay, HitResult result, Level world, SpellResolver resolver) {
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
            Vec3 hitVec = safelyGetHitPos(result);
            ParticleUtil.spawnTouch((ClientLevel) world, BlockPos.containing(hitVec), resolver.spellContext.getColors());
        }
    }

    public Vec3 safelyGetHitPos(HitResult result) {
        return result instanceof EntityHitResult entityHitResult ? entityHitResult.getEntity().position() : result.getLocation();
    }

    public void resolveSpell() {
        if (world == null)
            return;
        if (result instanceof EntityHitResult ehr && ehr.getEntity().isRemoved()) {
            return;
        }
        Vec3 pos = safelyGetHitPos(result);
        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, new AABB(pos, pos.add(1, 1, 1)));
        if (entities.isEmpty()) {
            resolver.resume(world);
            ParticleUtil.spawnTouchPacket(world, BlockPos.containing(pos), resolver.spellContext.getColors());
            return;
        }
        for (LivingEntity entity : entities) {
            SpellResolver newResolver = resolver.clone();
            newResolver.hitResult = new EntityHitResult(entity);
            newResolver.resume(world);
            ParticleUtil.spawnTouchPacket(world, BlockPos.containing(pos), resolver.spellContext.getColors());
        }
    }

    @Override
    public boolean isExpired() {
        return duration <= 0 || world == null;
    }
}
