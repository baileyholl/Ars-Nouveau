package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.tile.LightTile;
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
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class EffectLight extends AbstractEffect {

    public EffectLight() {
        super(GlyphLib.EffectLightID, "Light");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
            if (shooter == null || !shooter.equals(((EntityRayTraceResult) rayTraceResult).getEntity())) {
                applyPotion((LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity(), Effects.GLOWING, augments);
            }
            applyPotion((LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity(), Effects.NIGHT_VISION, augments);
        }

        if(rayTraceResult instanceof BlockRayTraceResult){
            BlockPos pos = ((BlockRayTraceResult) rayTraceResult).getBlockPos().relative(((BlockRayTraceResult) rayTraceResult).getDirection());
            if(!BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerWorld) world), world, pos))
                return;
            if (world.getBlockState(pos).getMaterial() == Material.AIR && world.isUnobstructed(BlockRegistry.LIGHT_BLOCK.defaultBlockState(), pos, ISelectionContext.empty())) {
                world.setBlockAndUpdate(pos, BlockRegistry.LIGHT_BLOCK.defaultBlockState());
                LightTile tile = ((LightTile)world.getBlockEntity(pos));
                if(tile != null){
                    tile.red = spellContext.colors.r;
                    tile.green = spellContext.colors.g;
                    tile.blue = spellContext.colors.b;
                }
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
    public Item getCraftingReagent(){return Items.LANTERN;}

    @Override
    public String getBookDescription() {
        return "If cast on a block, a permanent light source is created. When cast on yourself, you will receive night vision. When cast on other entities, they will receive Night Vision and Glowing.";
    }
}
