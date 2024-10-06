package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.block.TemporaryBlock;
import com.hollingsworth.arsnouveau.common.block.tile.TemporaryTile;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.world.saved_data.RedstoneSavedData;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EffectRedstone extends AbstractEffect {
    public static EffectRedstone INSTANCE = new EffectRedstone();
    private EffectRedstone() {
        super(GlyphLib.EffectRedstoneID, "Redstone Signal");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        int signalModifier = Mth.clamp((int) spellStats.getAmpMultiplier() + 10, 1, 15);
        int timeBonus = (int) spellStats.getDurationMultiplier();
        int delay = Math.max(GENERIC_INT.get() + timeBonus * BONUS_TIME.get(), 2);
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats);
        FakePlayer fakePlayer = ANFakePlayer.getOrFakePlayer((ServerLevel) world, shooter);
        for (BlockPos pos1 : posList) {
            if (spellStats.isSensitive()) {
                if (!world.isInWorldBounds(pos1))
                    return;
                pos1 = pos1.immutable();
                RedstoneSavedData.from((ServerLevel) world).SIGNAL_MAP.put(pos1, new RedstoneSavedData.Entry(pos1, signalModifier, delay));
                world.neighborChanged(pos1, world.getBlockState(pos1).getBlock(), pos1);
                world.updateNeighborsAt(pos1, world.getBlockState(pos1).getBlock());
            } else {

                pos1 = pos1.relative(rayTraceResult.getDirection());

                if (!world.isInWorldBounds(pos1))
                    return;
                boolean notReplaceable = !world.getBlockState(pos1).canBeReplaced();
                if (notReplaceable)
                    continue;
                var event = NeoForge.EVENT_BUS.post(new BlockEvent.EntityPlaceEvent(BlockSnapshot.create(world.dimension(), world, pos1), world.getBlockState(pos1), fakePlayer));
                if(event.isCanceled()){
                    continue;
                }
                BlockState state1 = BlockRegistry.TEMPORARY_BLOCK.get().defaultBlockState().setValue(TemporaryBlock.POWER, signalModifier);
                world.setBlockAndUpdate(pos1, state1);
                if(world.getBlockEntity(pos1) instanceof TemporaryTile tile){
                    tile.gameTime = world.getGameTime();
                    tile.tickDuration = delay;
                    tile.mimicState = Blocks.REDSTONE_BLOCK.defaultBlockState();
                    tile.updateBlock();
                }
                world.sendBlockUpdated(pos1, world.getBlockState(pos1), world.getBlockState(pos1), 2);
            }
        }

    }

    public ModConfigSpec.IntValue BONUS_TIME;

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericInt(builder, 5, "Base time in ticks", "base_duration");
        BONUS_TIME = builder.comment("Extend time bonus, in ticks").defineInRange("extend_time", 10, 0, Integer.MAX_VALUE);
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentAOE.INSTANCE, AugmentDampen.INSTANCE, AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentSensitive.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Places a temporary block of redstone with configurable power and duration. Augment with Sensitive to set the target block as a power source for itself and surrounding blocks. Dampen and Amplify will adjust the power from the base value of 10.";
    }

    @Override
    public int getDefaultManaCost() {
        return 0;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    public static final Map<String, Map<BlockPos, Integer>> signalMap = new ConcurrentHashMap<>();

}
