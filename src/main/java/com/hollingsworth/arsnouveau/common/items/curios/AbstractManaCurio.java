package com.hollingsworth.arsnouveau.common.items.curios;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public abstract class AbstractManaCurio extends ArsNouveauCurio implements IManaEquipment {
    public AbstractManaCurio() {
        super();
    }

    public int getMaxManaBoost(ItemStack i) {
        return 0;
    }

    public int getManaRegenBonus(ItemStack i) {
        return 0;
    }

    public static final ResourceLocation CURIOS_MANA = ArsNouveau.prefix("max_mana_modifier_curio");
    public static final ResourceLocation CURIOS_MANA_REGEN = ArsNouveau.prefix("mana_regen_modifier_curio");

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        ItemAttributeModifiers attributes = super.getDefaultAttributeModifiers(stack);
        // TODO: fix with curios slot group
        for(EquipmentSlotGroup group : EquipmentSlotGroup.values()) {
            attributes.withModifierAdded(PerkAttributes.MAX_MANA, new AttributeModifier(CURIOS_MANA, this.getMaxManaBoost(stack), AttributeModifier.Operation.ADD_VALUE), group);

            attributes.withModifierAdded(PerkAttributes.MANA_REGEN_BONUS, new AttributeModifier(CURIOS_MANA_REGEN, this.getManaRegenBonus(stack), AttributeModifier.Operation.ADD_VALUE), group);
        }
        return attributes;
    }
}
