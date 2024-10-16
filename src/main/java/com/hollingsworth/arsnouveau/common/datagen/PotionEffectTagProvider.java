package com.hollingsworth.arsnouveau.common.datagen;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.lib.PotionEffectTags;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PotionEffectTagProvider extends IntrinsicHolderTagsProvider<MobEffect> {

    public PotionEffectTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, Registries.MOB_EFFECT, pProvider, ef -> BuiltInRegistries.MOB_EFFECT.getResourceKey(ef).get(), ArsNouveau.MODID, existingFileHelper);
    }


    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(PotionEffectTags.UNSTABLE_GIFTS).add(
                MobEffects.SLOW_FALLING.value(), MobEffects.NIGHT_VISION.value(), MobEffects.CONDUIT_POWER.value(), MobEffects.ABSORPTION.value(), MobEffects.DAMAGE_BOOST.value(),
                MobEffects.FIRE_RESISTANCE.value(), MobEffects.DIG_SPEED.value(), MobEffects.MOVEMENT_SPEED.value(), MobEffects.REGENERATION.value(), MobEffects.DAMAGE_RESISTANCE.value()
        );
        //placeholder to not leave the tag empty
        this.tag(PotionEffectTags.DISPEL_DENY).add(ModPotions.SUMMONING_SICKNESS_EFFECT.get());
        this.tag(PotionEffectTags.TO_SYNC).add(ModPotions.SOAKED_EFFECT.get());
    }
}
