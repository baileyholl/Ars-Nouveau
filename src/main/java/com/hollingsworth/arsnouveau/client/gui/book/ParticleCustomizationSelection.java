package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.IParticleConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class ParticleCustomizationSelection extends BaseBook {

    public IParticleConfig selectedConfiguration;
    public ParticleOverviewScreen parent;

    public ParticleCustomizationSelection(IParticleConfig selectedConfiguration, ParticleOverviewScreen parent) {
        this.selectedConfiguration = selectedConfiguration;
        this.parent = parent;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        DocClientUtils.drawHeader(selectedConfiguration.getName(), graphics, bookLeft + LEFT_PAGE_OFFSET, bookTop + 20, ONE_PAGE_WIDTH, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if(button == 1){
            Minecraft.getInstance().setScreen(parent);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

}
