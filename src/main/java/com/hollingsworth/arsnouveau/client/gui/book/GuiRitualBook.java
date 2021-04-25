package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.RitualButton;
import com.hollingsworth.arsnouveau.common.items.RitualBook;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSetRitual;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class GuiRitualBook extends BaseBook{
    public String ritualDesc = "";
    public Minecraft mc;
    ArsNouveauAPI api;
    List<AbstractRitual> unlockedRituals;
    AbstractRitual selectedRitual;
    boolean isMainhand;

    public GuiRitualBook(ArsNouveauAPI api, List<String> unlockedRitualIds, boolean isMainhand){
        this.api = api;
        unlockedRituals = new ArrayList<>();
        for(String s : unlockedRitualIds){
            unlockedRituals.add(api.getRitual(s));
        }
        this.isMainhand = isMainhand;
    }

    @Override
    public void init() {
        super.init();
        addUnlockedRituals();
        addButton(new GuiImageButton(bookRight - 100, bookBottom - 40, 0,0,46, 18, 46, 18, "textures/gui/create_button.png", (n)->{
            Networking.INSTANCE.sendToServer(new PacketSetRitual(selectedRitual.getID(), mc.player.getMainHandItem().getItem() instanceof RitualBook));
        }));
        this.mc = this.minecraft;
    }

    public void addUnlockedRituals(){

        for(int i = 0; i < unlockedRituals.size(); i++){
            AbstractRitual ritual = unlockedRituals.get(i);
            addButton(new RitualButton(this, bookLeft + 15, bookTop +15 +15*i,
                    b->{
                        selectedRitual = ((RitualButton)b).ritual;
                        ritualDesc = selectedRitual.getDescription();
                    },
                    ritual));

        }
    }

    public static void open(ArsNouveauAPI api,  List<String> ritualIds, boolean isMainhand){
        Minecraft.getInstance().setScreen(new GuiRitualBook(api, ritualIds, isMainhand));
    }

    @Override
    public void drawBackgroundElements(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(stack, mouseX, mouseY, partialTicks);
        ITextProperties itextproperties = new StringTextComponent(ritualDesc);
        minecraft.font.drawWordWrap(itextproperties, bookLeft +155, bookTop +20, 120, 0);
        minecraft.font.draw(stack,"Select", 197, 159,  0);

        //10053171
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);

    }
}
