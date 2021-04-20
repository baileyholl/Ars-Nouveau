package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.event.DispelEvent;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class EffectDispel extends AbstractEffect {
    public EffectDispel() {
        super(GlyphLib.EffectDispelID, "Dispel");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            if(((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
                LivingEntity entity = (LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity();
                Collection<EffectInstance> effects = entity.getActiveEffects();
                EffectInstance[] array = effects.toArray(new EffectInstance[effects.size()]);
                for(EffectInstance e : array){
                    if(e.isCurativeItem(new ItemStack(Items.MILK_BUCKET)))
                        entity.removeEffect(e.getEffect());
                }
                if(entity instanceof IDispellable && entity.isAlive() && entity.getHealth() > 0 && !entity.removed){
                    ((IDispellable) entity).onDispel(shooter);
                }
                MinecraftForge.EVENT_BUS.post(new DispelEvent(rayTraceResult, world, shooter, augments, spellContext));
            }
            return;
        }
        if(rayTraceResult instanceof BlockRayTraceResult && world.getBlockState(((BlockRayTraceResult) rayTraceResult).getBlockPos()) instanceof IDispellable){
            ((IDispellable) world.getBlockState(((BlockRayTraceResult) rayTraceResult).getBlockPos())).onDispel(shooter);
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return rayTraceResult instanceof EntityRayTraceResult || (rayTraceResult instanceof BlockRayTraceResult && world.getBlockState(((BlockRayTraceResult) rayTraceResult).getBlockPos()).getBlock() instanceof IDispellable);
    }

    @Override
    public int getManaCost() {
        return 30;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.MILK_BUCKET;
    }

    @Override
    public String getBookDescription() {
        return "Removes any potion effects on the target. When used on a witch at half health, the witch will vanish in return for a Wixie shard. Will also dispel tamed Whelps, Carbuncles, and Sylphs back into their charm.";
    }
}
