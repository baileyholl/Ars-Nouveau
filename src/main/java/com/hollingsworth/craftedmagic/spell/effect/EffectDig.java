package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.util.LootUtil;
import com.hollingsworth.craftedmagic.spell.augment.AugmentAmplify;
import com.hollingsworth.craftedmagic.spell.augment.AugmentExtract;
import com.hollingsworth.craftedmagic.spell.augment.AugmentFortune;
import com.hollingsworth.craftedmagic.spell.augment.AugmentType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;

import java.util.ArrayList;
import java.util.List;

public class EffectDig extends EffectType {

    public EffectDig() {
        super(ModConfig.EffectDigID, "Dig");
    }

    @Override
    public int getManaCost() {
        return 15;
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AugmentType> augments) {
        if(!world.isRemote && rayTraceResult instanceof BlockRayTraceResult){
            BlockPos pos = new BlockPos(((BlockRayTraceResult) rayTraceResult).getPos());
            BlockState state = world.getBlockState(pos);
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
            //Iron block and lower.
            if(!(state.getBlockHardness(world, pos) <= maxHardness && state.getBlockHardness(world, pos) >= 0)){
                return;
            }
            if(hasBuff(augments, AugmentExtract.class)){
                Block.spawnDrops(state, LootUtil.getSilkContext((ServerWorld)world, pos, (PlayerEntity)shooter));
                world.destroyBlock(pos, false);
            }else if(hasBuff(augments, AugmentFortune.class)){
                Block.spawnDrops(state, LootUtil.getFortuneContext((ServerWorld)world, pos, (PlayerEntity)shooter, getBuffCount(augments, AugmentFortune.class)));
                world.destroyBlock(pos, false);
            }else {
                world.destroyBlock(pos, true);
            }
        }
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }
}
