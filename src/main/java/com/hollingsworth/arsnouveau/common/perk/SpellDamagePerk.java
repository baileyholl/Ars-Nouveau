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

import java.util.UUID;

public class SpellDamagePerk extends Perk {

    public static final SpellDamagePerk INSTANCE = new SpellDamagePerk(ArsNouveau.prefix( "thread_spellpower"));
    public static final UUID PERK_UUID = UUID.fromString("8b96679e-29e2-4a53-9f44-85024d78c366");

    public SpellDamagePerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        return attributeBuilder().put(PerkAttributes.SPELL_DAMAGE_BONUS.get(), new AttributeModifier(PERK_UUID, "SpellDamagePerk", 2 * slotValue, AttributeModifier.Operation.ADDITION)).build();
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
