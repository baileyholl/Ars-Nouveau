package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.util.List;

import com.hollingsworth.arsnouveau.api.spell.ISpellTier.Tier;

public class EffectGrow  extends AbstractEffect {

    public EffectGrow() {
        super(GlyphLib.EffectGrowID, "Grow");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof BlockRayTraceResult) {
            for(BlockPos blockpos : SpellUtil.calcAOEBlocks(shooter, ((BlockRayTraceResult) rayTraceResult).getBlockPos(), (BlockRayTraceResult) rayTraceResult, getBuffCount(augments, AugmentAOE.class), getBuffCount(augments, AugmentPierce.class))){
                //BlockPos blockpos = ((BlockRayTraceResult) rayTraceResult).getPos();
                ItemStack stack = new ItemStack(Items.BONE_MEAL);
                if(world instanceof ServerWorld)
                    BoneMealItem.applyBonemeal(stack, world, blockpos, FakePlayerFactory.getMinecraft((ServerWorld) world));
            }
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        if(!(rayTraceResult instanceof BlockRayTraceResult))
            return false;
        BlockPos pos = ((BlockRayTraceResult) rayTraceResult).getBlockPos();

        return world.getBlockState(pos).getBlock() instanceof IGrowable
                && ((IGrowable) world.getBlockState(pos).getBlock()).isValidBonemealTarget(world, pos, world.getBlockState(pos), world.isClientSide);
    }

    public static boolean applyBonemeal(World worldIn, BlockPos pos) {
        BlockState blockstate = worldIn.getBlockState(pos);
        if (blockstate.getBlock() instanceof IGrowable) {
            IGrowable igrowable = (IGrowable)blockstate.getBlock();
            if (igrowable.isValidBonemealTarget(worldIn, pos, blockstate, worldIn.isClientSide)) {
                if (!worldIn.isClientSide) {
                    if (igrowable.isBonemealSuccess(worldIn, worldIn.random, pos, blockstate) && !World.isOutsideBuildHeight(pos)) {
                        igrowable.performBonemeal((ServerWorld)worldIn, worldIn.random, pos, blockstate);
                        worldIn.sendBlockUpdated(pos, blockstate, blockstate, 3);
                    }
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public int getManaCost() {
        return 70;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.BONE_BLOCK;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Override
    public String getBookDescription() {
        return "Causes plants to accelerate in growth, but this does not provide mana for nearby Mana Condensers.";
    }
}
