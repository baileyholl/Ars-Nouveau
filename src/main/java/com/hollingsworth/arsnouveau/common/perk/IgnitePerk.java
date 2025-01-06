package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.EffectResolveEvent;
import com.hollingsworth.arsnouveau.api.perk.IEffectResolvePerk;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.spell.IDamageEffect;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.EntityHitResult;

public class IgnitePerk extends Perk implements IEffectResolvePerk {
    public static IgnitePerk INSTANCE = new IgnitePerk(ArsNouveau.prefix("thread_kindling"));

    public IgnitePerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public String getLangDescription() {
        return "Damaging effects cause the target to burn for a short duration before the effect resolves. Burn time is increased by 5 seconds per level.";
    }

    @Override
    public String getLangName() {
        return "Kindling";
    }

    @Override
    public void onEffectPreResolve(EffectResolveEvent.Pre event, PerkInstance perkInstance) {
        if (event.resolveEffect instanceof IDamageEffect damageEffect && event.rayTraceResult instanceof EntityHitResult entityHitResult) {
            if (damageEffect.canDamage(event.shooter, event.spellStats, event.resolver.spellContext, event.resolver, entityHitResult.getEntity()) && event.shooter != entityHitResult.getEntity()) {
                entityHitResult.getEntity().setRemainingFireTicks(20 * 5 * perkInstance.getSlot().value());
            }
        }
    }
}
