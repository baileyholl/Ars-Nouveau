package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectLight extends AbstractEffect {

    public EffectLight() {
        super(ModConfig.EffectLightID, "Light");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
            applyPotion((LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity(), Effects.NIGHT_VISION, augments);
        }

        if(rayTraceResult instanceof BlockRayTraceResult){
            BlockPos pos = ((BlockRayTraceResult) rayTraceResult).getPos().offset(((BlockRayTraceResult) rayTraceResult).getFace());
            if (world.getBlockState(pos).getMaterial() == Material.AIR && world.placedBlockWouldCollide(BlockRegistry.LIGHT_BLOCK.getDefaultState(), pos, ISelectionContext.dummy())) {
                world.setBlockState(pos, BlockRegistry.LIGHT_BLOCK.getDefaultState());
            }

        }
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    @Override
    public int getManaCost() {
        return 25;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.REDSTONE_LAMP;
    }

    @Override
    protected String getBookDescription() {
        return "If cast on a block, a permanent light source is created. When cast on an entity, the target will receive Night Vision.";
    }
}
