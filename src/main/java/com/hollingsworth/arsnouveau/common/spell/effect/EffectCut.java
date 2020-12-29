package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IForgeShearable;

import javax.annotation.Nullable;
import java.util.List;

public class EffectCut extends AbstractEffect {
    public EffectCut() {
        super(ModConfig.EffectCutID, "Cut");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            Entity entity = ((EntityRayTraceResult) rayTraceResult).getEntity();
            if(entity instanceof IForgeShearable){
                IForgeShearable shearable = (IForgeShearable) entity;
                ItemStack shears = new ItemStack(Items.SHEARS);
                applyEnchantments(augments, shears);
                if(shearable.isShearable(shears, world, entity.getPosition())){
                    List<ItemStack> items = shearable.onSheared(getPlayer(shooter, (ServerWorld) world), shears, world, entity.getPosition(), getBuffCount(augments, AugmentFortune.class));
                    items.forEach(i->world.addEntity(new ItemEntity(world, entity.getPosX(), entity.getPosY(), entity.getPosZ(), i)));
                }
            }else{
                dealDamage(world, shooter, 1.0f + getAmplificationBonus(augments), augments, entity, DamageSource.causePlayerDamage(getPlayer(shooter, (ServerWorld) world)));
            }
        }
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world,  LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        for(BlockPos p : SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getPos(), rayTraceResult, getBuffCount(augments, AugmentAOE.class))) {
            if (world.getBlockState(p).getBlock() instanceof IForgeShearable) {
                ItemStack shears = new ItemStack(Items.SHEARS);
                IForgeShearable shearable = (IForgeShearable) world.getBlockState(p).getBlock();
                applyEnchantments(augments, shears);
                if (shearable.isShearable(shears, world,p)) {
                    List<ItemStack> items = shearable.onSheared(getPlayer(shooter, (ServerWorld) world), shears, world, p, getBuffCount(augments, AugmentFortune.class));
                    items.forEach(i -> world.addEntity(new ItemEntity(world, p.getX(), p.getY(),p.getZ(), i)));
                }
            }
        }
    }

    @Override
    protected String getBookDescription() {
        return "Shears entities, or damages non-shearable entities for a small amount. Costs nothing.";
    }

    @Override
    public int getManaCost() {
        return 0;
    }

    @Override
    public Item getCraftingReagent() {
        return Items.SHEARS;
    }
}
