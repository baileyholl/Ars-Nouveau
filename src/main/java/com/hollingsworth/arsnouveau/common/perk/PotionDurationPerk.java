package com.hollingsworth.arsnouveau.common.perk;

import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

public class PotionDurationPerk extends Perk {

    public static final PotionDurationPerk INSTANCE = new PotionDurationPerk(ArsNouveau.prefix( "thread_wixie"));

    public PotionDurationPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        return attributeBuilder().put(PerkAttributes.WIXIE.get(), new AttributeModifier(INSTANCE.getRegistryName(), 0.15 * slotValue, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)).build();
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
