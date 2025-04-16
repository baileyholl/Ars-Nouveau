package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface IParticleProperty {

    Component getName();

    ResourceLocation getIconLocation();


    ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height);

}
