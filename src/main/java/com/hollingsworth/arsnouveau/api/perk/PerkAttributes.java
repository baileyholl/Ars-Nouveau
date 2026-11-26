package com.hollingsworth.arsnouveau.api.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

@EventBusSubscriber(modid = ArsNouveau.MODID, bus = EventBusSubscriber.Bus.MOD)
public class PerkAttributes {
    public static final HashMap<DeferredHolder<Attribute, Attribute>, UUID> UUIDS = new HashMap<DeferredHolder<Attribute, Attribute>, UUID>();
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, ArsNouveau.MODID);

    public static final DeferredHolder<Attribute, Attribute> WARDING = registerAttribute("ars_nouveau.perk.warding", (id) -> new RangedAttribute(id, 0.0D, 0.0D, 1024.0D).setSyncable(true), "07625fbb-f186-46c3-8b5f-989b747f29f8");
    public static final DeferredHolder<Attribute, Attribute> MANA_REGEN_BONUS = registerAttribute("ars_nouveau.perk.mana_regen", (id) -> new RangedAttribute(id, 0.0D, 0.0D, 2000.0D).setSyncable(true), "0c877091-ee4f-4eda-9868-4194d9a18833");

    public static final DeferredHolder<Attribute, Attribute> MAX_MANA = registerAttribute("ars_nouveau.perk.max_mana", (id) -> new RangedAttribute(id, 0.0D, 0.0D, 10000.0D).setSyncable(true), "22980b24-83e5-4683-a215-8997c4011389");

    public static final DeferredHolder<Attribute, Attribute> SPELL_DAMAGE_BONUS = registerAttribute("ars_nouveau.perk.spell_damage", (id) -> new RangedAttribute(id, 0.0D, 0.0D, 10000.0D).setSyncable(true), "50b50137-9c92-4e64-b350-6044e9e609de");
    public static final DeferredHolder<Attribute, Attribute> WHIRLIESPRIG = registerAttribute("ars_nouveau.perk.saturation", (id) -> new RangedAttribute(id, 1.0, 0.0D, 10000.0D).setSyncable(true), "152810f7-0d01-484e-a512-73fe70af3db7");
    public static final DeferredHolder<Attribute, Attribute> WIXIE = registerAttribute("ars_nouveau.perk.wixie", (id) -> new RangedAttribute(id, 1.0D, 0.0D, 1024.0D).setSyncable(true), "bae5d566-c9f6-4abf-9fe0-6ac140a34db1");
    public static final DeferredHolder<Attribute, Attribute> DRYGMY = registerAttribute("ars_nouveau.perk.drygmy", (id) -> new RangedAttribute(id, 0.0D, 0.0D, 1024.0D).setSyncable(true), "bae5d566-9fe0-c9f6-4abf-6ac140a34db1");

    public static final DeferredHolder<Attribute, Attribute> FEATHER = registerAttribute("ars_nouveau.perk.feather", (id) -> new RangedAttribute(id, 0.0D, 0.0D, 1.0D).setSyncable(true), "ee3a4090-c5f5-4a26-a9c2-69837237b35f");
    //public static final DeferredHolder<Attribute, Attribute> TOUGHNESS = registerAttribute("ars_nouveau.perk.toughness", (id) -> new RangedAttribute(id, 0.0D, 0.0D, 1024.0D).setSyncable(true), "eb1ccdaf-38e3-4a1a-a5fb-b0dc698157ff");

    public static final DeferredHolder<Attribute, Attribute> WEIGHT = registerAttribute("ars_nouveau.perk.weight", (id) -> new RangedAttribute(id, 1.0D, 0.0D, 100.0D).setSyncable(true), "24d1ae35-a7c7-4c85-9b56-c94de36faf91");
    public static final DeferredHolder<Attribute, Attribute> SUMMON_CAPACITY = registerAttribute("ars_nouveau.perk.summon_capacity", (id) -> new RangedAttribute(id, 0.0D, 0.0D, 1024.0D).setSyncable(true), "ee3a4090-1e6f-4f6a-8f3a-2e3b8f8c5d6e");

    public static DeferredHolder<Attribute, Attribute> registerAttribute(String name, Function<String, Attribute> attribute, String uuid) {
        return registerAttribute(name, attribute, UUID.fromString(uuid));
    }

    public static DeferredHolder<Attribute, Attribute> registerAttribute(String name, Function<String, Attribute> attribute, UUID uuid) {
        DeferredHolder<Attribute, Attribute> registryObject = ATTRIBUTES.register(name, () -> attribute.apply(name));
        UUIDS.put(registryObject, uuid);
        return registryObject;
    }

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().stream().filter(e -> e == EntityType.PLAYER).forEach(e -> {
            ATTRIBUTES.getEntries().forEach((v) -> {
                event.add(e, v);
            });
        });
    }


}
