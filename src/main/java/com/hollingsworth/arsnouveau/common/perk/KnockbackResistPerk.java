package com.hollingsworth.arsnouveau.common.perk;

import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class KnockbackResistPerk extends Perk {

    public static final KnockbackResistPerk INSTANCE = new KnockbackResistPerk(new ResourceLocation(ArsNouveau.MODID, "thread_amethyst_golem"));
    public static final UUID PERK_UUID = UUID.fromString("b1d84c5d-4c84-4626-b275-94698b08aae1");

    public KnockbackResistPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        return attributeBuilder().put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(PERK_UUID, "KnockbackPerk", .15 * slotValue, AttributeModifier.Operation.ADDITION)).build();
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
