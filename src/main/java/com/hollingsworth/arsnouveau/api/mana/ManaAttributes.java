package com.hollingsworth.arsnouveau.api.mana;

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
public class ManaAttributes {
    public static final HashMap<RegistryObject<Attribute>, UUID> UUIDS = new HashMap<>();
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES,ArsNouveau.MODID);


    public static final RegistryObject<Attribute> MANA_REGEN = registerAttribute("mana_regen",(id) -> new RangedAttribute(id, 0.0D, 0.0D, 2000.0D).setSyncable(true));
    public static final RegistryObject<Attribute> MAX_MANA = registerAttribute("max_mana",(id) -> new RangedAttribute(id, 0.0D, 0.0D, 10000.0D).setSyncable(true));;

    public static RegistryObject<Attribute> registerAttribute(String name, Function<String, Attribute> attribute) {
        RegistryObject<Attribute> registryObject = ATTRIBUTES.register(name, () -> attribute.apply("attribute.name.ars_nouveau." + name));
        UUIDS.put(registryObject, UUID.randomUUID());
        return registryObject;
    }

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().stream().filter(e -> e == EntityType.PLAYER).forEach(e -> {
            event.add(e, MAX_MANA.get());
            event.add(e, MANA_REGEN.get());
        });
    }

}