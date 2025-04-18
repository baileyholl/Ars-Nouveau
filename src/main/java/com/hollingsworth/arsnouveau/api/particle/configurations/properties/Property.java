package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public abstract class Property {

    public PropertyHolder propertyHolder;

    public Property(PropertyHolder propertyHolder) {
        this.propertyHolder = propertyHolder;
    }

    abstract ResourceLocation getId();

    public Component getName(){
        return Component.translatable(getId().getNamespace() + ".particle.property." + getId().getPath());
    }

    public ResourceLocation getIconLocation(){
        return ResourceLocation.fromNamespaceAndPath(getId().getNamespace(), "textures/gui/particle/" + getId().getPath() + ".png");
    }

    abstract public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height);

    public List<Property> subProperties(){
        return new ArrayList<>();
    }

}
