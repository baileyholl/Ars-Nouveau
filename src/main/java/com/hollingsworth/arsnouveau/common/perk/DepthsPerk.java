package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.NotNull;

public class DepthsPerk extends Perk {

    public static DepthsPerk INSTANCE = new DepthsPerk(ArsNouveau.prefix("thread_depths"));

    public DepthsPerk(ResourceLocation key) {
        super(key);
    }


    @Override
    public @NotNull ItemAttributeModifiers applyAttributeModifiers(ItemAttributeModifiers modifiers, ItemStack stack, int slotValue, EquipmentSlotGroup equipmentSlotGroup) {
        if (slotValue >= 3) {
            return modifiers.withModifierAdded(NeoForgeMod.SWIM_SPEED, new AttributeModifier(INSTANCE.getRegistryName(), 2.0, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
        }
        return modifiers;


    }

    @Override
    public String getLangDescription() {
        return "Greatly increases the amount of time you may breathe underwater by reducing the chance your air will decrease. If this perk is in slot 3 or higher, you will no longer lose air and your swim speed is greatly increased. Stacks with Respiration Enchantments.";
    }

    @Override
    public String getLangName() {
        return "Depths";
    }
}
