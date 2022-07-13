package com.hollingsworth.arsnouveau.api.util;


import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.minecraft.world.level.Explosion.BlockInteraction;

public class ANExplosion extends Explosion {
    public double amps;
    public double baseDamage;
    public double ampDamageScalar;

    public ANExplosion(Level p_i45752_1_, @Nullable Entity p_i45752_2_, double p_i45752_3_, double p_i45752_5_, double p_i45752_7_, float p_i45752_9_, List<BlockPos> p_i45752_10_) {
        super(p_i45752_1_, p_i45752_2_, p_i45752_3_, p_i45752_5_, p_i45752_7_, p_i45752_9_, p_i45752_10_);
    }

    public ANExplosion(Level p_i50006_1_, @Nullable Entity p_i50006_2_, double p_i50006_3_, double p_i50006_5_, double p_i50006_7_, float p_i50006_9_, boolean p_i50006_10_, BlockInteraction p_i50006_11_, List<BlockPos> p_i50006_12_) {
        super(p_i50006_1_, p_i50006_2_, p_i50006_3_, p_i50006_5_, p_i50006_7_, p_i50006_9_, p_i50006_10_, p_i50006_11_, p_i50006_12_);
    }

    public ANExplosion(Level p_i50007_1_, @Nullable Entity p_i50007_2_, double p_i50007_3_, double p_i50007_5_, double p_i50007_7_, float p_i50007_9_, boolean p_i50007_10_, BlockInteraction p_i50007_11_) {
        super(p_i50007_1_, p_i50007_2_, p_i50007_3_, p_i50007_5_, p_i50007_7_, p_i50007_9_, p_i50007_10_, p_i50007_11_);
    }

    public ANExplosion(Level p_i231610_1_, @Nullable Entity p_i231610_2_, @Nullable DamageSource p_i231610_3_, @Nullable ExplosionDamageCalculator p_i231610_4_, double p_i231610_5_, double p_i231610_7_, double p_i231610_9_, float p_i231610_11_, boolean p_i231610_12_, BlockInteraction p_i231610_13_,
                       double numAmps) {
        super(p_i231610_1_, p_i231610_2_, p_i231610_3_, p_i231610_4_, p_i231610_5_, p_i231610_7_, p_i231610_9_, p_i231610_11_, p_i231610_12_, p_i231610_13_);
        amps = numAmps;
    }

    @Override
    public void explode() {
        Set<BlockPos> set = Sets.newHashSet();
        int i = 16;

        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                for (int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d0 = (float) j / 15.0F * 2.0F - 1.0F;
                        double d1 = (float) k / 15.0F * 2.0F - 1.0F;
                        double d2 = (float) l / 15.0F * 2.0F - 1.0F;
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 = d0 / d3;
                        d1 = d1 / d3;
                        d2 = d2 / d3;
                        float f = this.radius * (0.7F + this.level.random.nextFloat() * 0.6F);
                        double d4 = this.x;
                        double d6 = this.y;
                        double d8 = this.z;

                        for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                            BlockPos blockpos = new BlockPos(d4, d6, d8);
                            BlockState blockstate = this.level.getBlockState(blockpos);
                            FluidState fluidstate = this.level.getFluidState(blockpos);
                            Optional<Float> optional = this.damageCalculator.getBlockExplosionResistance(this, this.level, blockpos, blockstate, fluidstate);
                            if (optional.isPresent()) {
                                f -= (optional.get() + 0.3F) * 0.3F;
                            }

                            if (f > 0.0F && this.damageCalculator.shouldBlockExplode(this, this.level, blockpos, blockstate, f)) {
                                set.add(blockpos);
                            }

                            d4 += d0 * (double) 0.3F;
                            d6 += d1 * (double) 0.3F;
                            d8 += d2 * (double) 0.3F;
                        }
                    }
                }
            }
        }

        this.toBlow.addAll(set);
        float f2 = this.radius * 2.0F;
        int k1 = Mth.floor(this.x - (double) f2 - 1.0D);
        int l1 = Mth.floor(this.x + (double) f2 + 1.0D);
        int i2 = Mth.floor(this.y - (double) f2 - 1.0D);
        int i1 = Mth.floor(this.y + (double) f2 + 1.0D);
        int j2 = Mth.floor(this.z - (double) f2 - 1.0D);
        int j1 = Mth.floor(this.z + (double) f2 + 1.0D);
        List<Entity> list = this.level.getEntities(this.source, new AABB(k1, i2, j2, l1, i1, j1));
        net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.level, this, list, f2);
        Vec3 vector3d = new Vec3(this.x, this.y, this.z);

        for (Entity entity : list) {
            if (!entity.ignoreExplosion()) {
                double d12 = Mth.sqrt((float) entity.distanceToSqr(vector3d)) / f2;
                if (d12 <= 1.0D) {
                    double d5 = entity.getX() - this.x;
                    double d7 = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y;
                    double d9 = entity.getZ() - this.z;
                    double d13 = Mth.sqrt((float) (d5 * d5 + d7 * d7 + d9 * d9));
                    if (d13 != 0.0D) {
                        d5 = d5 / d13;
                        d7 = d7 / d13;
                        d9 = d9 / d13;
                        double d14 = getSeenPercent(vector3d, entity);
                        double d10 = (1.0D - d12) * d14;
                        float damage = (float) Math.min(Math.max(0.0f, (float) ((int) ((d10 * d10 + d10) / 2.0D * 7.0D * (double) f2 + 1.0D))), baseDamage + this.amps * ampDamageScalar);
                        entity.hurt(this.getDamageSource(), damage);
                        double d11 = d10;
                        if (entity instanceof LivingEntity) {
                            d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity) entity, d10);
                        }

                        entity.setDeltaMovement(entity.getDeltaMovement().add(d5 * d11, d7 * d11, d9 * d11));
                        if (entity instanceof Player playerentity) {
                            if (!playerentity.isSpectator() && (!playerentity.isCreative() || !playerentity.abilities.flying)) {
                                this.hitPlayers.put(playerentity, new Vec3(d5 * d10, d7 * d10, d9 * d10));
                            }
                        }
                    }
                }
            }
        }
    }
}
