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

public class LootingPerk extends Perk {

    public static final LootingPerk INSTANCE = new LootingPerk(ArsNouveau.prefix("thread_drygmy"));

    public LootingPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public @NotNull ItemAttributeModifiers applyAttributeModifiers(ItemAttributeModifiers modifiers, ItemStack stack, int slotValue, EquipmentSlotGroup equipmentSlotGroup) {
        return modifiers.withModifierAdded(PerkAttributes.DRYGMY, new AttributeModifier(INSTANCE.getRegistryName(), slotValue, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
    }

    @Override
    public String getLangName() {
        return "The Drygmy";
    }

    @Override
    public String getLangDescription() {
        return "Grants an additional stack of looting.";
    }
}
