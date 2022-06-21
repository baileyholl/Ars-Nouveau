package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EffectFreeze extends AbstractEffect {
    public static EffectFreeze INSTANCE = new EffectFreeze();

    private EffectFreeze() {
        super(GlyphLib.EffectFreezeID, "Freeze");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getBlockPos();
        for(BlockPos p : SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, spellStats.getAoeMultiplier(), spellStats.getBuffCount(AugmentPierce.INSTANCE))){
            BlockPos affectedPos = extinguishOrFreeze(world, p, spellStats);
            if(affectedPos != null){
                ShapersFocus.tryPropagateBlockSpell(
                        new BlockHitResult(new Vec3(affectedPos.getX(), affectedPos.getY(), affectedPos.getZ()),
                                rayTraceResult.getDirection(), affectedPos, false),
                        world, shooter, spellContext, resolver);
            }
            for(Direction d : Direction.values()){
                BlockPos relative = p.relative(d);
                affectedPos = extinguishOrFreeze(world, relative, spellStats);
                if(affectedPos != null)
                    ShapersFocus.tryPropagateBlockSpell(
                            new BlockHitResult(new Vec3(affectedPos.getX(), affectedPos.getY(), affectedPos.getZ()),
                                   rayTraceResult.getDirection(), affectedPos, false),
                            world, shooter, spellContext, resolver);
            }
        }
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if(!(rayTraceResult.getEntity() instanceof LivingEntity))
            return;
        applyConfigPotion((LivingEntity) (rayTraceResult).getEntity(), MobEffects.MOVEMENT_SLOWDOWN, spellStats);
    }

    /**
     * Returns a position if a block was changed.
     * Returns null if nothing happened.
     */
    public @Nullable BlockPos extinguishOrFreeze(Level world, BlockPos p, SpellStats spellStats){
        BlockState hitState = world.getBlockState(p);
        BlockState aboveState = world.getBlockState(p.above());
        FluidState aboveFluidstate = world.getFluidState(p.above());
        if(aboveFluidstate.getType() == Fluids.WATER && aboveState.getBlock() instanceof LiquidBlock){
            if(spellStats.hasBuff(AugmentSensitive.INSTANCE)){
                world.setBlockAndUpdate(p.above(), Blocks.FROSTED_ICE.defaultBlockState());
            }else{
                world.setBlockAndUpdate(p.above(), Blocks.ICE.defaultBlockState());
            }
            return p.above();
        }else if(aboveFluidstate.getType() == Fluids.LAVA && aboveState.getBlock() instanceof LiquidBlock){
            world.setBlockAndUpdate(p.above(), Blocks.OBSIDIAN.defaultBlockState());
            return p.above();
        }else if(aboveFluidstate.getType() == Fluids.FLOWING_LAVA && aboveState.getBlock() instanceof LiquidBlock){
            world.setBlockAndUpdate(p.above(), Blocks.COBBLESTONE.defaultBlockState());
            return p.above();
        }
        else if(aboveState.getMaterial() == Material.FIRE){
            world.destroyBlock(p.above(), false);
            return p.above();
        }else if(hitState.getBlock() == Blocks.ICE){
            world.setBlock(p, Blocks.PACKED_ICE.defaultBlockState(), 3);
            return p;
        }else if(hitState.getBlock() == Blocks.PACKED_ICE){
            world.setBlock(p, Blocks.BLUE_ICE.defaultBlockState(), 3);
            return p;
        }else{
            return null;
        }
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 10);
        addExtendTimeConfig(builder, 5);
    }

    @Override
    public int getDefaultManaCost() {
        return 15;
    }

    @Override
    protected Map<String, Integer> getDefaultAugmentLimits(Map<String, Integer> defaults) {
        Map<String, Integer> map = new HashMap<>();
        map.put(GlyphLib.AugmentSensitiveID, 1);
        return map;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        Set<AbstractAugment> augments = new HashSet<>(getPotionAugments());
        augments.add(AugmentAOE.INSTANCE);
        augments.add(AugmentPierce.INSTANCE);
        augments.add(AugmentSensitive.INSTANCE);
        return augments;
    }

    @Override
    public String getBookDescription() {
        return "Freezes water or lava in a small area or slows a target for a short time. Freeze on Ice will turn it into Packed Ice, and Packed Ice into Blue Ice. Sensitive will turn water into Frosted Ice and will vanish after a short time.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_WATER);
    }
}
