package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.common.lib.PotionEffectTags;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

public class PotionEffectTagProvider extends TagsProvider<MobEffect> {
    public PotionEffectTagProvider(DataGenerator pGenerator, String modId, @org.jetbrains.annotations.Nullable net.minecraftforge.common.data.ExistingFileHelper existingFileHelper) {
        super(pGenerator, Registry.MOB_EFFECT, modId, existingFileHelper);
    }

    protected void addTags() {
        this.tag(PotionEffectTags.UNSTABLE_GIFTS).add(
                MobEffects.SLOW_FALLING, MobEffects.NIGHT_VISION, MobEffects.CONDUIT_POWER, MobEffects.ABSORPTION, MobEffects.DAMAGE_BOOST,
                MobEffects.FIRE_RESISTANCE, MobEffects.DIG_SPEED, MobEffects.MOVEMENT_SPEED, MobEffects.REGENERATION, MobEffects.DAMAGE_RESISTANCE
        );
    }
}
