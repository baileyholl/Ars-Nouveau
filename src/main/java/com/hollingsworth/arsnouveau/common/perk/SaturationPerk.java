package com.hollingsworth.arsnouveau.common.perk;

import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

public class SaturationPerk extends Perk {

    public static final SaturationPerk INSTANCE = new SaturationPerk(ArsNouveau.prefix( "thread_whirlisprig"));

    public SaturationPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        double val = 0.0;
        if(slotValue == 1)
            val = 0.3;
        if(slotValue == 2)
            val = 0.6;
        if(slotValue >= 3)
            val = 1.0;
        return attributeBuilder().put(PerkAttributes.WHIRLIESPRIG.get(), new AttributeModifier(INSTANCE.getRegistryName(), val, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)).build();
    }

    @Override
    public String getLangName() {
        return "The Whirlisprig";
    }

    @Override
    public String getLangDescription() {
        return "Increases the saturation of consumed food by 30%% each level.";
    }
}
