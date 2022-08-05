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

public class JumpHeightPerk extends Perk {

    public static final JumpHeightPerk INSTANCE = new JumpHeightPerk(new ResourceLocation(ArsNouveau.MODID, "jump_height_perk"));
    public static final UUID PERK_UUID = UUID.fromString("e5f68a8c-589f-4dde-978d-b4c507a4485b");

    public JumpHeightPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int count) {
        return attributeBuilder().put(PerkAttributes.JUMP_HEIGHT.get(), new AttributeModifier(PERK_UUID, "JumpHeight", 1 * count, AttributeModifier.Operation.ADDITION)).build();
    }

    @Override
    public int getCountCap() {
        return 3;
    }
}
