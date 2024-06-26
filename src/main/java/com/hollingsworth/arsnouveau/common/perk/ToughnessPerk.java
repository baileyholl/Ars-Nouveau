package com.hollingsworth.arsnouveau.common.perk;

import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

public class ToughnessPerk extends Perk {

    public static final ToughnessPerk INSTANCE = new ToughnessPerk(ArsNouveau.prefix( "thread_toughness"));

    public ToughnessPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        return attributeBuilder().put(Attributes.ARMOR_TOUGHNESS.value(), new AttributeModifier(INSTANCE.getRegistryName(), 1 * slotValue, AttributeModifier.Operation.ADD_VALUE)).build();
    }

    @Override
    public String getLangName() {
        return "Toughness";
    }

    @Override
    public String getLangDescription() {
        return "Grants an additional point of toughness each level.";
    }
}
