package com.hollingsworth.arsnouveau.common.entity.statemachine.arcano_boss;

import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.common.entity.arcano_boss.ArcanoBoss;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectEarthshake;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectHarm;
import com.hollingsworth.arsnouveau.common.world.dimension.PlanariumChunkGenerator;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArcanoBossGroundAttack extends ArcanoState {

    private final List<Vec3> corners = List.of(
            new Vec3(1.5, 2.0, 1.5),
            new Vec3(29.5, 2.0, 1.5),
            new Vec3(29.5, 2.0, 29.5),
            new Vec3(1.5, 2.0, 29.5)
    );

    private Phase phase = Phase.WINDUP;
    private int cornerIndex;
    private int phaseTicks;

    public ArcanoBossGroundAttack(ArcanoBoss arcanoBoss) {
        super(arcanoBoss);
    }

    @Override
    public void onStart() {
        arcanoBoss.setNoGravity(true);
        arcanoBoss.getNavigation().stop();
        beginCorner();
    }

    @Override
    public @Nullable ArcanoState tick() {
        arcanoBoss.setDeltaMovement(Vec3.ZERO);
        if (phase == Phase.WINDUP) {
            phaseTicks++;
            if (phaseTicks >= 35 && arcanoBoss.level() instanceof ServerLevel serverLevel) {
                resolveShockwave(serverLevel);
                arcanoBoss.setArcanoPose(ArcanoBoss.ArcanoBossState.IDLE);
                phase = Phase.WAVE;
                phaseTicks = 0;
            }
            return null;
        }

        phaseTicks++;
        if (phaseTicks >= 42) {
            cornerIndex++;
            if (cornerIndex >= corners.size()) {
                return new InitArcanoState(arcanoBoss);
            }
            beginCorner();
        }
        return null;
    }

    private void beginCorner() {
        phase = Phase.WINDUP;
        phaseTicks = 0;
        teleportTo(corners.get(cornerIndex));
        arcanoBoss.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(15.5, arcanoBoss.getEyeY(), 15.5));
        arcanoBoss.yBodyRot = arcanoBoss.getYRot();
        arcanoBoss.setYHeadRot(arcanoBoss.getYRot());
        arcanoBoss.setArcanoPose(ArcanoBoss.ArcanoBossState.SLAM_STAFF);
    }

    private void resolveShockwave(ServerLevel serverLevel) {
        Spell spell = new Spell().add(EffectEarthshake.INSTANCE).add(AugmentAOE.INSTANCE, 37).add(EffectHarm.INSTANCE).add(AugmentDampen.INSTANCE, 2);
        EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(serverLevel, spell, arcanoBoss, new LivingCaster(arcanoBoss)));
        Vec3 origin = new Vec3(arcanoBoss.getX(), PlanariumChunkGenerator.innerBox.minY, arcanoBoss.getZ());
        BlockPos floorPos = BlockPos.containing(origin).below();
        resolver.onResolveEffect(serverLevel, new BlockHitResult(origin, Direction.UP, floorPos, false));
    }

    private void teleportTo(Vec3 destination) {
        if (!(arcanoBoss.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 previousPosition = arcanoBoss.position();
        serverLevel.sendParticles(ParticleTypes.PORTAL, previousPosition.x, previousPosition.y + 1.0, previousPosition.z,
                12, 0.5, 1.0, 0.5, 0.1);
        arcanoBoss.teleportTo(destination.x, destination.y, destination.z);
        arcanoBoss.setDeltaMovement(Vec3.ZERO);
        arcanoBoss.level().gameEvent(GameEvent.TELEPORT, previousPosition, GameEvent.Context.of(arcanoBoss));
        serverLevel.sendParticles(ParticleTypes.PORTAL, destination.x, destination.y + 1.0, destination.z,
                12, 0.5, 1.0, 0.5, 0.1);
        serverLevel.playSound(null, arcanoBoss.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0f, 1.0f);
    }

    @Override
    public void onEnd() {
        arcanoBoss.setArcanoPose(ArcanoBoss.ArcanoBossState.IDLE);
        arcanoBoss.setNoGravity(false);
        arcanoBoss.setDeltaMovement(Vec3.ZERO);
    }

    private enum Phase {
        WINDUP,
        WAVE
    }
}
