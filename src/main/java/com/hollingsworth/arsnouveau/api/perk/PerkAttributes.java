package com.hollingsworth.arsnouveau.api.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PerkAttributes {
    public static final HashMap<RegistryObject<Attribute>, UUID> UUIDS = new HashMap<>();
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, ArsNouveau.MODID);

    public static final RegistryObject<Attribute> WARDING = registerAttribute("ars_nouveau.perk.warding", (id) -> new RangedAttribute(id, 0.0D, 0.0D, 1024.0D).setSyncable(true), "07625fbb-f186-46c3-8b5f-989b747f29f8");
    public static final RegistryObject<Attribute> MANA_REGEN_BONUS = registerAttribute("ars_nouveau.perk.mana_regen", (id) -> new RangedAttribute(id, 0.0D, 0.0D, 2000.0D).setSyncable(true), "0c877091-ee4f-4eda-9868-4194d9a18833");

    public static final RegistryObject<Attribute> MAX_MANA = registerAttribute("ars_nouveau.perk.max_mana", (id) -> new RangedAttribute(id, 0.0D, 0.0D, 10000.0D).setSyncable(true), "22980b24-83e5-4683-a215-8997c4011389");

    @Deprecated
    public static final RegistryObject<Attribute> MAX_MANA_BONUS = MAX_MANA, FLAT_MANA_BONUS = MAX_MANA;

    public static final RegistryObject<Attribute> SPELL_DAMAGE_BONUS = registerAttribute("ars_nouveau.perk.spell_damage", (id) -> new RangedAttribute(id, 0.0D, 0.0D, 10000.0D).setSyncable(true), "50b50137-9c92-4e64-b350-6044e9e609de");
    public static final RegistryObject<Attribute> WHIRLIESPRIG = registerAttribute("ars_nouveau.perk.saturation", (id) -> new RangedAttribute(id, 1.0, 0.0D, 10000.0D).setSyncable(true), "152810f7-0d01-484e-a512-73fe70af3db7");
    public static final RegistryObject<Attribute> WIXIE = registerAttribute("ars_nouveau.perk.wixie", (id) -> new RangedAttribute(id, 1.0D, 0.0D, 1024.0D).setSyncable(true), "bae5d566-c9f6-4abf-9fe0-6ac140a34db1");

    public static final RegistryObject<Attribute> FEATHER = registerAttribute("ars_nouveau.perk.feather", (id) -> new RangedAttribute(id, 0.0D, 0.0D, 1.0D).setSyncable(true), "ee3a4090-c5f5-4a26-a9c2-69837237b35f");
    //public static final RegistryObject<Attribute> TOUGHNESS = registerAttribute("ars_nouveau.perk.toughness", (id) -> new RangedAttribute(id, 0.0D, 0.0D, 1024.0D).setSyncable(true), "eb1ccdaf-38e3-4a1a-a5fb-b0dc698157ff");

    public static RegistryObject<Attribute> registerAttribute(String name, Function<String, Attribute> attribute, String uuid) {
        return registerAttribute(name, attribute, UUID.fromString(uuid));
    }

    public static RegistryObject<Attribute> registerAttribute(String name, Function<String, Attribute> attribute, UUID uuid) {
        RegistryObject<Attribute> registryObject = ATTRIBUTES.register(name, () -> attribute.apply(name));
        UUIDS.put(registryObject, uuid);
        return registryObject;
    }

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().stream().filter(e -> e == EntityType.PLAYER).forEach(e -> {
            ATTRIBUTES.getEntries().forEach((v) -> {
                    event.add(e, v.get());
            });
        });
    }



}
