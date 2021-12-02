package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EffectFreeze extends AbstractEffect {
    public static EffectFreeze INSTANCE = new EffectFreeze();

    private EffectFreeze() {
        super(GlyphLib.EffectFreezeID, "Freeze");
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        BlockPos pos = rayTraceResult.getBlockPos();
        for(BlockPos p : SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, spellStats.getBuffCount(AugmentAOE.INSTANCE), spellStats.getBuffCount(AugmentPierce.INSTANCE))){
            extinguishOrFreeze(world, p);
            for(Direction d : Direction.values()){
                extinguishOrFreeze(world, p.relative(d));
            }
        }
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(!(rayTraceResult.getEntity() instanceof LivingEntity))
            return;
        applyConfigPotion((LivingEntity) (rayTraceResult).getEntity(), MobEffects.MOVEMENT_SLOWDOWN, spellStats);
    }

    public void extinguishOrFreeze(Level world, BlockPos p){
        BlockState state = world.getBlockState(p.above());
        FluidState fluidState = world.getFluidState(p.above());
        if(fluidState.getType() == Fluids.WATER && state.getBlock() instanceof LiquidBlock){
            world.setBlockAndUpdate(p.above(), Blocks.ICE.defaultBlockState());
        }
        else if(fluidState.getType() == Fluids.LAVA && state.getBlock() instanceof LiquidBlock){
            world.setBlockAndUpdate(p.above(), Blocks.OBSIDIAN.defaultBlockState());
        }else if(fluidState.getType() == Fluids.FLOWING_LAVA && state.getBlock() instanceof LiquidBlock){
            world.setBlockAndUpdate(p.above(), Blocks.COBBLESTONE.defaultBlockState());
        }
        else if(state.getMaterial() == Material.FIRE){
            world.destroyBlock(p.above(), false);

        }
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 10);
        addExtendTimeConfig(builder, 5);
    }

    @Override
    public boolean wouldSucceed(HitResult rayTraceResult, Level world, LivingEntity shooter, List<AbstractAugment> augments) {
        return nonAirAnythingSuccess(rayTraceResult, world);
    }

    @Override
    public int getManaCost() {
        return 15;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.SNOW_BLOCK;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        Set<AbstractAugment> augments = new HashSet<>(POTION_AUGMENTS);
        augments.add(AugmentAOE.INSTANCE);
        augments.add(AugmentPierce.INSTANCE);
        return augments;
    }

    @Override
    public String getBookDescription() {
        return "Freezes water or lava in a small area or slows a target for a short time.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_WATER);
    }
}
