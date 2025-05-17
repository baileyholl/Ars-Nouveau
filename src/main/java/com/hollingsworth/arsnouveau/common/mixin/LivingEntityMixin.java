package com.hollingsworth.arsnouveau.common.mixin;

import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {
    @Shadow public abstract double getAttributeValue(Holder<Attribute> attribute);

    @Shadow @Nullable public abstract AttributeInstance getAttribute(Holder<Attribute> attribute);

    @Override
    public double wrapGravity(Operation<Double> original) {
        var g = super.wrapGravity(original);
        var weight = this.getAttribute(PerkAttributes.WEIGHT);
        if (weight != null) {
            g *= weight.getValue();
        }

        return g;
    }
}
