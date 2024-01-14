package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.perk.IEffectResolvePerk;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class VampiricPerk extends Perk implements IEffectResolvePerk {
    public static VampiricPerk INSTANCE = new VampiricPerk(new ResourceLocation(ArsNouveau.MODID, "thread_life_drain"));
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
    public void onPreResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, AbstractEffect effect, PerkInstance perkInstance) {

    }

    @Override
    public void onPostResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver, AbstractEffect effect, PerkInstance perkInstance) {

    }

    @Override
    public void onPostSpellDamageEvent(SpellDamageEvent.Post event, PerkInstance perkInstance) {
        float healAmount = event.damage * (0.2f * PerkUtil.countForPerk(perkInstance.getPerk(), event.caster));
        event.caster.heal(healAmount);
    }
}
