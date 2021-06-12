package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.entity.EntityEvokerFangs;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectFangs extends AbstractEffect {
    public static EffectFangs INSTANCE = new EffectFangs();

    private EffectFangs() {
        super(GlyphLib.EffectFangsID, "Fangs");
    }



    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        super.onResolveBlock(rayTraceResult, world, shooter, augments, spellContext);
    }

    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(shooter == null && spellContext.castingTile != null) {
            shooter = FakePlayerFactory.getMinecraft((ServerWorld) world);
            BlockPos pos = spellContext.castingTile.getBlockPos();
            shooter.setPos(pos.getX(), pos.getY(), pos.getZ());
        }

        if(shooter == null)
            return;
        Vector3d vec = rayTraceResult.getLocation();

        double damage = DAMAGE.get() + AMP_VALUE.get() * getAmplificationBonus(augments);
        double targetX = vec.x;
        double targetY = vec.y;
        double targetZ = vec.z;

        double d0 = Math.min(targetY, shooter.getY());
        double d1 = Math.max(targetY, shooter.getY()) + 1.0D;
        float f = (float)MathHelper.atan2(targetZ - shooter.getZ(), targetX - shooter.getX());

        if(rayTraceResult instanceof EntityRayTraceResult && shooter.equals(((EntityRayTraceResult) rayTraceResult).getEntity())){
            for(int i = 0; i < 5; ++i) {
                float f1 = f + (float)i * (float)Math.PI * 0.4F;
                int j =  ( i + getDurationModifier(augments)) / (1 + getBuffCount(augments, AugmentAccelerate.class));
                spawnFangs(world, shooter.getX() + (double)MathHelper.cos(f1) * 1.5D, shooter.getZ() + (double)MathHelper.sin(f1) * 1.5D, d0, d1, f1, j,shooter, (float) damage);
            }

            for(int k = 0; k < 8; ++k) {
                float f2 = f + (float)k * (float)Math.PI * 2.0F / 8.0F + 1.2566371F;
                int j =  ( k + getDurationModifier(augments)) / (1 + getBuffCount(augments, AugmentAccelerate.class));
                spawnFangs(world, shooter.getX() + (double)MathHelper.cos(f2) * 2.5D, shooter.getZ() + (double)MathHelper.sin(f2) * 2.5D, d0, d1, f2, j, shooter, (float) damage);
            }
            return;
        }
        for(int l = 0; l < 16; ++l) {
            double d2 = 1.25D * (double)(l + 1);
            int j =  ( l + getDurationModifier(augments)) / (1 + getBuffCount(augments, AugmentAccelerate.class));
            this.spawnFangs(world, shooter.getX() + (double)MathHelper.cos(f) * d2, shooter.getZ() + (double)MathHelper.sin(f) * d2, d0, d1, f, j, shooter, (float) damage);
        }
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 6.0);
        addAmpConfig(builder, 3.0);
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return nonAirAnythingSuccess(rayTraceResult, world);
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    private void spawnFangs(World world, double xAngle, double zAngle, double yStart, double yEnd, float rotationYaw, int tickDelay, LivingEntity caster, float damage) {
        BlockPos blockpos = new BlockPos(xAngle, yEnd, zAngle);
        boolean flag = false;
        double d0 = 0.0D;

        while(true) {
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
            if (blockpos.getY() < MathHelper.floor(yStart) - 1) {
                break;
            }
        }

        if (flag) {
           world.addFreshEntity(new EntityEvokerFangs(world, xAngle, (double)blockpos.getY() + d0, zAngle, rotationYaw, tickDelay, caster, damage));
        }

    }

    @Override
    public int getManaCost() {
        return 35;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.PRISMARINE_SHARD;
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }

    @Nonnull
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
        return "Summons Evoker Fangs in the direction where the spell was targeted.";
    }
}
