package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtract;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class EffectFell extends AbstractEffect {
    public static EffectFell INSTANCE = new EffectFell();


    private EffectFell() {
        super(GlyphLib.EffectFellID, "Fell");
    }

    @Override
    public void onResolveBlock(BlockHitResult ray, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos blockPos = ray.getBlockPos();
        BlockState state = world.getBlockState(blockPos);
        if (isTree(state)) {
            Set<BlockPos> list = getTree(world, blockPos, (int) (GENERIC_INT.get() + Math.round(AOE_BONUS.get() * spellStats.getAoeMultiplier())));
            world.levelEvent(2001, blockPos, Block.getId(state));
            list.forEach(listPos -> {
                if (!BlockUtil.destroyRespectsClaim(shooter, world, listPos))
                    return;
                if (spellStats.hasBuff(AugmentExtract.INSTANCE)) {
                    world.getBlockState(listPos).getDrops(LootUtil.getSilkContext((ServerLevel) world, listPos, shooter)).forEach(i -> world.addFreshEntity(new ItemEntity(world, listPos.getX(), listPos.getY(), listPos.getZ(), i)));
                    BlockUtil.destroyBlockSafelyWithoutSound(world, listPos, false, shooter);
                } else if (spellStats.hasBuff(AugmentFortune.INSTANCE)) {
                    world.getBlockState(listPos)
                            .getDrops(LootUtil.getFortuneContext((ServerLevel) world, listPos, shooter, spellStats.getBuffCount(AugmentFortune.INSTANCE)))
                            .forEach(i -> world.addFreshEntity(new ItemEntity(world, listPos.getX(), listPos.getY(), listPos.getZ(), i)));
                    BlockUtil.destroyBlockSafelyWithoutSound(world, listPos, false, shooter);
                } else {
                    BlockUtil.destroyBlockSafelyWithoutSound(world, listPos, true, shooter);
                }
            });
        }
    }

    public ModConfigSpec.IntValue AOE_BONUS;

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericInt(builder, 50, "Base amount of harvested blocks", "base_harvest");
        AOE_BONUS = builder.comment("Additional max blocks per AOE").defineInRange("aoe_bonus", 50, 0, Integer.MAX_VALUE);
    }

    public boolean isTree(BlockState blockstate) {
        return blockstate.is(BlockTagProvider.FELLABLE);
    }

    public Set<BlockPos> getTree(Level world, BlockPos start, int maxBlocks) {
        return SpellUtil.DFSBlockstates(world, start, maxBlocks, this::isTree);
    }


    @Override
    public int getDefaultManaCost() {
        return 150;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.ONE;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAOE.INSTANCE,
                AugmentExtract.INSTANCE,
                AugmentFortune.INSTANCE,
                AugmentAmplify.INSTANCE
        );
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addBlockAoeAugmentDescriptions(map);
    }

    @Override
    public String getBookDescription() {
        return "Harvests entire trees, mushrooms, cactus, and other vegetation. Can be amplified with Amplify to break materials of higher hardness. AOE will increase the number of blocks that may be broken at one time.";
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
