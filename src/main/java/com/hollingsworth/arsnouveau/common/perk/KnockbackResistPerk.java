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

public class KnockbackResistPerk extends Perk {

    public static final KnockbackResistPerk INSTANCE = new KnockbackResistPerk(ArsNouveau.prefix("thread_amethyst_golem"));

    public KnockbackResistPerk(ResourceLocation key) {
        super(key);
    }


    @Override
    public @NotNull ItemAttributeModifiers applyAttributeModifiers(ItemAttributeModifiers modifiers, ItemStack stack, int slotValue) {
        return modifiers.withModifierAdded(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(INSTANCE.getRegistryName(), .15 * slotValue, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ARMOR);
    }

    @Override
    public String getLangName() {
        return "The Amethyst Golem";
    }

    @Override
    public String getLangDescription() {
        return "Grants 15%% knockback resistance per level.";
    }
}
