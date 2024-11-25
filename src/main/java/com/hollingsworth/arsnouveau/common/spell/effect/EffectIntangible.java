package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.block.tile.IntangibleAirTile;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EffectIntangible extends AbstractEffect {
    public static EffectIntangible INSTANCE = new EffectIntangible();

    private EffectIntangible() {
        super(GlyphLib.EffectIntangibleID, "Intangible");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getBlockPos();
        int duration = (int) (GENERIC_INT.get() + EXTEND_TIME.get() * spellStats.getDurationMultiplier());

        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, spellStats);
        for (BlockPos pos1 : posList) {
            if (world.getBlockEntity(pos1) != null || world.getBlockState(pos1).isAir()
                    || world.getBlockState(pos1).getBlock() == Blocks.BEDROCK || !canBlockBeHarvested(spellStats, world, pos) || !BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, pos1))
                continue;

            BlockState state = world.getBlockState(pos1);
            int id = Block.getId(state);
            world.setBlockAndUpdate(pos1, BlockRegistry.INTANGIBLE_AIR.defaultBlockState());
            IntangibleAirTile tile = ((IntangibleAirTile) world.getBlockEntity(pos1));
            if(tile != null) {
                tile.stateID = id;
                tile.maxLength = duration * 20;
            }
        }
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericInt(builder, 3, "Base duration, in seconds", "base");
        addExtendTimeConfig(builder, 1);
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

    @Override
    public int getDefaultManaCost() {
        return 30;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentExtendTime.INSTANCE,
                AugmentPierce.INSTANCE,
                AugmentAOE.INSTANCE, AugmentDurationDown.INSTANCE
        );
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addPotionAugmentDescriptions(map);
        addBlockAoeAugmentDescriptions(map);
        map.put(AugmentAmplify.INSTANCE, "Increases the hardness of blocks that can be affected.");
        map.put(AugmentDampen.INSTANCE, "Decreases the hardness of blocks that can be affected.");
    }

    @Override
    public String getBookDescription() {
        return "Causes blocks to temporarily turn into air. Can be modified with Amplify for blocks of higher hardness, AOE, Duration Down, and Extend Time.";
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
