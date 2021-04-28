package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class EffectPhantomBlock extends AbstractEffect {

    public EffectPhantomBlock() {
        super(GlyphLib.EffectPhantomBlockID, "Phantom");
    }


    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        super.onResolveBlock(rayTraceResult, world, shooter, augments, spellContext);
        for(BlockPos pos : SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, getBuffCount(augments, AugmentAOE.class), getBuffCount(augments, AugmentPierce.class))) {
            pos =  rayTraceResult.isInside() ? pos : pos.relative(( rayTraceResult).getDirection());
            if(!BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerWorld) world), world, pos))
                continue;
            if (world.getBlockState(pos).getMaterial() == Material.AIR && world.isUnobstructed(BlockRegistry.PHANTOM_BLOCK.defaultBlockState(), pos, ISelectionContext.empty())) {
                world.setBlockAndUpdate(pos, BlockRegistry.PHANTOM_BLOCK.defaultBlockState());
            }
        }
    }


    @Override
    public int getManaCost() {
        return 5;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.GLASS;
    }

    @Override
    public String getBookDescription() {
        return "Creates a temporary block that will disappear after a short time.";
    }
}
