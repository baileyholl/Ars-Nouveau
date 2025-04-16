package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.api.particle.configurations.IConfigurableParticle;
import com.hollingsworth.arsnouveau.api.particle.configurations.IConfigurableParticleType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public record TimelineOption(Supplier<IConfigurableParticle> getSelected, Consumer<IConfigurableParticle> setSelected, ImmutableList<IConfigurableParticleType<?>> options) {
}
