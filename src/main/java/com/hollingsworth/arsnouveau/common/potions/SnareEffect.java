package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SnareEffect extends MobEffect {
    public static final ResourceLocation SNARE_ATTR = ArsNouveau.prefix("snare_modifier");

    public SnareEffect() {
        super(MobEffectCategory.HARMFUL, 2039587);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, SNARE_ATTR, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

}
