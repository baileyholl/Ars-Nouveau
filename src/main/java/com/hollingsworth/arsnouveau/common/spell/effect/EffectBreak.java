package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.hollingsworth.arsnouveau.api.util.BlockUtil.destroyBlockSafely;

public class EffectBreak extends AbstractEffect {
    public static EffectBreak INSTANCE = new EffectBreak();

    private EffectBreak() {
        super(GlyphLib.EffectBreakID, "Break");
    }

    @Override
    public int getManaCost() {
        return 10;
    }

    public ItemStack getStack(LivingEntity shooter){
        if(isRealPlayer(shooter)){
            ItemStack mainHand = getPlayer(shooter, (ServerWorld)shooter.level).getMainHandItem();
            return (mainHand.isEmpty() ? getPlayer(shooter, (ServerWorld)shooter.level).getOffhandItem() : mainHand).copy();
        }

        return new ItemStack(Items.DIAMOND_PICKAXE);
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(!world.isClientSide && rayTraceResult instanceof BlockRayTraceResult){
            BlockPos pos = new BlockPos(((BlockRayTraceResult) rayTraceResult).getBlockPos());
            BlockState state;

            int aoeBuff = getBuffCount(augments, AugmentAOE.class);
            int pierceBuff = getBuffCount(augments, AugmentPierce.class);
            List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, (BlockRayTraceResult)rayTraceResult, aoeBuff, pierceBuff);
            ItemStack stack = getStack(shooter);

            Map<Enchantment, Integer> map =  EnchantmentHelper.getEnchantments(stack);

            for(BlockPos pos1 : posList) {
                state = world.getBlockState(pos1);

                if(!canBlockBeHarvested(augments, world, pos1) || !BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerWorld) world), world, pos1)){
                    continue;
                }
                if (hasBuff(augments, AugmentExtract.class)) {
                    stack.enchant(Enchantments.SILK_TOUCH, 1);
                    state.getBlock().playerDestroy(world, getPlayer(shooter, (ServerWorld) world), pos1, world.getBlockState(pos1), world.getBlockEntity(pos1), stack);

                    destroyBlockSafely(world, pos1, false, shooter);

                } else if (hasBuff(augments, AugmentFortune.class)) {
                    int bonus = getBuffCount(augments, AugmentFortune.class);
                    stack.enchant(Enchantments.BLOCK_FORTUNE, bonus);
                    //Block.spawnDrops(world.getBlockState(pos1), world, pos1, world.getTileEntity(pos1), shooter,stack);
                    state.getBlock().popExperience((ServerWorld) world, pos1, state.getExpDrop(world, pos1, bonus, 0));
                    state.getBlock().playerDestroy(world, getPlayer(shooter, (ServerWorld) world), pos1, world.getBlockState(pos1), world.getBlockEntity(pos1), stack);
                    destroyBlockSafely(world, pos1, false, shooter);
                } else {
                    state.getBlock().playerDestroy(world, getPlayer(shooter, (ServerWorld) world), pos1, world.getBlockState(pos1), world.getBlockEntity(pos1), stack);
                    destroyBlockSafely(world, pos1, false, shooter);
                    state.getBlock().popExperience((ServerWorld) world, pos1, state.getExpDrop(world, pos1, 0, 0));
                }
            }
        }
    }

    @Override
    public boolean defaultedStarterGlyph() {
        return true;
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return rayTraceResult instanceof BlockRayTraceResult && world.getBlockState(((BlockRayTraceResult) rayTraceResult).getBlockPos()).getMaterial() != Material.AIR && canBlockBeHarvested(augments, world, ((BlockRayTraceResult) rayTraceResult).getBlockPos());
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

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentPierce.INSTANCE,
                AugmentAOE.INSTANCE,
                AugmentExtract.INSTANCE,
                AugmentFortune.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "A spell you start with. Breaks blocks of an average hardness. Can be amplified to increase the harvest level.";
    }
}
