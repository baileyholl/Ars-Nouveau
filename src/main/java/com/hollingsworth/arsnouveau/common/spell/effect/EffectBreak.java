package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.util.HolderHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EffectBreak extends AbstractEffect {
    public static EffectBreak INSTANCE = new EffectBreak();

    private EffectBreak() {
        super(GlyphLib.EffectBreakID, "Break");
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }

    public ItemStack getStack(LivingEntity shooter, BlockHitResult blockHitResult) {
        ItemStack stack = shooter.getMainHandItem().copy();
        boolean usePick = shooter.level.getBlockState(blockHitResult.getBlockPos()).is(BlockTagProvider.BREAK_WITH_PICKAXE);
        if (usePick) {
            return new ItemStack(Items.DIAMOND_PICKAXE);
        }
        return stack.isEmpty() ? new ItemStack(Items.DIAMOND_PICKAXE) : stack;
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getBlockPos();
        BlockState state;

        MobEffectInstance miningFatigue = shooter.getEffect(MobEffects.DIG_SLOWDOWN);
        if (miningFatigue != null)
            spellStats.setAmpMultiplier(spellStats.getAmpMultiplier() - miningFatigue.getAmplifier());

        double aoeBuff = spellStats.getAoeMultiplier();
        int pierceBuff = spellStats.getBuffCount(AugmentPierce.INSTANCE);
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, aoeBuff, pierceBuff);
        ItemStack stack = spellStats.isSensitive() ? new ItemStack(Items.SHEARS) : getStack(shooter, rayTraceResult);

        int numFortune = spellStats.getBuffCount(AugmentFortune.INSTANCE);
        int numSilkTouch = spellStats.getBuffCount(AugmentExtract.INSTANCE);
        if (numFortune > 0 && stack.getEnchantmentLevel(HolderHelper.unwrap(world, Enchantments.FORTUNE)) < numFortune) {
            stack.enchant(HolderHelper.unwrap(world, Enchantments.FORTUNE), numFortune);
        }
        if (numSilkTouch > 0 && stack.getEnchantmentLevel(HolderHelper.unwrap(world, Enchantments.SILK_TOUCH)) < numSilkTouch) {
            stack.enchant(HolderHelper.unwrap(world, Enchantments.SILK_TOUCH), numSilkTouch);
        }
        for (BlockPos pos1 : posList) {
            if (world.isOutsideBuildHeight(pos1) || world.random.nextFloat() < spellStats.getBuffCount(AugmentRandomize.INSTANCE) * 0.25F) {
                continue;
            }
            state = world.getBlockState(pos1);

            if (!canBlockBeHarvested(spellStats, world, pos1) || !BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, pos1) || state.is(BlockTagProvider.BREAK_BLACKLIST)) {
                continue;
            }

            if (!BlockUtil.breakExtraBlock(spellContext, (ServerLevel) world, pos1, stack, shooter.getUUID(), true)) {
                continue;
            }

            ShapersFocus.tryPropagateBlockSpell(new BlockHitResult(
                    new Vec3(pos1.getX(), pos1.getY(), pos1.getZ()), rayTraceResult.getDirection(), pos1, false
            ), world, shooter, spellContext, resolver);
        }
    }

    @Override
    public boolean defaultedStarterGlyph() {
        return true;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentPierce.INSTANCE, AugmentAOE.INSTANCE,
                AugmentExtract.INSTANCE, AugmentFortune.INSTANCE,
                AugmentSensitive.INSTANCE, AugmentRandomize.INSTANCE
        );
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addBlockAoeAugmentDescriptions(map);
        map.put(AugmentSensitive.INSTANCE, "Breaks blocks with Shears instead of a pickaxe.");
        map.put(AugmentDampen.INSTANCE, "Decreases the harvest level.");
        map.put(AugmentAmplify.INSTANCE, "Increases the harvest level.");
    }

    @Override
    public String getBookDescription() {
        return "Breaks blocks of an average hardness. Can be amplified to increase the harvest level. Sensitive will simulate breaking blocks with Shears instead of a pickaxe.";
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentFortune.INSTANCE.getRegistryName(), 4);
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 1);
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
