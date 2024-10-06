package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.common.util.HolderHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class LootUtil {

    public static ItemStack getDefaultFakeTool() {
        return new ItemStack(Items.DIAMOND_PICKAXE);
    }

    public static ItemStack getDefaultFakeWeapon() {
        return new ItemStack(Items.DIAMOND_SWORD);
    }

    public static LootParams.Builder getDefaultContext(ServerLevel serverWorld, BlockPos pos, LivingEntity shooter) {
        return (new LootParams.Builder(serverWorld)).withParameter(LootContextParams.ORIGIN, new Vec3(pos.getX(), pos.getY(), pos.getZ())).withParameter(LootContextParams.THIS_ENTITY, shooter)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, serverWorld.getBlockEntity(pos));
    }

    public static LootParams.Builder getSilkContext(ServerLevel serverWorld, BlockPos pos, LivingEntity shooter) {
        ItemStack stack = getDefaultFakeTool();
        stack.enchant(HolderHelper.unwrap(serverWorld, Enchantments.SILK_TOUCH), 1);
        return getDefaultContext(serverWorld, pos, shooter).withParameter(LootContextParams.TOOL, stack);
    }


    public static LootParams.Builder getFortuneContext(ServerLevel world, BlockPos pos, LivingEntity shooter, int enchLevel) {
        ItemStack stack = getDefaultFakeTool();
        stack.enchant(HolderHelper.unwrap(world, Enchantments.FORTUNE), enchLevel);
        return getDefaultContext(world, pos, shooter).withParameter(LootContextParams.TOOL, stack);
    }

    public static LootParams.Builder getLootingContext(ServerLevel world, LivingEntity player, LivingEntity slainEntity, int looting, DamageSource source) {
        ItemStack stack = getDefaultFakeWeapon();
        stack.enchant(HolderHelper.unwrap(world, Enchantments.LOOTING), looting);
        return new LootParams.Builder(world)
                .withParameter(LootContextParams.THIS_ENTITY, slainEntity)
                .withParameter(LootContextParams.ORIGIN, new Vec3(slainEntity.getX(), slainEntity.getY(), slainEntity.getZ()))
                .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, ANFakePlayer.getOrFakePlayer(world, player))
                .withParameter(LootContextParams.DAMAGE_SOURCE, source).withOptionalParameter(LootContextParams.ATTACKING_ENTITY, source.getEntity())
                .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, source.getDirectEntity())
                .withParameter(LootContextParams.ATTACKING_ENTITY, player).withLuck(player instanceof Player ? ((Player) player).getLuck() : 1.0f)
                .withParameter(LootContextParams.TOOL, stack).withParameter(LootContextParams.EXPLOSION_RADIUS, 0.0f)
                .withParameter(LootContextParams.BLOCK_STATE, Blocks.AIR.defaultBlockState())
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, null);
    }
}
