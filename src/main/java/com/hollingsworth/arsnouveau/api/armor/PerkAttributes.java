package com.hollingsworth.arsnouveau.api.armor;

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
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES,ArsNouveau.MODID);

    public static final RegistryObject<Attribute> WARDING = registerAttribute("warding",(id) -> new RangedAttribute(id, 0.0D, 0.0D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> REGEN_BONUS = registerAttribute("mana_regen",(id) -> new RangedAttribute(id, 0.0D, 0.0D, 2000.0D).setSyncable(true));
    public static final RegistryObject<Attribute> CAP_BONUS = registerAttribute("max_mana",(id) -> new RangedAttribute(id, 0.0D, 0.0D, 10000.0D).setSyncable(true));
    public static final RegistryObject<Attribute> SPELL_DAMAGE_BONUS = registerAttribute("spell_damage",(id) -> new RangedAttribute(id, 0.0D, 0.0D, 10.0D).setSyncable(true));
    public static final RegistryObject<Attribute> WHIRLIESPRIG = registerAttribute("saturation",(id) -> new RangedAttribute(id, 0.0D, 0.0D, 3.0D).setSyncable(true));
    public static final RegistryObject<Attribute> WIXIE = registerAttribute("wixie",(id) -> new RangedAttribute(id, 0.0D, 0.0D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> DEPTHS = registerAttribute("breathing",(id) -> new RangedAttribute(id, 0.0D, 0.0D, 3.0D).setSyncable(true));
    public static final RegistryObject<Attribute> REPAIRING = registerAttribute("repairing",(id) -> new RangedAttribute(id, 0.0D, 0.0D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> GLIDING = registerAttribute("gliding",(id) -> new RangedAttribute(id, 0.0D, 0.0D, 1.0D));
    public static final RegistryObject<Attribute> FIRE_RES = registerAttribute("fire_res",(id) -> new RangedAttribute(id, 0.0D, 0.0D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> FEATHER = registerAttribute("feather",(id) -> new RangedAttribute(id, 0.0D, 0.0D, 1024.0D).setSyncable(true));

    public static RegistryObject<Attribute> registerAttribute(String name, Function<String, Attribute> attribute) {
        RegistryObject<Attribute> registryObject = ATTRIBUTES.register(name, () -> attribute.apply(name));
        UUIDS.put(registryObject, UUID.randomUUID());
        return registryObject;
    }

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().stream().filter(e -> e == EntityType.PLAYER).forEach(e -> {
            //i'm not typing this so many times I'm lazy just foreach this amiright what could go wrong
            ATTRIBUTES.getEntries().forEach((v)->{
                event.add(e, v.get());
            });
        });
    }

}
