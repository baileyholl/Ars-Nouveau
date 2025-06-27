package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec2;

import java.util.Objects;

public class EmitterProperty extends BaseProperty<EmitterProperty> {

    public static final MapCodec<EmitterProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ANCodecs.VEC2.fieldOf("rotation").forGetter(i -> i.rotation),
            Codec.INT.fieldOf("age").forGetter(i -> i.age)
    ).apply(instance, EmitterProperty::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EmitterProperty> STREAM_CODEC = StreamCodec.composite(
            ANCodecs.VEC2_STREAM,
            i -> i.rotation,
            ByteBufCodecs.INT,
            i -> i.age,
            EmitterProperty::new
    );

    public Vec2 rotation;
    public int age;

    public EmitterProperty(Vec2 rotation, int age) {
        this.rotation = rotation;
        this.age = age;
    }

    @Override
    public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public IPropertyType<EmitterProperty> getType() {
        return ParticlePropertyRegistry.EMITTER_PROPERTY.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EmitterProperty that = (EmitterProperty) o;
        return Float.compare(rotation.x, that.rotation.x) == 0 && Float.compare(rotation.y, that.rotation.y) == 0 && age == that.age;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rotation.x, rotation.y, age);
    }
}
