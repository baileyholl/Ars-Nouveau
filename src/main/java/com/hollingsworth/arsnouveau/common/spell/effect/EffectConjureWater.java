package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectConjureWater extends AbstractEffect {

    public static EffectConjureWater INSTANCE = new EffectConjureWater();

    private EffectConjureWater() {
        super(GlyphLib.EffectConjureWaterID, "Conjure Water");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Entity entity = rayTraceResult.getEntity();
        if(entity.isOnFire()){
            entity.clearFire();
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        int aoeBuff = spellStats.getBuffCount(AugmentAOE.INSTANCE);
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult,aoeBuff, spellStats.getBuffCount(AugmentPierce.INSTANCE));
        if(world.dimensionType().ultraWarm())
            return;
        for(BlockPos pos1 : posList) {
            Block block = world.getBlockState(pos1).getBlock();
            if(block instanceof SimpleWaterloggedBlock && ((SimpleWaterloggedBlock) block).canPlaceLiquid(world, pos1, world.getBlockState(pos1), Fluids.WATER)){
                ((SimpleWaterloggedBlock) block).placeLiquid(world, pos1, world.getBlockState(pos1), Fluids.WATER.getSource(true));
            }else{
                BlockPos hitPos = pos1.relative(rayTraceResult.getDirection());
                if(!BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, pos1))
                    continue;

                if(world.getBlockState(hitPos).canBeReplaced(Fluids.WATER)){
                    world.setBlockAndUpdate(hitPos, Blocks.WATER.defaultBlockState());
                }
            }
        }
    }

    @Override
    public int getManaCost() {
        return 80;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE, AugmentPierce.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Places water at a location or extinguishes entities on fire.";
    }

    @Override
    public Item getCraftingReagent() {
        return Items.WATER_BUCKET;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_WATER);
    }
}
