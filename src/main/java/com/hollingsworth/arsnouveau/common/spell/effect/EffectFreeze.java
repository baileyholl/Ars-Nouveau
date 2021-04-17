package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;

public class EffectFreeze extends AbstractEffect {
    public EffectFreeze() {
        super(GlyphLib.EffectFreezeID, "Freeze");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
            applyPotion((LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity(), Effects.MOVEMENT_SLOWDOWN, augments, 10, 5);
        }
        else if (rayTraceResult instanceof BlockRayTraceResult) {
            BlockPos pos = ((BlockRayTraceResult) rayTraceResult).getBlockPos();
            BlockState state = world.getBlockState(pos.above());
            if(state.getMaterial() == Material.WATER){
                world.setBlockAndUpdate(pos.above(), Blocks.ICE.defaultBlockState());
            }else if(state.getMaterial() == Material.FIRE){
                world.destroyBlock(pos.above(), false);
            }
        }else if(shooter instanceof PlayerEntity){
            RayTraceResult result = rayTrace(world, (PlayerEntity)shooter, RayTraceContext.FluidMode.SOURCE_ONLY);
            if (result instanceof BlockRayTraceResult) {
                BlockState state = world.getBlockState(((BlockRayTraceResult) result).getBlockPos());
                if (state.getBlock().defaultBlockState() == Blocks.WATER.defaultBlockState()) {
                    world.setBlockAndUpdate(((BlockRayTraceResult) result).getBlockPos(), Blocks.ICE.defaultBlockState());
                }else if(state.getBlock().defaultBlockState() == Blocks.LAVA.defaultBlockState()){
                    world.setBlockAndUpdate(((BlockRayTraceResult) result).getBlockPos(), Blocks.OBSIDIAN.defaultBlockState());
                }
            }
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return nonAirAnythingSuccess(rayTraceResult, world);
    }

    protected static RayTraceResult rayTrace(World worldIn, PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
        float f = player.xRot;
        float f1 = player.yRot;
        Vector3d vec3d = player.getEyePosition(1.0F);
        float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
        float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        //
        double d0 = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();;
        Vector3d vec3d1 = vec3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        return worldIn.clip(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.OUTLINE, fluidMode, player));
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

    @Override
    public String getBookDescription() {
        return "Freezes water or slows a target for a short time.";
    }
}
