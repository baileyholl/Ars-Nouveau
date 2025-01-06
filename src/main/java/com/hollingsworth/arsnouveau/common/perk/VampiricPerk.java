package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.perk.IEffectResolvePerk;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import net.minecraft.resources.ResourceLocation;

public class VampiricPerk extends Perk implements IEffectResolvePerk {
    public static VampiricPerk INSTANCE = new VampiricPerk(ArsNouveau.prefix( "thread_life_drain"));
    public VampiricPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public String getLangDescription() {
        return "Dealing damage with spells heals you for 20%% per level of the damage dealt.";
    }

    @Override
    public String getLangName() {
        return "Life Drain";
    }

    @Override
    public void onPostSpellDamageEvent(SpellDamageEvent.Post event, PerkInstance perkInstance) {
        float healAmount = event.damage * (0.2f * PerkUtil.countForPerk(perkInstance.getPerk(), event.caster));
        event.caster.heal(healAmount);
    }
}
