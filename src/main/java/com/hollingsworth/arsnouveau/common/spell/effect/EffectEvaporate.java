package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectEvaporate extends AbstractEffect {

    public static EffectEvaporate INSTANCE = new EffectEvaporate();

    private EffectEvaporate() {
        super(GlyphLib.EffectEvaporate, "Evaporate");
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        super.onResolveBlock(rayTraceResult, world, shooter, spellStats, spellContext);
        BlockPos pos = rayTraceResult.getBlockPos();
        for(BlockPos p : SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, spellStats.getBuffCount(AugmentAOE.INSTANCE), spellStats.getBuffCount(AugmentPierce.INSTANCE))){
            evaporate(world, p);
            for(Direction d : Direction.values()){
                evaporate(world, p.relative(d));
            }
        }
    }

    public void evaporate(World world, BlockPos p){
        if(!world.getFluidState(p).isEmpty()){
            world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    @Override
    public String getBookDescription() {
        return "Deletes fluids in an area. Can be expanded with AOE.";
    }

    @Override
    public int getManaCost() {
        return 50;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.SPONGE;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return setOf(AugmentAOE.INSTANCE);
    }
}
