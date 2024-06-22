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

public class MagicCapacityPerk extends Perk {

    public static final MagicCapacityPerk INSTANCE = new MagicCapacityPerk(ArsNouveau.prefix( "thread_magic_capacity"));
    public static final UUID PERK_UUID = UUID.fromString("42ebba5f-7843-4da9-9ad4-e9ca37120602");

    public MagicCapacityPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        return attributeBuilder().put(PerkAttributes.MAX_MANA.get(), new AttributeModifier(PERK_UUID, "MagicCapacity",  0.1 * slotValue, AttributeModifier.Operation.MULTIPLY_TOTAL)).build();
    }

    @Override
    public String getLangName() {
        return "Magic Capacity";
    }

    @Override
    public String getLangDescription() {
        return "Increases the users maximum mana by 10%% per level.";
    }
}
