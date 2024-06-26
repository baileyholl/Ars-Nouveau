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

public class MagicCapacityPerk extends Perk {

    public static final MagicCapacityPerk INSTANCE = new MagicCapacityPerk(ArsNouveau.prefix( "thread_magic_capacity"));

    public MagicCapacityPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        return attributeBuilder().put(PerkAttributes.MAX_MANA.get(), new AttributeModifier(INSTANCE.getRegistryName(),  0.1 * slotValue, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)).build();
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
