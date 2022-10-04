package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

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

    public ItemStack getStack(LivingEntity shooter) {
        ItemStack stack = shooter.getMainHandItem().copy();
        return stack.isEmpty() ? new ItemStack(Items.DIAMOND_PICKAXE) : stack;
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getBlockPos();
        BlockState state;

        double aoeBuff = spellStats.getAoeMultiplier();
        int pierceBuff = spellStats.getBuffCount(AugmentPierce.INSTANCE);
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, aoeBuff, pierceBuff);
        ItemStack stack = spellStats.hasBuff(AugmentSensitive.INSTANCE) ? new ItemStack(Items.SHEARS) : getStack(shooter);

        int numFortune = spellStats.getBuffCount(AugmentFortune.INSTANCE);
        int numSilkTouch = spellStats.getBuffCount(AugmentExtract.INSTANCE);
        stack.enchant(Enchantments.BLOCK_FORTUNE, numFortune);
        stack.enchant(Enchantments.SILK_TOUCH, numSilkTouch);

        for (BlockPos pos1 : posList) {
            state = world.getBlockState(pos1);

            if (!canBlockBeHarvested(spellStats, world, pos1) || !BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, pos1) || state.is(BlockTagProvider.BREAK_BLACKLIST)) {
                continue;
            }

            state.getBlock().playerDestroy(world, getPlayer(shooter, (ServerLevel) world), pos1, world.getBlockState(pos1), world.getBlockEntity(pos1), stack);
            if (!state.is(BlockTagProvider.NO_BREAK_DROP))
                destroyBlockSafely(world, pos1, false, shooter);

            state.getBlock().popExperience((ServerLevel) world, pos1, state.getExpDrop(world, world.getRandom(), pos1, numFortune, numSilkTouch));

            ShapersFocus.tryPropagateBlockSpell(new BlockHitResult(
                    new Vec3(pos1.getX(), pos1.getY(), pos1.getZ()), rayTraceResult.getDirection(), pos1, false
            ), world, shooter, spellContext, resolver);
        }
    }

    @Override
    public boolean defaultedStarterGlyph() {
        return true;
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
    protected Map<ResourceLocation, Integer> getDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        super.getDefaultAugmentLimits(defaults);
        defaults.put(AugmentFortune.INSTANCE.getRegistryName(), 4);
        return defaults;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
