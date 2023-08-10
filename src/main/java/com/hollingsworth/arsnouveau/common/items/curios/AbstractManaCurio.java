package com.hollingsworth.arsnouveau.common.items.curios;

import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

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

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> attributes = super.getAttributeModifiers(slotContext, uuid, stack);
        attributes.put(PerkAttributes.MAX_MANA.get(), new AttributeModifier(uuid, "max_mana_modifier_curio" ,this.getMaxManaBoost(stack), AttributeModifier.Operation.ADDITION) );
        attributes.put(PerkAttributes.MANA_REGEN_BONUS.get(), new AttributeModifier(uuid, "mana_regen_modifier_curio" ,this.getManaRegenBonus(stack), AttributeModifier.Operation.ADDITION) );
        return attributes;
    }

}
