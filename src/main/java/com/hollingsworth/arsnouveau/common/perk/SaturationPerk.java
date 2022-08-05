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

import java.util.UUID;

public class SaturationPerk extends Perk {

    public static final SaturationPerk INSTANCE = new SaturationPerk(new ResourceLocation(ArsNouveau.MODID, "saturation_perk"));
    public static final UUID PERK_UUID = UUID.fromString("fe329876-34b0-4349-a60a-6215ca44bd4e");

    public SaturationPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int count) {
        return attributeBuilder().put(PerkAttributes.WHIRLIESPRIG.get(), new AttributeModifier(PERK_UUID, "SaturationPerk", 0.33 * count, AttributeModifier.Operation.ADDITION)).build();
    }

    @Override
    public int getCountCap() {
        return 3;
    }
}
