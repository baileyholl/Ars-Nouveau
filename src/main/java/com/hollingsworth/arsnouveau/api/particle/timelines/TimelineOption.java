package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.api.particle.configurations.IParticleMotionType;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.Property;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record TimelineOption(ResourceLocation id,
                             TimelineEntryData entry,
                             ImmutableList<IParticleMotionType<?>> options,
                             List<Property> properties) {

    public TimelineOption(ResourceLocation id,
                          TimelineEntryData entry,
                          ImmutableList<IParticleMotionType<?>> options) {
        this(id, entry, options, new ArrayList<>());
    }

    public TimelineOption withProperty(Property property){
        properties.add(property);
        return this;
    }

    public Component name(){
        return Component.translatable(id.getNamespace() + ".timeline." + id.getPath());
    }

    public Component description(){
        return Component.translatable(id.getNamespace() + ".timeline." + id.getPath() + ".description");
    }
}
