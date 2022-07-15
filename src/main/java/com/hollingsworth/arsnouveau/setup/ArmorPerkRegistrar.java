package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.armor.perks.ArmorPerk;
import com.hollingsworth.arsnouveau.common.world.Deferred;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ArmorPerkRegistrar {

    //I'll fix this later boooooo
        public static final DeferredRegister<ArmorPerk> PERKS = DeferredRegister.create(ArmorPerk.class, ArsNouveau.MODID);

        public static final RegistryObject<ArmorPerk> STARBIE = PERKS.register("starbuncle_perk", () ->
                new ArmorPerk(new ResourceLocation("starbuncle_perk")).withAttributeModifier((stats, attributeMap, uuid, perk, level) -> {
                attributeMap.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, "Movement Modifier", perk.getBonus()));
        }));

}
