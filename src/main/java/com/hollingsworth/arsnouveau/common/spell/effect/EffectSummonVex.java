package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.common.entity.EntityAllyVex;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
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

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.TOTEM_OF_UNDYING;
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }

    @Override
    protected String getBookDescription() {
        return "Summons three Vex allies that will attack nearby hostile enemies. These Vex will last a short time until they begin to take damage, but time may be extended with the " +
                "Extend Time augment.";
    }
}
