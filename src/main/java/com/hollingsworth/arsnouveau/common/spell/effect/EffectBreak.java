package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtract;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static com.hollingsworth.arsnouveau.api.util.BlockUtil.destroyBlockSafely;

public class EffectBreak extends AbstractEffect {

    public EffectBreak() {
        super(ModConfig.EffectBreakID, "Break");
    }

    @Override
    public int getManaCost() {
        return 10;
    }

    public ItemStack getStack(LivingEntity shooter){
        if(isRealPlayer(shooter)){
            ItemStack mainHand = getPlayer(shooter, (ServerWorld)shooter.world).getHeldItemMainhand();
            return mainHand.isEmpty() ? getPlayer(shooter, (ServerWorld)shooter.world).getHeldItemOffhand() : mainHand;
        }

        return new ItemStack(Items.DIAMOND_PICKAXE);
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(!world.isRemote && rayTraceResult instanceof BlockRayTraceResult){
            BlockPos pos = new BlockPos(((BlockRayTraceResult) rayTraceResult).getPos());
            BlockState state;

            int aoeBuff = getBuffCount(augments, AugmentAOE.class);
            List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, (BlockRayTraceResult)rayTraceResult,1 + aoeBuff, 1 + aoeBuff, 1, -1);

            for(BlockPos pos1 : posList) {
                state = world.getBlockState(pos1);

                if(!canBlockBeHarvested(augments, world, pos1) || !BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerWorld) world), world, pos1)){
                    continue;
                }
                ItemStack stack = getStack(shooter);

                Map<Enchantment, Integer> map =  EnchantmentHelper.getEnchantments(stack);

                if (hasBuff(augments, AugmentExtract.class)) {
                    stack.addEnchantment(Enchantments.SILK_TOUCH, 1);
                    state.getBlock().harvestBlock(world, getPlayer(shooter, (ServerWorld) world), pos1, world.getBlockState(pos1), world.getTileEntity(pos1), stack);

                    destroyBlockSafely(world, pos1, false, shooter);

                } else if (hasBuff(augments, AugmentFortune.class)) {
                    int bonus = getBuffCount(augments, AugmentFortune.class);
                    stack.addEnchantment(Enchantments.FORTUNE, bonus);
                    //Block.spawnDrops(world.getBlockState(pos1), world, pos1, world.getTileEntity(pos1), shooter,stack);
                    state.getBlock().dropXpOnBlockBreak((ServerWorld) world, pos1, state.getExpDrop(world, pos1, bonus, 0));
                    state.getBlock().harvestBlock(world, getPlayer(shooter, (ServerWorld) world), pos1, world.getBlockState(pos1), world.getTileEntity(pos1), stack);
                    destroyBlockSafely(world, pos1, false, shooter);
                } else {
                    state.getBlock().harvestBlock(world, getPlayer(shooter, (ServerWorld) world), pos1, world.getBlockState(pos1), world.getTileEntity(pos1), stack);
                    destroyBlockSafely(world, pos1, false, shooter);
                    state.getBlock().dropXpOnBlockBreak((ServerWorld) world, pos1, state.getExpDrop(world, pos1, 0, 0));
                }

                EnchantmentHelper.setEnchantments(map, stack);
            }
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return rayTraceResult instanceof BlockRayTraceResult && world.getBlockState(((BlockRayTraceResult) rayTraceResult).getPos()).getMaterial() != Material.AIR && canBlockBeHarvested(augments, world, ((BlockRayTraceResult) rayTraceResult).getPos());
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.IRON_PICKAXE;
    }

    @Override
    protected String getBookDescription() {
        return "A spell you start with. Breaks blocks of an average hardness. Can be amplified to increase the harvest level.";
    }
}
