package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class DataSerializers {
    public static final DeferredRegister<EntityDataSerializer<?>> DS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, ArsNouveau.MODID);
    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<Vec3>> VEC = DS.register("vec3",
            () -> EntityDataSerializer.forValueType(
                    StreamCodec.of(
                            (pBuffer, pValue) -> {
                                pBuffer.writeDouble(pValue.x);
                                pBuffer.writeDouble(pValue.y);
                                pBuffer.writeDouble(pValue.z);
                            },
                            pBuffer -> new Vec3(pBuffer.readDouble(), pBuffer.readDouble(), pBuffer.readDouble())
                    )
            )
    );

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<SpellResolver>> SPELL_RESOLVER = DS.register("spell_resolver",
            () -> EntityDataSerializer.forValueType(
                    StreamCodec.of(
                            SpellResolver.STREAM,
                            SpellResolver.STREAM
                    )
            )
    );
}
