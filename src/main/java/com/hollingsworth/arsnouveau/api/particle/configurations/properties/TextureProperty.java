package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.configurations.ParticleConfigWidgetProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;

public class TextureProperty implements IParticleProperty{

    public Consumer<ResourceLocation> onTextureChanged;

    public TextureProperty(Consumer<ResourceLocation> onTextureChanged){
        this.onTextureChanged = onTextureChanged;
    }

    @Override
    public Component getName() {
        return Component.translatable("ars_nouveau.particle.property.texture");
    }

    @Override
    public ResourceLocation getIconLocation() {
        return null;
    }


    @Override
    public ParticleConfigWidgetProvider buildWidgets(int x, int y, int width, int height) {
        return new ParticleConfigWidgetProvider(x, y, width, height) {
            @Override
            public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
                DocClientUtils.drawHeader(getName(), graphics, x, y, width, mouseX, mouseY, partialTicks);
            }

            @Override
            public void addWidgets(List<AbstractWidget> widgets) {

            }
        };
    }
}
