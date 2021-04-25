package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectHeal extends AbstractEffect {
    public EffectHeal() {
        super(GlyphLib.EffectHealID, "Heal");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
            LivingEntity entity = ((LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity());
            if(entity.removed || entity.getHealth() <= 0)
                return;

            float healVal = 3.0f + 3 * getBuffCount(augments, AugmentAmplify.class);
            if(getBuffCount(augments, AugmentExtendTime.class) > 0){
                applyPotionWithCap(entity, Effects.REGENERATION, augments, 5, 5, 5);
            }else{
                if(entity.isInvertedHealAndHarm()){
                    dealDamage(world, shooter, healVal, augments, entity, buildDamageSource(world, shooter).setMagic());
                }else{
                    entity.heal(healVal);
                }
            }

        }

    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return livingEntityHitSuccess(rayTraceResult);
    }

    @Override
    public int getManaCost() {
        return 100;
    }

    @Override
    public Tier getTier() {
        return Tier.TWO;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.GLISTERING_MELON_SLICE;
    }

    @Override
    public String getBookDescription() {
        return "Heals a small amount of health for the target. When used with Extend Time, the Regeneration buff is applied instead, up to level 5. When used on Undead, the spell will deal an equal amount of magic damage.";
    }
}
