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

public class MagicCapacityPerk extends Perk {

    public static final MagicCapacityPerk INSTANCE = new MagicCapacityPerk(ArsNouveau.prefix("thread_magic_capacity"));

    public MagicCapacityPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public @NotNull ItemAttributeModifiers applyAttributeModifiers(ItemAttributeModifiers modifiers, ItemStack stack, int slotValue, EquipmentSlotGroup equipmentSlotGroup) {
        return modifiers.withModifierAdded(PerkAttributes.MAX_MANA, new AttributeModifier(INSTANCE.getRegistryName(), 0.1 * slotValue, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), equipmentSlotGroup);
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
