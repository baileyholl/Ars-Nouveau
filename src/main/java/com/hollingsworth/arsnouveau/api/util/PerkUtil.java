package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.perk.IPerkProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class PerkUtil {

    public static @Nullable IPerkHolder<ItemStack> getPerkHolder(ItemStack stack){
        IPerkProvider<ItemStack> holder = ArsNouveauAPI.getInstance().getPerkProvider(stack.getItem());
        return holder == null ? null : holder.getPerkHolder(stack);
    }

    public static double perkValue(LivingEntity entity, Attribute attribute){
        AttributeInstance instance = entity.getAttribute(attribute);
        return instance == null ? attribute.getDefaultValue() : instance.getValue();
    }

    public static double valueOrZero(LivingEntity entity, Attribute attribute){
        return entity.getAttribute(attribute) == null ? 0 : entity.getAttributeValue(attribute);
    }

}
