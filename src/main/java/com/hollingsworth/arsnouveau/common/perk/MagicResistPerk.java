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

public class MagicResistPerk extends Perk {

    public static final MagicResistPerk INSTANCE = new MagicResistPerk(ArsNouveau.prefix( "thread_warding"));

    public MagicResistPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        return attributeBuilder().put(PerkAttributes.WARDING.get(), new AttributeModifier(INSTANCE.getRegistryName(), 2 * slotValue, AttributeModifier.Operation.ADD_VALUE)).build();
    }

    @Override
    public String getLangName() {
        return "Warding";
    }

    @Override
    public String getLangDescription() {
        return "Reduces the amount of magic damage taken by a flat amount each level.";
    }
}
