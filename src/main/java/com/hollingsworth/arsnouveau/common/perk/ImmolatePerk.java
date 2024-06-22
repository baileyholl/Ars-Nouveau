package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.perk.IEffectResolvePerk;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.perk.PerkInstance;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class ImmolatePerk extends Perk implements IEffectResolvePerk {
    public static final ImmolatePerk INSTANCE = new ImmolatePerk(ArsNouveau.prefix( "thread_immolation"));

    public ImmolatePerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public void onSpellCast(SpellCastEvent spellCastEvent, PerkInstance perkInstance) {
        LivingEntity caster = spellCastEvent.context.getUnwrappedCaster();
        int level = PerkUtil.countForPerk(INSTANCE, caster);

        if(caster.isOnFire()){
            caster.extinguishFire();
            caster.addEffect(new MobEffectInstance(ModPotions.IMMOLATE_EFFECT.get(), 20 * 5, level - 1));
        }
        if(caster.level.getBlockState(caster.blockPosition()).is(BlockTags.FIRE)){
            caster.level.removeBlock(caster.blockPosition(), false);
            caster.addEffect(new MobEffectInstance(ModPotions.IMMOLATE_EFFECT.get(), 20 * 5, level - 1));
        }
    }

    @Override
    public String getLangName() {
        return "Immolation";
    }

    @Override
    public String getLangDescription() {
        return "If the user is on fire or standing in fire, the fire will be extinguished when the user casts a spell and will be granted the Immolation potion effect. Fire spells will deal additional damage and last longer for each level of Immolation.";
    }
}
