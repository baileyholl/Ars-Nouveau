package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.RedstoneAir;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectRedstone extends AbstractEffect {
    public static EffectRedstone INSTANCE = new EffectRedstone();

    private EffectRedstone() {
        super(GlyphLib.EffectRedstoneID, "Redstone Signal");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        BlockState state = BlockRegistry.REDSTONE_AIR.defaultBlockState();
        int signalModifier = (int) spellStats.getAmpMultiplier() + 10;
        if(signalModifier < 1)
            signalModifier = 1;
        if(signalModifier > 15)
            signalModifier = 15;
        state = state.setValue(RedstoneAir.POWER, signalModifier);
        BlockPos pos = rayTraceResult.getBlockPos().relative(rayTraceResult.getDirection());
        if(!(world.getBlockState(pos).getMaterial() == Material.AIR && world.getBlockState(pos).getBlock() != BlockRegistry.REDSTONE_AIR)){
            return;
        }
        int timeBonus = (int) spellStats.getDurationMultiplier();
        world.setBlockAndUpdate(pos, state);
        world.scheduleTick(pos, state.getBlock(), GENERIC_INT.get() + timeBonus * BONUS_TIME.get());
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

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.REDSTONE_BLOCK;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentExtendTime.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Creates a brief redstone signal on a block, like a button. The signal starts at strength 10, and may be increased with Amplify, or decreased with Dampen. The duration may be extended with Extend Time.";
    }

    @Override
    public int getManaCost() {
        return 0;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
