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

public class SpellDamagePerk extends Perk {

    public static final SpellDamagePerk INSTANCE = new SpellDamagePerk(ArsNouveau.prefix( "thread_spellpower"));

    public SpellDamagePerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        return attributeBuilder().put(PerkAttributes.SPELL_DAMAGE_BONUS.get(), new AttributeModifier(INSTANCE.getRegistryName(), 2 * slotValue, AttributeModifier.Operation.ADD_VALUE)).build();
    }

    @Override
    public String getLangName() {
        return "Spell Power";
    }

    @Override
    public String getLangDescription() {
        return "Grants an increasing amount of Spell Damage each level.";
    }
}
