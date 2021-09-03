package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.event.DispelEvent;
import com.hollingsworth.arsnouveau.api.spell.*;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class EffectDispel extends AbstractEffect {
    public static EffectDispel INSTANCE = new EffectDispel();

    private EffectDispel() {
        super(GlyphLib.EffectDispelID, "Dispel");
    }


    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(rayTraceResult.getEntity() instanceof LivingEntity){
            LivingEntity entity = (LivingEntity) rayTraceResult.getEntity();
            Collection<EffectInstance> effects = entity.getActiveEffects();
            EffectInstance[] array = effects.toArray(new EffectInstance[effects.size()]);
            for(EffectInstance e : array){
                if(e.isCurativeItem(new ItemStack(Items.MILK_BUCKET)))
                    entity.removeEffect(e.getEffect());
            }
            if(entity instanceof IDispellable && entity.isAlive() && entity.getHealth() > 0 && !entity.removed){
                ((IDispellable) entity).onDispel(shooter);
            }
            MinecraftForge.EVENT_BUS.post(new DispelEvent(rayTraceResult, world, shooter, spellStats.getAugments(), spellContext));
        }
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(world.getBlockState(rayTraceResult.getBlockPos()) instanceof IDispellable){
            ((IDispellable) world.getBlockState(rayTraceResult.getBlockPos())).onDispel(shooter);
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

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        // Augments were sent with the DispelEvent, but there's no use of its augments field.
        return augmentSetOf();
    }

    @Override
    public String getBookDescription() {
        return "Removes any potion effects on the target. When used on a witch at half health, the witch will vanish in return for a Wixie shard. Will also dispel tamed summons back into their charm.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ABJURATION);
    }
}
