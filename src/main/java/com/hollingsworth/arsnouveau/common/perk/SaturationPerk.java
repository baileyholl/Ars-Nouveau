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

public class SaturationPerk extends Perk {

    public static final SaturationPerk INSTANCE = new SaturationPerk(ArsNouveau.prefix("thread_whirlisprig"));

    public SaturationPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public @NotNull ItemAttributeModifiers applyAttributeModifiers(ItemAttributeModifiers modifiers, ItemStack stack, int slotValue, EquipmentSlotGroup equipmentSlotGroup) {
        double val = switch (slotValue) {
            case 0 -> 0.0;
            case 1 -> 0.3;
            case 2 -> 0.6;
            default -> 1.0;
        };
        return modifiers.withModifierAdded(PerkAttributes.WHIRLIESPRIG, new AttributeModifier(INSTANCE.getRegistryName(), val, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), equipmentSlotGroup);

    }

    @Override
    public String getLangName() {
        return "The Whirlisprig";
    }

    @Override
    public String getLangDescription() {
        return "Increases the saturation of consumed food by 30%% each level.";
    }
}
