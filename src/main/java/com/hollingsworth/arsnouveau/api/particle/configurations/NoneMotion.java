package com.hollingsworth.arsnouveau.api.particle.configurations;

import com.hollingsworth.arsnouveau.api.particle.PropertyParticleOptions;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.PropMap;
import com.hollingsworth.arsnouveau.api.registry.ParticleMotionRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public class NoneMotion extends ParticleMotion {
    public static MapCodec<NoneMotion> CODEC = buildPropCodec(NoneMotion::new);

    public static StreamCodec<RegistryFriendlyByteBuf, NoneMotion> STREAM = buildStreamCodec(NoneMotion::new);
    public NoneMotion(PropMap propertyMap) {
        super(propertyMap);
    }

    public NoneMotion() {
        this(new PropMap());
    }

    @Override
    public void tick(PropertyParticleOptions particleOptions, Level level, double x, double y, double z, double prevX, double prevY, double prevZ) {

    }

    @Override
    public IParticleMotionType<NoneMotion> getType() {
        return ParticleMotionRegistry.NONE_TYPE.get();
    }
}
