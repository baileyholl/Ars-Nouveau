package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DelayedSpellEvent implements ITimedEvent{
    private int duration;
    private final Spell spell;
    private final SpellContext context;
    private final RayTraceResult result;
    private final World world;
    private final @Nullable LivingEntity shooter;

    public DelayedSpellEvent(int delay, Spell spell, RayTraceResult result, World world, @Nullable LivingEntity shooter, SpellContext context){
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
        if(duration <= 0 && serverSide){
            resolveSpell();
        }else if(!serverSide && result != null){
            BlockPos hitVec = result instanceof EntityRayTraceResult ? ((EntityRayTraceResult) result).getEntity().blockPosition() : new BlockPos(result.getLocation());

            //ParticleUtil.spawnRitualAreaEffect(new BlockPos(result.getLocation()).above(), world, world.random, context.colors.toParticleColor(), 1);

            ParticleUtil.spawnTouch((ClientWorld) world, hitVec, context.colors.toParticleColor());
        }
    }

    public void resolveSpell(){
        if(world == null)
            return;
        SpellResolver.resolveEffects(world, shooter, result, spell, context);
    }

    @Override
    public boolean isExpired() {
        return duration <= 0 || world == null;
    }
}
