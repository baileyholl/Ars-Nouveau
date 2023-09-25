package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.RedstoneSpellEvent;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.RedstoneAir;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

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

        BlockState state = BlockRegistry.REDSTONE_AIR.defaultBlockState().setValue(RedstoneAir.POWER, signalModifier);
        BlockPos pos = rayTraceResult.getBlockPos();

        int timeBonus = (int) spellStats.getDurationMultiplier();
        int delay = Math.max(GENERIC_INT.get() + timeBonus * BONUS_TIME.get(), 2);

        if (spellStats.isSensitive()) {
            if (!world.isInWorldBounds(pos))
                return;
            signalMap.get(world.dimension().location().toString()).put(pos, signalModifier);
            EventQueue.getServerInstance().addEvent(new RedstoneSpellEvent(delay, rayTraceResult, world));
        } else {

            pos = pos.relative(rayTraceResult.getDirection());

            if (!(world.getBlockState(pos).getMaterial() == Material.AIR && world.getBlockState(pos).getBlock() != BlockRegistry.REDSTONE_AIR)) {
                return;
            }
            if (!world.isInWorldBounds(pos))
                return;
            world.setBlockAndUpdate(pos, state);
            world.scheduleTick(pos, state.getBlock(), delay);
        }

        BlockPos hitPos = pos.relative(rayTraceResult.getDirection().getOpposite());

        BlockUtil.safelyUpdateState(world, pos);
        world.updateNeighborsAt(pos, state.getBlock());
        world.updateNeighborsAt(hitPos, state.getBlock());

    }

    public ForgeConfigSpec.IntValue BONUS_TIME;

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericInt(builder, 5, "Base time in ticks", "base_duration");
        BONUS_TIME = builder.comment("Extend time bonus, in ticks").defineInRange("extend_time", 10, 0, Integer.MAX_VALUE);
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE, AugmentSensitive.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Creates a brief redstone signal on a block, like a button. The signal starts at strength 10, and may be increased with Amplify, or decreased with Dampen. The duration may be extended with Extend Time or shortened with Duration Down.";
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
