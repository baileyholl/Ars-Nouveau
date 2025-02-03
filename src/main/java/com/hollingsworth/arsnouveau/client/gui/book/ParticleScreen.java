package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.api.particle.ParticleTimeline;
import com.hollingsworth.arsnouveau.api.particle.configurations.BurstConfiguration;
import com.hollingsworth.arsnouveau.api.particle.configurations.TrailConfiguration;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.registry.ModParticles;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateParticleTimeline;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.InteractionHand;

public class ParticleScreen extends BaseBook {
    int slot;
    InteractionHand stackHand;
    ParticleTimeline timeline;
    public ParticleScreen(ParticleTimeline particleTimeline, int slot, InteractionHand stackHand) {
        this.slot = slot;
        this.stackHand = stackHand;
        this.timeline = particleTimeline;
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new GuiImageButton(bookLeft + 25, bookBottom - 30, 0, 0, 37, 12, 37, 12, "textures/gui/save_icon.png", this::onCreate));
        addRenderableWidget(new GuiImageButton(bookRight - 131, bookTop + 44, 0, 0, 48, 11, 48, 11,
                "textures/gui/color_icons/purple_color_icon.png", (_2) -> {
            this.timeline.onResolvingEffect = new BurstConfiguration(ModParticles.SNOW_TYPE.get());
        }));

        addRenderableWidget(new GuiImageButton(bookRight - 131, bookTop + 68, 0, 0, 48, 11, 48, 11,
                "textures/gui/color_icons/yellow_color_icon.png", (_2) -> {
            this.timeline.trailEffect = new TrailConfiguration(ModParticles.SNOW_TYPE.get());
        }));
    }

    public void onCreate(Button button){
        Networking.sendToServer(new PacketUpdateParticleTimeline(slot, timeline, this.stackHand == InteractionHand.MAIN_HAND));
    }
}
