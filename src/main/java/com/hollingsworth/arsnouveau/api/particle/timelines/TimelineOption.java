package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleMotionType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record TimelineOption(ResourceLocation id, TimelineEntryData entry, ImmutableList<IParticleMotionType<?>> options) {

    public Component name(){
        return Component.translatable(id.getNamespace() + ".timeline." + id.getPath());
    }

    public Component description(){
        return Component.translatable(id.getNamespace() + ".timeline." + id.getPath() + ".description");
    }
}
