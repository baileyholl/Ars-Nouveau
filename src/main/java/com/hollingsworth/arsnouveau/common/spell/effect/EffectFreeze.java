package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
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
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        super.onResolveBlock(rayTraceResult, world, shooter, augments, spellContext);
        BlockPos pos = rayTraceResult.getBlockPos();
        for(BlockPos p : SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, getBuffCount(augments, AugmentAOE.class))){
            extinguishOrFreeze(world, p);
            for(Direction d : Direction.values()){
                extinguishOrFreeze(world, p.relative(d));
            }
        }

    }

    public void extinguishOrFreeze(World world, BlockPos p){
        BlockState state = world.getBlockState(p.above());
        FluidState fluidState = world.getFluidState(p.above());
        if(fluidState.getType() == Fluids.WATER && state.getBlock() instanceof FlowingFluidBlock){
            world.setBlockAndUpdate(p.above(), Blocks.ICE.defaultBlockState());
        }else if(state.getMaterial() == Material.FIRE){
            world.destroyBlock(p.above(), false);

        }
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        super.onResolveEntity(rayTraceResult, world, shooter, augments, spellContext);
        if(!(rayTraceResult.getEntity() instanceof LivingEntity))
            return;
        applyConfigPotion((LivingEntity) (rayTraceResult).getEntity(), Effects.MOVEMENT_SLOWDOWN, augments);
    }


    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 10);
        addExtendTimeConfig(builder, 5);
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
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
        return augments;
    }

    @Override
    public String getBookDescription() {
        return "Freezes water in a small area or slows a target for a short time. Can be augmented with AOE, Extend Time, or Amplify.";
    }
}
