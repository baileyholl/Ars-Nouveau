package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

public class FeatherPerk extends Perk {

    public static final FeatherPerk INSTANCE = new FeatherPerk(ArsNouveau.prefix("thread_feather"));

    public FeatherPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public @NotNull ItemAttributeModifiers applyAttributeModifiers(ItemAttributeModifiers modifiers, ItemStack stack, int slotValue, EquipmentSlotGroup equipmentSlotGroup) {
        return modifiers.withModifierAdded(PerkAttributes.FEATHER, new AttributeModifier(INSTANCE.getRegistryName(), 0.2 * slotValue, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
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
