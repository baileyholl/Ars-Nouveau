package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;
import com.hollingsworth.craftedmagic.api.spell.AbstractEffect;
import com.hollingsworth.craftedmagic.entity.EntityAllyVex;
import com.hollingsworth.craftedmagic.spell.augment.AugmentExtendTime;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectSummonVex extends AbstractEffect {
    public EffectSummonVex() {
        super(ModConfig.EffectSummonVexID, "Summon Vex");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AbstractAugment> augments) {
        for(int i = 0; i < 3; ++i) {
            BlockPos blockpos = (new BlockPos(shooter)).add(-2 + shooter.getRNG().nextInt(5), 1, -2 + shooter.getRNG().nextInt(5));
            EntityAllyVex vexentity = new EntityAllyVex(world, (PlayerEntity)shooter);
            vexentity.moveToBlockPosAndAngles(blockpos, 0.0F, 0.0F);
            vexentity.onInitialSpawn(world, world.getDifficultyForLocation(blockpos), SpawnReason.MOB_SUMMONED, (ILivingEntityData)null, (CompoundNBT)null);
            vexentity.setOwner((PlayerEntity)shooter);
            vexentity.setBoundOrigin(blockpos);
            vexentity.setLimitedLife(20 * (75 + 10 * getBuffCount(augments, AugmentExtendTime.class)));
            world.addEntity(vexentity);
        }
    }



    @Override
    public int getManaCost() {
        return 75;
    }
}
