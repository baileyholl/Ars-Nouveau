package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class MethodBeam extends AbstractCastMethod {
    public MethodBeam() {
        super(ModConfig.MethodBeamID, "Beam");
    }

    @Override
    public void onCast(@Nullable ItemStack stack, LivingEntity playerEntity, World world, ArrayList<AbstractAugment> augments) {
        if(playerEntity instanceof PlayerEntity) {
            System.out.println(playerEntity.getEyeHeight());
            RayTraceResult result = world.rayTraceBlocks(new RayTraceContext(playerEntity.getPositionVec().add(0.5, playerEntity.getEyeHeight(), 0.5), playerEntity.getPositionVec().add(playerEntity.getLookVec().scale(5)), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, playerEntity));

            System.out.println(result.getHitVec());
            ParticleUtil.beam(new BlockPos(playerEntity.getPositionVec().add(0.5, playerEntity.getEyeHeight(), 0.5)),new BlockPos(result.getHitVec()), world);
        }
    }

    @Override
    public void onCastOnBlock(ItemUseContext context, ArrayList<AbstractAugment> augments) {

    }

    @Override
    public void onCastOnBlock(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, ArrayList<AbstractAugment> augments) {

    }

    @Override
    public void onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, ArrayList<AbstractAugment> augments) {

    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
