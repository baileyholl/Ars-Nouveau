package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.ILightable;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.SconceBlock;
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
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        super.onResolveEntity(rayTraceResult, world, shooter, augments, spellContext);
        if(!(rayTraceResult.getEntity() instanceof LivingEntity))
            return;
        if (shooter == null || !shooter.equals(rayTraceResult.getEntity())) {
            applyPotion((LivingEntity) rayTraceResult.getEntity(), Effects.GLOWING, augments);
        }
        applyPotion((LivingEntity) rayTraceResult.getEntity(), Effects.NIGHT_VISION, augments);
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        super.onResolveBlock(rayTraceResult, world, shooter, augments, spellContext);
        BlockPos pos = rayTraceResult.getBlockPos().relative(rayTraceResult.getDirection());
        if(!BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerWorld) world), world, pos))
            return;

        if(world.getBlockEntity( rayTraceResult.getBlockPos()) instanceof ILightable){
            ((ILightable) world.getBlockEntity(rayTraceResult.getBlockPos())).onLight(rayTraceResult, world, shooter, augments, spellContext);
            return;
        }

        if (world.getBlockState(pos).getMaterial() == Material.AIR && world.isUnobstructed(BlockRegistry.LIGHT_BLOCK.defaultBlockState(), pos, ISelectionContext.empty())) {
            world.setBlockAndUpdate(pos, BlockRegistry.LIGHT_BLOCK.defaultBlockState().setValue(SconceBlock.LIGHT_LEVEL, Math.max(0,Math.min(15, 14 + getAmplificationBonus(augments)))));
            LightTile tile = ((LightTile)world.getBlockEntity(pos));
            if(tile != null){
                tile.red = spellContext.colors.r;
                tile.green = spellContext.colors.g;
                tile.blue = spellContext.colors.b;
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
        return "If cast on a block, a permanent light source is created. May be amplified up to Glowstone brightness, or Dampened for a lower light level. When cast on yourself, you will receive night vision. When cast on other entities, they will receive Night Vision and Glowing.";
    }
}
