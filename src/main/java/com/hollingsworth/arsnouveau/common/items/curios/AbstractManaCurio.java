package com.hollingsworth.arsnouveau.common.items.curios;

import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.item.ArsNouveauCurio;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

public abstract class AbstractManaCurio extends ArsNouveauCurio {
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
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributes = super.getAttributeModifiers(slotContext, id, stack);
        attributes.put(PerkAttributes.MAX_MANA, new AttributeModifier(id, this.getMaxManaBoost(stack), AttributeModifier.Operation.ADD_VALUE));
        attributes.put(PerkAttributes.MANA_REGEN_BONUS, new AttributeModifier(id, this.getManaRegenBonus(stack), AttributeModifier.Operation.ADD_VALUE));
        return attributes;
    }
}
