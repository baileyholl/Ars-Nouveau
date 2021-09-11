package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
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
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

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
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Entity entity = rayTraceResult.getEntity();
        if(entity.isOnFire()){
            entity.clearFire();
        }
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        int aoeBuff = spellStats.getBuffCount(AugmentAOE.INSTANCE);
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult,aoeBuff, spellStats.getBuffCount(AugmentPierce.INSTANCE));
        if(world.dimensionType().ultraWarm())
            return;
        for(BlockPos pos1 : posList) {
            BlockPos hitPos = pos1.relative(rayTraceResult.getDirection());
            if(!BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerWorld) world), world, pos1))
                continue;

            if(world.getBlockState(hitPos).canBeReplaced(Fluids.WATER)){
                world.setBlockAndUpdate(hitPos, Blocks.WATER.defaultBlockState());
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
