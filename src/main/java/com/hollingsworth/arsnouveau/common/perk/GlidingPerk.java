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

public class GlidingPerk extends Perk {

    public static final GlidingPerk INSTANCE = new GlidingPerk(new ResourceLocation(ArsNouveau.MODID, "thread_gliding"));
    public static final UUID PERK_UUID = UUID.fromString("556fd264-22f2-4454-85b1-19070179f09a");

    public GlidingPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        return attributeBuilder().put(PerkAttributes.GLIDING.get(), new AttributeModifier(PERK_UUID, "Gliding", 1 * slotValue, AttributeModifier.Operation.ADDITION)).build();
    }

    @Override
    public int getCountCap() {
        return 1;
    }

    @Override
    public String getLangName() {
        return "Gliding";
    }

    @Override
    public String getLangDescription() {
        return "Allows you to glide as if you are wearing an elytra.";
    }
}
