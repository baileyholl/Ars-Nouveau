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

public class PotionDurationPerk extends Perk {

    public static final PotionDurationPerk INSTANCE = new PotionDurationPerk(ArsNouveau.prefix("thread_wixie"));

    public PotionDurationPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public @NotNull ItemAttributeModifiers applyAttributeModifiers(ItemAttributeModifiers modifiers, ItemStack stack, int slotValue, EquipmentSlotGroup equipmentSlotGroup) {
        return modifiers.withModifierAdded(PerkAttributes.WIXIE, new AttributeModifier(INSTANCE.getRegistryName(), 0.15 * slotValue, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), equipmentSlotGroup);
    }

    @Override
    public String getLangName() {
        return "The Wixie";
    }

    @Override
    public String getLangDescription() {
        return "Increases the duration of potions by 15%% each level.";
    }
}
