package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class BaseProperty {

    public PropertyHolder propertyHolder;

    public BaseProperty(PropertyHolder propertyHolder) {
        this.propertyHolder = propertyHolder;
    }

    abstract ResourceLocation getId();

    public Component getName(){
        return Component.translatable(getId().getNamespace() + ".particle.property." + getId().getPath());
    }

    abstract public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height);

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BaseProperty property && this.getId().equals(property.getId());
    }
}
