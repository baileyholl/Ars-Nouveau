package com.hollingsworth.arsnouveau.common.potions;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SnareEffect extends MobEffect {

    public SnareEffect() {
        super(MobEffectCategory.HARMFUL, 2039587);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, "0dee8a21-f182-42c8-8361-1ad6186cac30", -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

}
