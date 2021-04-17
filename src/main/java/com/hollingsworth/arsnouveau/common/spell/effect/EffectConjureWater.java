package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import com.hollingsworth.arsnouveau.api.spell.ISpellTier.Tier;

public class EffectConjureWater extends AbstractEffect {
    public EffectConjureWater() {
        super(GlyphLib.EffectConjureWaterID, "Conjure Water");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            Entity entity = ((EntityRayTraceResult) rayTraceResult).getEntity();
            if(entity.isOnFire()){
                entity.clearFire();
            }
        }

        if(!(rayTraceResult instanceof BlockRayTraceResult))
            return;
        int aoeBuff = getBuffCount(augments, AugmentAOE.class);
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, ((BlockRayTraceResult) rayTraceResult).getBlockPos(), (BlockRayTraceResult)rayTraceResult,aoeBuff, getBuffCount(augments, AugmentPierce.class));
        BlockRayTraceResult result = (BlockRayTraceResult) rayTraceResult;
        if(world.dimensionType().ultraWarm())
            return;
        for(BlockPos pos1 : posList) {
            BlockPos hitPos = pos1.relative(result.getDirection());
            if(world.getBlockState(hitPos).canBeReplaced(Fluids.WATER)){
                world.setBlockAndUpdate(hitPos, Blocks.WATER.defaultBlockState());
            }
        }
    }

    @Override
    public int getManaCost() {
        return 80;
    }

    @Override
    public String getBookDescription() {
        return "Places water at a location. Can be augmented with AOE.";
    }

    @Override
    public Item getCraftingReagent() {
        return Items.WATER_BUCKET;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }
}
