package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import com.hollingsworth.arsnouveau.api.spell.ISpellTier.Tier;

public class EffectWither extends AbstractEffect {
    public EffectWither() {
        super(GlyphLib.EffectWitherID, "Wither");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof EntityRayTraceResult){
            Entity entity = ((EntityRayTraceResult) rayTraceResult).getEntity();
            if(!(entity instanceof LivingEntity))
                return;
            applyPotion((LivingEntity) entity, Effects.WITHER,augments);
        }
    }

    @Override
    public int getManaCost() {
        return 50;
    }

    @Override
    public Item getCraftingReagent() {
        return Items.WITHER_SKELETON_SKULL;
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }

    @Override
    public String getBookDescription() {
        return "Applies the Wither debuff.";
    }
}
