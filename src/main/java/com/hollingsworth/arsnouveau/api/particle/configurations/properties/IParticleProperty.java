package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface IParticleProperty {

    ResourceLocation getId();

    default Component getName(){
        return Component.translatable(getId().getNamespace() + ".particle.property." + getId().getPath());
    }

    default ResourceLocation getIconLocation(){
        return ResourceLocation.fromNamespaceAndPath(getId().getNamespace(), "textures/gui/particle/" + getId().getPath() + ".png");
    }


    ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height);

}
