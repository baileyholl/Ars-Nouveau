package com.hollingsworth.arsnouveau.client.gui.documentation;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.nuggets.client.gui.BaseScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class IndexScreen extends BaseScreen {

    public static ResourceLocation background = ArsNouveau.prefix( "textures/gui/spell_book_template.png");

    public IndexScreen() {
        super(Component.empty(), 290, 194, background);
    }

    public static void open() {
        Minecraft.getInstance().setScreen(new IndexScreen());
    }
}
