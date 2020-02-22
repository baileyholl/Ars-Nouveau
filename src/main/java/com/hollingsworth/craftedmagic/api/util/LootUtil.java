package com.hollingsworth.craftedmagic.api.util;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

public class LootUtil {

    public static ItemStack getDefaultFakeTool(){return new ItemStack(Items.DIAMOND_PICKAXE);}
    public static ItemStack getDefaultFakeWeapon(){return new ItemStack(Items.DIAMOND_SWORD);}
    public static LootContext.Builder getDefaultContext(ServerWorld serverWorld, BlockPos pos, PlayerEntity shooter){
        return (new LootContext.Builder(serverWorld)).withRandom(serverWorld.rand).withParameter(LootParameters.POSITION, pos).withParameter(LootParameters.THIS_ENTITY, shooter)
                .withNullableParameter(LootParameters.BLOCK_ENTITY, serverWorld.getTileEntity(pos));
    }

    public static LootContext.Builder getSilkContext(ServerWorld serverWorld, BlockPos pos, PlayerEntity shooter){
        ItemStack stack = getDefaultFakeTool();
        stack.addEnchantment(Enchantments.SILK_TOUCH, 1);
        return getDefaultContext(serverWorld, pos, shooter).withParameter(LootParameters.TOOL, stack);
    }



    public static LootContext.Builder getFortuneContext(ServerWorld world, BlockPos pos, PlayerEntity shooter, int enchLevel){
        ItemStack stack = getDefaultFakeTool();
        stack.addEnchantment(Enchantments.FORTUNE, enchLevel);
        return getDefaultContext(world, pos, shooter).withParameter(LootParameters.TOOL, stack);
    }

    public static LootContext.Builder getLootingContext(ServerWorld world, PlayerEntity player, LivingEntity slainEntity, int looting, DamageSource source){
        ItemStack stack = getDefaultFakeWeapon();
        stack.addEnchantment(Enchantments.LOOTING, looting);
        return(new LootContext.Builder(world)).withRandom(world.rand).withParameter(LootParameters.THIS_ENTITY, slainEntity)
                .withParameter(LootParameters.POSITION, new BlockPos(slainEntity)).withParameter(LootParameters.DAMAGE_SOURCE, source).withNullableParameter(LootParameters.KILLER_ENTITY, source.getTrueSource())
                .withNullableParameter(LootParameters.DIRECT_KILLER_ENTITY, source.getImmediateSource()).withParameter(LootParameters.LAST_DAMAGE_PLAYER, player).withLuck(player.getLuck()).withParameter(LootParameters.TOOL, stack).withParameter(LootParameters.EXPLOSION_RADIUS, 0.0f)
                .withParameter(LootParameters.BLOCK_STATE, Blocks.AIR.getDefaultState()).withParameter(LootParameters.BLOCK_ENTITY, new FurnaceTileEntity());
    }
}
