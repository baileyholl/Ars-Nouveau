package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.IEffectResolvePerk;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class ChillingPerk extends Perk implements IEffectResolvePerk {
    public static ChillingPerk INSTANCE = new ChillingPerk(ArsNouveau.prefix( "thread_chilling"));

    public ChillingPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public String getLangDescription() {
        return "Damaging effects inflict Freezing on the target before the spell resolves. Freezing lasts for 10 seconds per level, and becomes Freezing 2 at a level 3 slot.";
    }

    @Override
    public String getLangName() {
        return "Chilling";
    }

    @Override
    public void onPreResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, AbstractEffect effect, PerkInstance perkInstance) {
        if(effect instanceof IDamageEffect damageEffect && rayTraceResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof LivingEntity livingEntity){
            if(damageEffect.canDamage(shooter, spellStats, spellContext, resolver, entityHitResult.getEntity()) && shooter != entityHitResult.getEntity()){
                livingEntity.setTicksFrozen(livingEntity.getTicksFrozen() + 1);
                livingEntity.addEffect(new MobEffectInstance(ModPotions.FREEZING_EFFECT.get(), perkInstance.getSlot().value * 10 * 20, perkInstance.getSlot().value >= 3 ? 2 : 1));
            }
        }
    }

    @Override
    public void onPostResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, AbstractEffect effect, PerkInstance perkInstance) {

    }
}
