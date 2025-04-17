package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.api.particle.configurations.IConfigurableParticle;
import com.hollingsworth.arsnouveau.api.particle.configurations.IConfigurableParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public record TimelineOption(ResourceLocation id, Supplier<IConfigurableParticle> getSelected, Consumer<IConfigurableParticle> setSelected, ImmutableList<IConfigurableParticleType<?>> options) {

    public Component name(){
        return Component.translatable(id.getNamespace() + ".timeline." + id.getPath());
    }

    public Component description(){
        return Component.translatable(id.getNamespace() + ".timeline." + id.getPath() + ".description");
    }
}
