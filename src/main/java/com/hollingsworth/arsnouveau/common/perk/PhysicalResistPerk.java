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

public class PhysicalResistPerk extends Perk {

    public static final PhysicalResistPerk INSTANCE = new PhysicalResistPerk(new ResourceLocation(ArsNouveau.MODID, "physical_resist_perk"));
    public static final UUID PERK_UUID = UUID.fromString("0608747b-de8f-48c9-a8b9-14ae2372cdde");

    public PhysicalResistPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int count) {
        return attributeBuilder().put(PerkAttributes.PROTECTION.get(), new AttributeModifier(PERK_UUID, "PhysicalResist", 2 * count, AttributeModifier.Operation.ADDITION)).build();
    }

    @Override
    public int getCountCap() {
        return 3;
    }
}
