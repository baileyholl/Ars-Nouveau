package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
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
            ItemStack mainHand = getPlayer(shooter, (ServerLevel)shooter.level).getMainHandItem();
            return (mainHand.isEmpty() ? getPlayer(shooter, (ServerLevel)shooter.level).getOffhandItem() : mainHand).copy();
        }

        return new ItemStack(Items.DIAMOND_PICKAXE);
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

            if(!canBlockBeHarvested(spellStats, world, pos1) || !BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, pos1)){
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
    public boolean wouldSucceed(HitResult rayTraceResult, Level world, LivingEntity shooter, List<AbstractAugment> augments) {
        return rayTraceResult instanceof BlockHitResult && world.getBlockState(((BlockHitResult) rayTraceResult).getBlockPos()).getMaterial() != Material.AIR && canBlockBeHarvested(augments, world, ((BlockHitResult) rayTraceResult).getBlockPos());
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
                AugmentFortune.INSTANCE,
                AugmentSensitive.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "A spell you start with. Breaks blocks of an average hardness. Can be amplified to increase the harvest level. Sensitive will simulate breaking blocks with Shears instead of a pickaxe.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
