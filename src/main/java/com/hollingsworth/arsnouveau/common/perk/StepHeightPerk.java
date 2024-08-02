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

import java.util.UUID;

public class StepHeightPerk extends Perk {

    public static final StepHeightPerk INSTANCE = new StepHeightPerk(ArsNouveau.prefix( "thread_high_step"));

    public static final UUID PERK_STEP_UUID = UUID.fromString("46937d0b-123c-4786-95b5-748afd50f398");

    public StepHeightPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public @NotNull ItemAttributeModifiers applyAttributeModifiers(ItemAttributeModifiers modifiers, ItemStack stack, int slotValue, EquipmentSlotGroup equipmentSlotGroup) {
        return modifiers.withModifierAdded(Attributes.STEP_HEIGHT, new AttributeModifier(INSTANCE.getRegistryName(), slotValue, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
    }

    @Override
    public String getLangName() {
        return "High Step";
    }

    @Override
    public String getLangDescription() {
        return "Increases step height by one for each level.";
    }
}
