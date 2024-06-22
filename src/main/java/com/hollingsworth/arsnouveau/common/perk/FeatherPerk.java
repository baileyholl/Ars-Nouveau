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

public class FeatherPerk extends Perk {

    public static final FeatherPerk INSTANCE = new FeatherPerk(ArsNouveau.prefix( "thread_feather"));
    public static final UUID PERK_UUID = UUID.fromString("3923ee66-756d-4b1d-b216-bb9338b0315b");

    public FeatherPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        return attributeBuilder().put(PerkAttributes.FEATHER.get(), new AttributeModifier(PERK_UUID, "Feather", 0.2 * slotValue, AttributeModifier.Operation.ADDITION)).build();
    }

    @Override
    public String getLangName() {
        return "Feather";
    }

    @Override
    public String getLangDescription() {
        return "Decreases the amount of fall damage taken by a percentage. Stacks with Feather Enchantments.";
    }
}
