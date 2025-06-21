package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import com.hollingsworth.arsnouveau.api.registry.ParticlePropertyRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public class WallProperty extends BaseProperty<WallProperty> {
    public int range;
    public int chance;
    public int numParticles;
    public Direction direction;

    public static MapCodec<WallProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("range").forGetter(i -> i.range),
            Codec.INT.fieldOf("chance").forGetter(i -> i.chance),
            Codec.INT.fieldOf("num_particles").forGetter(i -> i.numParticles),
            Direction.CODEC.fieldOf("direction").forGetter(i -> i.direction)
    ).apply(instance, WallProperty::new));

    public static StreamCodec<RegistryFriendlyByteBuf, WallProperty> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, (i) -> i.range,
            ByteBufCodecs.INT, (i) -> i.chance,
            ByteBufCodecs.INT, (i) -> i.numParticles,
            Direction.STREAM_CODEC, (i) -> i.direction,
            WallProperty::new);

    public WallProperty(int range, int chance, int numParticles, Direction direction) {
        this.range = range;
        this.direction = direction;
        this.chance = chance;
        this.numParticles = numParticles;
    }

    @Override
    public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public IPropertyType<WallProperty> getType() {
        return ParticlePropertyRegistry.WALL_PROPERTY.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WallProperty that = (WallProperty) o;
        return range == that.range && chance == that.chance && numParticles == that.numParticles && direction == that.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(range, chance, numParticles, direction);
    }
}
