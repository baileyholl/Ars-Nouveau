package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.api.documentation.DocClientUtils;
import com.hollingsworth.arsnouveau.api.particle.timelines.ParticleTimeline;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateParticleTimeline;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;

public class ParticleOverviewScreen extends BaseBook {
    int slot;
    InteractionHand stackHand;
    ParticleTimeline timeline;

    public AbstractSpellPart selectedPart = MethodProjectile.INSTANCE;

    public ParticleOverviewScreen(ParticleTimeline particleTimeline, int slot, InteractionHand stackHand) {
        this.slot = slot;
        this.stackHand = stackHand;
        this.timeline = particleTimeline;
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new GuiImageButton(bookLeft + 25, bookBottom - 30, 0, 0, 37, 12, 37, 12, "textures/gui/save_icon.png", this::onCreate));

//
        addRenderableWidget(new ParticleOptionButton(bookLeft + LEFT_PAGE_OFFSET, bookTop + 36, (button) -> {

        }, Component.translatable(selectedPart.getLocaleName()), selectedPart.glyphItem.getDefaultInstance()));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        DocClientUtils.drawHeader(Component.literal("Spell Styles"), graphics, bookLeft + LEFT_PAGE_OFFSET, bookTop + 20, ONE_PAGE_WIDTH, mouseX, mouseY, partialTicks);
    }

    public void onCreate(Button button){
        Networking.sendToServer(new PacketUpdateParticleTimeline(slot, timeline, this.stackHand == InteractionHand.MAIN_HAND));
    }
}
