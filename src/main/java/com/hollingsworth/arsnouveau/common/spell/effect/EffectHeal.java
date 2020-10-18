package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectHeal extends AbstractEffect {
    public EffectHeal() {
        super(ModConfig.EffectHealID, "Heal");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof EntityRayTraceResult){

            if(((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
                LivingEntity entity = ((LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity());
                if(entity.removed || entity.getHealth() <= 0)
                    return;

                float maxHealth = entity.getMaxHealth();
                float healVal = 3.0f + 3 * getBuffCount(augments, AugmentAmplify.class);
                if(entity.getHealth() + healVal > maxHealth){
                    entity.setHealth(entity.getMaxHealth());
                }else{
                    entity.setHealth(entity.getHealth() + healVal);
                }
            }
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        return livingEntityHitSuccess(rayTraceResult);
    }

    @Override
    public int getManaCost() {
        return 40;
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
    protected String getBookDescription() {
        return "Heals a small amount of health for the target";
    }
}
