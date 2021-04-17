package com.hollingsworth.arsnouveau.api.util;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

import net.minecraftforge.common.util.FakePlayerFactory;

public class LootUtil {

    public static ItemStack getDefaultFakeTool(){return new ItemStack(Items.DIAMOND_PICKAXE);}
    public static ItemStack getDefaultFakeWeapon(){return new ItemStack(Items.DIAMOND_SWORD);}
    public static LootContext.Builder getDefaultContext(ServerWorld serverWorld, BlockPos pos, LivingEntity shooter){
        return (new LootContext.Builder(serverWorld)).withRandom(serverWorld.random).withParameter(LootParameters.ORIGIN, new Vector3d(pos.getX(), pos.getY(), pos.getZ())).withParameter(LootParameters.THIS_ENTITY, shooter)
                .withOptionalParameter(LootParameters.BLOCK_ENTITY, serverWorld.getBlockEntity(pos));
    }

    public static LootContext.Builder getSilkContext(ServerWorld serverWorld, BlockPos pos, LivingEntity shooter){
        ItemStack stack = getDefaultFakeTool();
        stack.enchant(Enchantments.SILK_TOUCH, 1);
        return getDefaultContext(serverWorld, pos, shooter).withParameter(LootParameters.TOOL, stack);
    }



    public static LootContext.Builder getFortuneContext(ServerWorld world, BlockPos pos, LivingEntity shooter, int enchLevel){
        ItemStack stack = getDefaultFakeTool();
        stack.enchant(Enchantments.BLOCK_FORTUNE, enchLevel);
        return getDefaultContext(world, pos, shooter).withParameter(LootParameters.TOOL, stack);
    }

    public static LootContext.Builder getLootingContext(ServerWorld world, LivingEntity player, LivingEntity slainEntity, int looting, DamageSource source){
        ItemStack stack = getDefaultFakeWeapon();
        stack.enchant(Enchantments.MOB_LOOTING, looting);
        return(new LootContext.Builder(world)).withRandom(world.random).withParameter(LootParameters.THIS_ENTITY, slainEntity)
                .withParameter(LootParameters.ORIGIN, new Vector3d(slainEntity.getX(), slainEntity.getY(), slainEntity.getZ())).withParameter(LootParameters.LAST_DAMAGE_PLAYER, FakePlayerFactory.getMinecraft(world)).withParameter(LootParameters.DAMAGE_SOURCE, source).withOptionalParameter(LootParameters.KILLER_ENTITY, source.getEntity())
                .withOptionalParameter(LootParameters.DIRECT_KILLER_ENTITY, source.getDirectEntity()).withParameter(LootParameters.KILLER_ENTITY, player).withLuck( player instanceof PlayerEntity ? ((PlayerEntity)player).getLuck() : 1.0f).withParameter(LootParameters.TOOL, stack).withParameter(LootParameters.EXPLOSION_RADIUS, 0.0f)
                .withParameter(LootParameters.BLOCK_STATE, Blocks.AIR.defaultBlockState()).withParameter(LootParameters.BLOCK_ENTITY, new FurnaceTileEntity());
    }
}
