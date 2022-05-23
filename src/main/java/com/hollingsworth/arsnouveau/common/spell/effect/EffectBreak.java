package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

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
    public int getDefaultManaCost() {
        return 10;
    }

    public ItemStack getStack(LivingEntity shooter){
        return new ItemStack(Items.DIAMOND_PICKAXE);
//        if(isRealPlayer(shooter)){
//            ItemStack mainHand = getPlayer(shooter, (ServerLevel)shooter.level).getMainHandItem();
//            return (mainHand.isEmpty() ? getPlayer(shooter, (ServerLevel)shooter.level).getOffhandItem() : mainHand).copy();
//        }
//
//        return new ItemStack(Items.DIAMOND_PICKAXE);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        BlockPos pos = rayTraceResult.getBlockPos();
        BlockState state;

        int aoeBuff = spellStats.getBuffCount(AugmentAOE.INSTANCE);
        int pierceBuff = spellStats.getBuffCount(AugmentPierce.INSTANCE);
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, aoeBuff, pierceBuff);
        ItemStack stack = spellStats.hasBuff(AugmentSensitive.INSTANCE) ? new ItemStack(Items.SHEARS) : getStack(shooter);

        for(BlockPos pos1 : posList) {
            state = world.getBlockState(pos1);

            if(!canBlockBeHarvested(spellStats, world, pos1) || !BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, pos1) || state.is(BlockTagProvider.BREAK_BLACKLIST)){
                continue;
            }
            if(spellStats.hasBuff(AugmentExtract.INSTANCE)) {
                stack.enchant(Enchantments.SILK_TOUCH, 1);
                state.getBlock().playerDestroy(world, getPlayer(shooter, (ServerLevel) world), pos1, world.getBlockState(pos1), world.getBlockEntity(pos1), stack);
                destroyBlockSafely(world, pos1, false, shooter);
            }else if(spellStats.hasBuff(AugmentFortune.INSTANCE)) {
                int bonus = spellStats.getBuffCount(AugmentFortune.INSTANCE);
                stack.enchant(Enchantments.BLOCK_FORTUNE, bonus);
                state.getBlock().popExperience((ServerLevel) world, pos1, state.getExpDrop(world, pos1, bonus, 0));
                state.getBlock().playerDestroy(world, getPlayer(shooter, (ServerLevel) world), pos1, world.getBlockState(pos1), world.getBlockEntity(pos1), stack);
                destroyBlockSafely(world, pos1, false, shooter);
            } else {
                state.getBlock().playerDestroy(world, getPlayer(shooter, (ServerLevel) world), pos1, world.getBlockState(pos1), world.getBlockEntity(pos1), stack);
                destroyBlockSafely(world, pos1, false, shooter);
                state.getBlock().popExperience((ServerLevel) world, pos1, state.getExpDrop(world, pos1, 0, 0));
            }
        }
    }


    @Override
    public boolean defaultedStarterGlyph() {
        return true;
    }

    @Override
    public boolean wouldSucceed(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        return rayTraceResult instanceof BlockHitResult && world.getBlockState(((BlockHitResult) rayTraceResult).getBlockPos()).getMaterial() != Material.AIR && canBlockBeHarvested(spellStats, world, ((BlockHitResult) rayTraceResult).getBlockPos());
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentPierce.INSTANCE,
                AugmentAOE.INSTANCE,
                AugmentExtract.INSTANCE,
                AugmentFortune.INSTANCE,
                AugmentSensitive.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "A spell you start with. Breaks blocks of an average hardness. Can be amplified to increase the harvest level. Sensitive will simulate breaking blocks with Shears instead of a pickaxe.";
    }

    @Override
    protected Map<String, Integer> getDefaultAugmentLimits() {
        Map<String, Integer> map = super.getDefaultAugmentLimits();
        map.put(GlyphLib.AugmentFortuneID, 4);
        return map;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
