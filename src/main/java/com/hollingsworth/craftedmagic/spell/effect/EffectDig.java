package com.hollingsworth.craftedmagic.spell.effect;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractEffect;
import com.hollingsworth.craftedmagic.api.util.LootUtil;
import com.hollingsworth.craftedmagic.api.util.SpellUtil;
import com.hollingsworth.craftedmagic.spell.augment.AugmentAOE;
import com.hollingsworth.craftedmagic.spell.augment.AugmentExtract;
import com.hollingsworth.craftedmagic.spell.augment.AugmentFortune;
import com.hollingsworth.craftedmagic.api.spell.AbstractAugment;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;

public class EffectDig extends AbstractEffect {

    public EffectDig() {
        super(ModConfig.EffectDigID, "Dig");
    }

    @Override
    public int getManaCost() {
        return 15;
    }




    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AbstractAugment> augments) {
        if(!world.isRemote && rayTraceResult instanceof BlockRayTraceResult){
            BlockPos pos = new BlockPos(((BlockRayTraceResult) rayTraceResult).getPos());
            BlockState state;
            float maxHardness = 3.0f;
            int buff = getAmplificationBonus(augments);
            if(buff >= 3){
                maxHardness = 50.0f + 25 * (getAmplificationBonus(augments) - 3);
            }else if(buff == 2){
                maxHardness = 23.0f;
            }else if(buff == 1){
                maxHardness = 5.0f;
            }else if(buff == -1){
                maxHardness = 2.5f;
            }else if(buff == -2){
                maxHardness = 1.0f;
            }else if(buff < -2){
                maxHardness = 0.5f;
            }

            int aoeBuff = getBuffCount(augments, AugmentAOE.class);
            ImmutableList<BlockPos> posList = SpellUtil.calcAOEBlocks((PlayerEntity)shooter, pos, (BlockRayTraceResult)rayTraceResult,1 + aoeBuff, 1 + aoeBuff, 1, -1);
            for(BlockPos pos1 : posList) {
                state = world.getBlockState(pos1);
                // Iron block or lower unpowered
                if(!(state.getBlockHardness(world, pos1) <= maxHardness && state.getBlockHardness(world, pos1) >= 0)){
                    continue;
                }
                if (hasBuff(augments, AugmentExtract.class)) {
                    Block.spawnDrops(state, LootUtil.getSilkContext((ServerWorld) world, pos1, (PlayerEntity) shooter));
                    world.destroyBlock(pos1, false);
                } else if (hasBuff(augments, AugmentFortune.class)) {
                    Block.spawnDrops(state, LootUtil.getFortuneContext((ServerWorld) world, pos1, (PlayerEntity) shooter, getBuffCount(augments, AugmentFortune.class)));
                    world.destroyBlock(pos1, false);
                } else {
                    world.destroyBlock(pos1, true);
                }
            }
        }
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }
}
