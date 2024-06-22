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

public class MagicResistPerk extends Perk {

    public static final MagicResistPerk INSTANCE = new MagicResistPerk(ArsNouveau.prefix( "thread_warding"));
    public static final UUID PERK_UUID = UUID.fromString("fc967b66-a432-44e1-93ec-e8ed583b47b3");

    public MagicResistPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        return attributeBuilder().put(PerkAttributes.WARDING.get(), new AttributeModifier(PERK_UUID, "MagicResist", 2 * slotValue, AttributeModifier.Operation.ADDITION)).build();
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
