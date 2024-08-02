package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

public class ToughnessPerk extends Perk {

    public static final ToughnessPerk INSTANCE = new ToughnessPerk(ArsNouveau.prefix( "thread_toughness"));

    public ToughnessPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public @NotNull ItemAttributeModifiers applyAttributeModifiers(ItemAttributeModifiers modifiers, ItemStack stack, int slotValue, EquipmentSlotGroup equipmentSlotGroup) {
        return modifiers.withModifierAdded(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(INSTANCE.getRegistryName(), 1F * slotValue, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
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
