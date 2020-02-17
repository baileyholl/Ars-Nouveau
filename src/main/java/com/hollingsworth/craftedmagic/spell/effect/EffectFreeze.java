package com.hollingsworth.craftedmagic.spell.effect;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.AbstractEffect;
import com.hollingsworth.craftedmagic.api.spell.AugmentType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.ArrayList;

public class EffectFreeze extends AbstractEffect {
    public EffectFreeze() {
        super(ModConfig.EffectFreezeID, "Freeze");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, ArrayList<AugmentType> augments) {
        if(rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
            applyPotion((LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity(), Effects.SLOWNESS, augments, 10, 5);
        }
        else if (rayTraceResult instanceof BlockRayTraceResult) {
            System.out.println(rayTraceResult);
            BlockPos pos = ((BlockRayTraceResult) rayTraceResult).getPos();
            BlockState state = world.getBlockState(pos.up());
            if(state.getMaterial() == Material.WATER){
                world.setBlockState(pos.up(), Blocks.ICE.getDefaultState());
            }else if(state.getMaterial() == Material.FIRE){
                world.extinguishFire((PlayerEntity) shooter, pos, ((BlockRayTraceResult) rayTraceResult).getFace());
            }
        }else{
            RayTraceResult result = rayTrace(world, (PlayerEntity) shooter, RayTraceContext.FluidMode.SOURCE_ONLY);
            if (result instanceof BlockRayTraceResult) {
                BlockState state = world.getBlockState(((BlockRayTraceResult) result).getPos());
                if (state.getBlock().getDefaultState() == Blocks.WATER.getDefaultState()) {
                    world.setBlockState(((BlockRayTraceResult) result).getPos(), Blocks.ICE.getDefaultState());
                }else if(state.getBlock().getDefaultState() == Blocks.LAVA.getDefaultState()){
                    world.setBlockState(((BlockRayTraceResult) result).getPos(), Blocks.OBSIDIAN.getDefaultState());
                }
            }
        }

    }

    protected static RayTraceResult rayTrace(World worldIn, PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
        float f = player.rotationPitch;
        float f1 = player.rotationYaw;
        Vec3d vec3d = player.getEyePosition(1.0F);
        float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
        float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();;
        Vec3d vec3d1 = vec3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        return worldIn.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.OUTLINE, fluidMode, player));
    }

    @Override
    public int getManaCost() {
        return 15;
    }
}
