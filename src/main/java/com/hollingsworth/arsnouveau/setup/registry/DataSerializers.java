package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> DS = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, ArsNouveau.MODID);
    public static final RegistryObject<EntityDataSerializer<Vec3>> VEC = DS.register("vec3", () -> EntityDataSerializer.simple((buffer, vec) -> {
        buffer.writeDouble(vec.x);
        buffer.writeDouble(vec.y);
        buffer.writeDouble(vec.z);
    }, buffer -> new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble())));

}
