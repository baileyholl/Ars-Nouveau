package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityEvokerFangs;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class EffectFangs extends AbstractEffect implements IDamageEffect {
    public static EffectFangs INSTANCE = new EffectFangs();

    private EffectFangs() {
        super(GlyphLib.EffectFangsID, "Fangs");
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (shooter == null && spellContext.castingTile != null) {
            shooter = ANFakePlayer.getPlayer((ServerLevel) world);
            BlockPos pos = spellContext.castingTile.getBlockPos();
            shooter.setPos(pos.getX(), pos.getY(), pos.getZ());
        }

        if (shooter == null)
            return;
        Vec3 vec = rayTraceResult.getLocation();

        double damage = DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier();
        double targetX = vec.x;
        double targetY = vec.y;
        double targetZ = vec.z;

        double d0 = Math.min(targetY, shooter.getY());
        double d1 = Math.max(targetY, shooter.getY()) + 1.0D;
        float f = (float) Mth.atan2(targetZ - shooter.getZ(), targetX - shooter.getX());
        int accelerate = spellStats.getBuffCount(AugmentAccelerate.INSTANCE); //no decelerate support atm
        double durationModifier = spellStats.getDurationMultiplier();
        // Create fangs in an AOE around the caster
        if (rayTraceResult instanceof EntityHitResult && shooter.equals(((EntityHitResult) rayTraceResult).getEntity())) {
            for (int i = 0; i < 5; ++i) {
                float f1 = f + (float) i * (float) Math.PI * 0.4F;
                int j = (int) ((i + durationModifier) / (1 + accelerate));
                spawnFangs(world, shooter.getX() + (double) Mth.cos(f1) * 1.5D, shooter.getZ() + (double) Mth.sin(f1) * 1.5D, d0, d1, f1, j, shooter, (float) damage);
            }

            for (int k = 0; k < 8; ++k) {
                float f2 = f + (float) k * (float) Math.PI * 2.0F / 8.0F + 1.2566371F;
                int j = (int) ((k + durationModifier) / (1 + accelerate));
                spawnFangs(world, shooter.getX() + (double) Mth.cos(f2) * 2.5D, shooter.getZ() + (double) Mth.sin(f2) * 2.5D, d0, d1, f2, j, shooter, (float) damage);
            }
            return;
        }
        for (int l = 0; l < 16; ++l) {
            double d2 = 1.25D * (double) (l + 1);
            int j = (int) ((l + durationModifier) / (1 + accelerate));
            this.spawnFangs(world, shooter.getX() + (double) Mth.cos(f) * d2, shooter.getZ() + (double) Mth.sin(f) * d2, d0, d1, f, j, shooter, (float) damage);
        }
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 6.0);
        addAmpConfig(builder, 3.0);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    private void spawnFangs(Level world, double xAngle, double zAngle, double yStart, double yEnd, float rotationYaw, int tickDelay, LivingEntity caster, float damage) {
        BlockPos blockpos = BlockPos.containing(xAngle, yEnd, zAngle);
        boolean flag = false;
        double d0 = 0.0D;

        while (true) {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = world.getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(world, blockpos1, Direction.UP)) {
                if (!world.isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = world.getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(world, blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }

                flag = true;
                break;
            }

            blockpos = blockpos.below();
            if (blockpos.getY() < Mth.floor(yStart) - 1) {
                break;
            }
        }

        if (flag) {
            world.addFreshEntity(new EntityEvokerFangs(world, xAngle, (double) blockpos.getY() + d0, zAngle, rotationYaw, tickDelay, caster, damage));
        }

    }

    @Override
    public int getDefaultManaCost() {
        return 35;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE,
                AugmentAccelerate.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Summons Evoker Fangs in the direction where the spell was targeted. Using fangs on your self will spawn them in an area around you.";
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.CONJURATION);
    }
}
