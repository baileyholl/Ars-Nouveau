package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectGravity extends AbstractEffect {
    public static EffectGravity INSTANCE = new EffectGravity();

    private EffectGravity() {
        super(GlyphLib.EffectGravityID, "Gravity");
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        super.onResolveBlock(rayTraceResult, world, shooter, augments, spellContext);
        BlockPos pos = rayTraceResult.getBlockPos();
        int aoeBuff = getBuffCount(augments, AugmentAOE.class);
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult,aoeBuff, getBuffCount(augments, AugmentPierce.class));
        for(BlockPos pos1 : posList) {
            if(world.getBlockEntity(pos1) != null || !canBlockBeHarvested(augments, world, pos1) || !BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerWorld) world), world, pos1))
                continue;

            FallingBlockEntity blockEntity = new FallingBlockEntity(world,pos1.getX() +0.5, pos1.getY(), pos1.getZ() +0.5, world.getBlockState(pos1));
            world.addFreshEntity(blockEntity);
        }
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        super.onResolveEntity(rayTraceResult, world, shooter, augments, spellContext);
        Entity entity = ((EntityRayTraceResult) rayTraceResult).getEntity();
        entity.setDeltaMovement(entity.getDeltaMovement().add(0, -1.0 - getAmplificationBonus(augments), 0));
        entity.hurtMarked = true;
    }

    @Override
    public int getManaCost() {
        return 15;
    }

    @Override
    public Item getCraftingReagent() {
        return Items.ANVIL;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentAOE.INSTANCE,
                AugmentPierce.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Causes blocks and entities to fall.";
    }
}
